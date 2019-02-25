package de.mkammerer.demo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Our own implementation of user management.
 */
public class UserService implements UserDetailsService {
    private final Map<String, User> users = new HashMap<>();
    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "'not found");
        }

        return user;
    }

    public void addUser(String username, String password, String... roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String role : roles) {
            // Has to have the 'ROLE_' prefix, otherwise .hasRole in SecurityConfig won't work
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        String hash = passwordEncoder.encode(password);
        users.put(username, new User(username, hash, authorities));
    }
}
