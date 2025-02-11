# SiteMinder Tech Challenge

To run in local:

```
mvn spring-boot:run
```

The local URL for testing HTTP POSTs:

http://localhost:8080/mail

The deployment in Google App Engine:

https://sm-tech-challenge.ts.r.appspot.com/mail

POST body JSON example:
```json
{
    "recipients": ["christianrubiales@gmail.com", "christianrubiales@yahoo.com"],
    "cc": ["christianrubiales@gmail.com", "christianrubiales@yahoo.com"],
    "bcc": ["christianrubiales@gmail.com", "christianrubiales@yahoo.com"],
    "body": "Test 1"
}
```

Notes:
- The `recipients` array must contain at least one email address.
- Email addresses must be in valid format
- `cc` and `bcc` are not required
- `body` is also not required