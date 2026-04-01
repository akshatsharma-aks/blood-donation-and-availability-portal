package com.blooddonation.security;

import com.blooddonation.entity.User;
import com.blooddonation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Called automatically by Spring Security during login.
     * Loads a user by email (used as username).
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "No user found with email: " + email));

        // Ensure user is enabled
        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User account is disabled");
        }

        return user;
    }

    /**
     * Load user by ID (used when decoding JWT token)
     */
    @Transactional(readOnly = true)
    public User loadUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "No user found with id: " + id));
    }
}