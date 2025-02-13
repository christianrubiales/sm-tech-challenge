# SiteMinder Tech Challenge

To run in local:

```
mvn spring-boot:run
```

The local URL for testing HTTP POSTs:

http://localhost:8080/mail

The online deployment:

http://170.64.251.90:8080/mail

POST body JSON example:
```json
{
    "recipients": ["christianrubiales@gmail.com", "christianrubiales@yahoo.com"],
    "cc": ["christianrubiales@gmail.com", "christianrubiales@yahoo.com"],
    "bcc": ["christianrubiales@gmail.com", "christianrubiales@yahoo.com"],
    "body": "Test 1"
}
```

# Notes:
- The `recipients` array must contain at least one email address.
- Email addresses must be in valid format
- `cc` and `bcc` are not required
- `body` is also not required


This implementation uses the `Resend` API.

# Resend API

- Allowed sender is only `*@resend.dev`
- At least 1 recipient is required
- Recipient can only be `christianrubiales@yahoo.com` or `delivered@resend.dev`
(uses of other `*@resend.dev` addresses will be accepted but the mail will not be delivered
and only tagged as Sent)
- If both `christianrubiales@yahoo.com` and `*@resend.dev` are used,
mail will only be sent to `*@resend.dev`
- Subject can be blank
- Body content is required

References:

https://resend.com/docs/api-reference/introduction

https://resend.com/docs/api-reference/emails/send-email