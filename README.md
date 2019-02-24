This applicaton showcases a Spring Boot Service which is able to:

* issuing OAuth2 tokens, encoded in JWT format, using RSA signing
* verifying these tokens when protected endpoints are called
* return the RSA public key to the client so that it can validate the issued tokens
