package de.mkammerer.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer // Enables token verification
@EnableAuthorizationServer // Enables token creation
@EnableWebSecurity // Enables Spring Security
public class SecurityConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // Spring Security routes can be configured here
        http.authorizeRequests()
                .antMatchers("/test/unprotected").permitAll()
                .antMatchers("/test/user").hasRole("USER")
                .anyRequest().denyAll(); // Block everything which is not whitelisted
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Uses BCrypt by default, but can also read other hashes
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean // Uses the given users instead of letting Spring Security generate one
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // We have implemented our own user service
        UserService userService = new UserService(passwordEncoder);

        userService.addUser("user1", "password1", "USER");
        userService.addUser("user2", "password2", "USER");

        return userService;
    }
}
