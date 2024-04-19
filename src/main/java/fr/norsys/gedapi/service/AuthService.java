package fr.norsys.gedapi.service;

import fr.norsys.gedapi.model.User;
import fr.norsys.gedapi.request.LoginRequest;
import fr.norsys.gedapi.request.RegisterRequest;
import fr.norsys.gedapi.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserService userService;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final CustomUserDetailsService userDetailsService;

    public AuthenticationResponse register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Username and password must not be null or empty");
        }

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        int savedUserId = userService.registre(user);

        User savedUser = userService.findById(savedUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + savedUserId));
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        var jwt = jwtService.generateToken(userDetails);
        return AuthenticationResponse
                .builder()
                .token(jwt)
                .build();
    }
        public AuthenticationResponse login(LoginRequest request) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
            var jwt = jwtService.generateToken(user);
            User authenticatedUser = userService.findByUsername(request.getUsername());
            return AuthenticationResponse
                    .builder()
                    .token(jwt)
                    .userId(authenticatedUser.getId())
                    .build();
        }
    }