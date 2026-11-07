package com.example.booking.security;

import com.example.booking.entity.User;
import com.example.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring го вика ова при login и при проверка на токен
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Корисникот не постои"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword_hash(),
                // Spring очекува префикс ROLE_ за hasRole(...)
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}