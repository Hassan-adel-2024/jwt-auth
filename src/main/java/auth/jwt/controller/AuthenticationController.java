package auth.jwt.controller;

import auth.jwt.dto.AuthRequest;
import auth.jwt.dto.AuthResponse;
import auth.jwt.entity.AppUser;
import auth.jwt.service.JwtService;
import auth.jwt.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
         log.info("Attempting to authenticate user with email: {}", request.getEmail());

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        AppUser user = (AppUser) authentication.getPrincipal();
        log.info("User authenticated: {}", user.getEmail());

        String token = jwtService.generateToken(user);
        log.info("Generated token: {}", token);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
