package de.mkammerer.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/unprotected")
    public String unprotected() {
        return "unprotected";
    }

    @GetMapping("/user")
    public String user(OAuth2Authentication principal) {
        LOGGER.info("/test/user from {}", principal);
        return "Hello " + principal.getName();
    }

    @GetMapping("/denied")
    public String denied() {
        return "You should never be able to call this method!";
    }
}
