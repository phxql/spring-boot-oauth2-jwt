package de.mkammerer.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoApplicationTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;
    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Test
    public void test() {
        // Get a token
        OAuth2AccessToken token = acquireToken();
        LOGGER.info("Access token: {}", token.getValue());
        LOGGER.info("Expires: {}", token.getExpiration());
        LOGGER.info("Refresh token: {}", token.getRefreshToken());

        // Can be called without token
        String tokenKey = executeGet("/oauth/token_key");
        assertThat(tokenKey).contains("-----BEGIN PUBLIC KEY-----");

        // Can be called without token
        String unprotected = executeGet("/test/unprotected");
        assertThat(unprotected).isEqualTo("unprotected");

        // Must be called with token
        String user = executeAuthenticatedGet(token, "/test/user");
        assertThat(user).isEqualTo("Hello user1");

        String userUnauthorized = executeGet("/test/user");
        assertThat(userUnauthorized).contains("unauthorized");

        // Can not be called with token or without
        String denied = executeAuthenticatedGet(token, "/test/denied");
        assertThat(denied).contains("access_denied");

        String denied2 = executeGet("/test/denied");
        assertThat(denied2).contains("unauthorized");
    }

    private String executeGet(String url) {
        return restTemplate.getForEntity(url, String.class).getBody();
    }

    private String executeAuthenticatedGet(OAuth2AccessToken token, String path) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token.getValue());
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(path, HttpMethod.GET, request, String.class).getBody();
    }

    private OAuth2AccessToken acquireToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("username", "user1");
        map.add("password", "password1");

        return restTemplate.withBasicAuth(clientId, clientSecret).postForObject("/oauth/token", new HttpEntity<>(map, headers), OAuth2AccessToken.class);
    }
}