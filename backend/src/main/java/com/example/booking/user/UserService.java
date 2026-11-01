package com.example.booking.user;


import com.example.booking.exceptions.EmailAlreadyExistsException;
import com.example.booking.security.JwtService;
import com.example.booking.user.dto.AuthResponse;
import com.example.booking.user.dto.LoginRequest;
import com.example.booking.user.dto.RegisterRequest;
import com.example.booking.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public User register(RegisterRequest request){
        String email = request.email().trim().toLowerCase();
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException(email);
        }
        User user = new User();
        user.setFull_name(request.fullName());
        user.setPassword_hash(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setRole(Role.CUSTOMER);
        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmail(email).orElseThrow();
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, UserResponse.from(user));
    }
}
