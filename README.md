# Example project for OAuth2 with JWT tokens in Spring Boot 

This applicaton showcases a Spring Boot Service:

* issues OAuth2 tokens, encoded in JWT format, using RSA signing
* verifies these tokens when protected endpoints are called
* returns the RSA public key to the client so that it can validate the issued tokens
* uses roles to protect endpoints, some endpoints are world-readable
* implements a custom `UserDetailsService` for managing users
* tests the whole Spring Security stuff in an integration test

## curls

### Issue a token

```
curl id:secret@localhost:8080/oauth/token -d grant_type=password -d username=user2 -d password=password2 -d scope=foo
```

Store the issued `access_token` in the variable `ACCESS_TOKEN`.

### Call the protected endpoint

```
curl localhost:8080/test/user -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Download RSA public key for token verification

```
curl localhost:8080/oauth/token_key
```
