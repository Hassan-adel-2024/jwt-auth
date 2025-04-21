package auth.jwt.jwtconfig;

import auth.jwt.entity.AppUser;
import auth.jwt.service.JwtService;
import auth.jwt.service.impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                   @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Check if the Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract the JWT token from the header
        jwt = authHeader.substring(7); // Remove "Bearer "

        // 3. Extract the username (email) from the token
        userEmail = jwtService.extractUsername(jwt);

        // 4. Check if the user is not already authenticated
        if (userEmail != null &&
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Load user details from the DB (or wherever)
            var userDetails = customUserDetailsService.loadUserByUsername(userEmail);

            // 6. Validate the token
            if (jwtService.validateToken(jwt, (AppUser) userDetails)) {
                // 7. Create an authentication token and set it in the context
                var authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request)
                );

                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            filterChain.doFilter(request, response);
        }
    }

}
