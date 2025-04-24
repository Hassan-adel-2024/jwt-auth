package auth.jwt.controller;

import auth.jwt.dto.AuthRequest;
import auth.jwt.dto.AuthResponse;
import auth.jwt.dto.RefreshTokenDto;
import auth.jwt.entity.AppUser;
import auth.jwt.entity.BlackListToken;
import auth.jwt.service.JwtService;
import auth.jwt.service.impl.BlackListTokenService;
import auth.jwt.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final BlackListTokenService blackListTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
//         log.info("Attempting to authenticate user with email: {}", request.getEmail());

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        AppUser user = (AppUser) authentication.getPrincipal();
//        log.info("User authenticated: {}", user.getEmail());

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
//        log.info("Generated token: {}", token);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        try {
            String email = jwtService.extractUsername(refreshTokenDto.getRefreshToken());
            AppUser user =(AppUser) userDetailsService.loadUserByUsername(email);
            if(!(jwtService.validateToken(refreshTokenDto.getRefreshToken(), user))) {
                return ResponseEntity.status(401).body("Invalid refresh token");
            }
            String newAccessToken = jwtService.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshTokenDto.getRefreshToken()));
        }
        catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("authorization") String tokenHeader) {
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        String accessToken = tokenHeader.replace("Bearer ", "");
        blackListTokenService.blackListAToken(accessToken);
        return ResponseEntity.ok("Logout successful");

    }
}
