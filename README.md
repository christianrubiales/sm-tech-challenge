# SiteMinder Tech Challenge

## Running the application

To run in local:

1) Set the following environment variables:

```
MAILTRAP_API_TOKEN=
RESEND_API_TOKEN=
```

2) Run using maven:

```
mvn clean spring-boot:run
```

## Testing

The local URL for testing HTTP POSTs:

http://localhost:8080/mail

The online deployment:

http://170.64.251.90:8080/mail

POST body JSON example:
```json
{
    "from": "<email address>",
    "recipients": ["<email address>"],
    "cc": ["<email address>"],
    "bcc": ["<email address>"],
    "subject": "Test Subject",
    "body": "Test Body"
}
```

## Notes:
- The `recipients` array must contain at least one email address.
- Email addresses must be in valid format
- `cc` and `bcc` are not required
- `subject` is also not required
- `body` content is required


This implementation uses the `Mailtrap` and `Resend` APIs.

## Mailtrap API Constraints

- Allowed sender is only `*@demomailtrap.com`
- Recipient can only be `christianrubiales@yahoo.com` in `recipients`, `cc` and `bcc`
- Can't contain the same recipient in `recipients`, `cc` and `bcc`
- This limitation of `Mailtrap` can be used to force errors and the failover to `Resend API`
- `recipients` is required and must contain at least one address
- `cc` and `bcc` are not required
- `subject` is required and must not be blank
- `body` is required and must not be blank

References:

https://api-docs.mailtrap.io/docs/mailtrap-api-docs/67f1d70aeb62c-send-email-including-templates

## Resend API

- Allowed sender is only `*@resend.dev`
- At least 1 recipient is required
- Recipient can only be `christianrubiales@yahoo.com` or `delivered@resend.dev`
(uses of other `*@resend.dev` addresses will be accepted but the mail will not be `Delivered`
and only tagged as `Sent`)
- If both `christianrubiales@yahoo.com` and `*@resend.dev` are used,
mail will only be sent to `*@resend.dev`
- `subject` is required but can be blank
- `body` content is required

References:

https://resend.com/docs/api-reference/introduction

https://resend.com/docs/api-reference/emails/send-email

## Example Tests

1. No content in `body`, but `body` content is required by both `Mailtrap` and `Resend` -> Error:

```json
{
    "from": "onboarding@resend.dev",
    "recipients": ["christianrubiales@yahoo.com"],
    "cc": [],
    "bcc": [],
    "subject": "",
    "body": ""
}
```

2. Empty `subject`, required by `Mailtrap` but ok with `Resend` -> 
will fail with `Mailtrap` and will failover to `Resend`, will be sent by `Resend`:

```json
{
    "from": "onboarding@resend.dev",
    "recipients": ["christianrubiales@yahoo.com"],
    "cc": [],
    "bcc": [],
    "subject": "",
    "body": "Test"
}
```

3. All required fields set -> Mail will be delivered by `Mailtrap`

```json
{
    "from": "hello@demomailtrap.com",
    "recipients": ["christianrubiales@yahoo.com"],
    "cc": [],
    "bcc": [],
    "subject": "Test Subject",
    "body": "Test"
}
```

4. All required fields set, using `Resend` sender address -> 
Will fail with `Mailtrap`, mail will be delivered by `Resend`

```json
{
    "from": "onboarding@resend.dev",
    "recipients": ["christianrubiales@yahoo.com"],
    "cc": [],
    "bcc": [],
    "subject": "Test Subject",
    "body": "Test"
}
```

5. `Mailtrap` will only accept a single recipient in `recipients`, `cc`, and `bcc` ->
will encounter an error when using multiple recipients with `Mailtrap` but alright with `Resend`:

```json
{
    "from": "onboarding@resend.dev",
    "recipients": ["christianrubiales@yahoo.com","hello0@resend.dev"],
    "cc": ["hello1@resend.dev","hello2@resend.dev"],
    "bcc": ["hello3@resend.dev","hello4@resend.dev"],
    "subject": "Test Subject",
    "body": "Test"
}
```