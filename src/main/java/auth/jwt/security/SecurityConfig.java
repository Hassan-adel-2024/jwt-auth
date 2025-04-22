package auth.jwt.security;

import auth.jwt.jwtconfig.JwtAuthenticationFilter;
import auth.jwt.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Marks this class as a Spring configuration class
@Configuration
// Enables Spring Security's web security support
@EnableWebSecurity
// Generates a constructor with required arguments (final fields)
@RequiredArgsConstructor
public class SecurityConfig {

    // Service to load user-specific data for authentication
    private final CustomUserDetailsService userDetailsService;

    // Custom JWT filter to process authentication tokens
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // List of API endpoints that don't require authentication
    private static final String [] publicApi = {
            "/api/user/save",  // User registration endpoint
            "/api/auth/login",
            "api/auth/refresh",

    };

    // List of API endpoints that require authentication
    private static final String[] authApi = {
            "/api/role/save"  // Role management endpoint
            ,
            "/api/users"
    };

    // Defines the security filter chain configuration
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection (common for stateless APIs using JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization rules for HTTP requests
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to these endpoints
                        .requestMatchers(publicApi).permitAll()

                        // Require authentication for these endpoints
                        .requestMatchers(authApi).hasAnyAuthority("ROLE_ADMIN", "ROLE_USER", "ROLE_MANAGER")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Configure session management to be stateless (no sessions)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Set our custom authentication provider
                .authenticationProvider(authenticationProvider())

                // Add our JWT filter before the default username/password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Build and return the configured security filter chain
        return http.build();
    }

    // Configures the authentication provider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Create DAO (Data Access Object) authentication provider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Set the password encoder (BCrypt)
        authProvider.setPasswordEncoder(passwordEncoder());

        // Set the custom user details service
        authProvider.setUserDetailsService(userDetailsService);

        return authProvider;
    }

    // Defines the password encoder bean (BCrypt implementation)
    @Bean
    PasswordEncoder passwordEncoder() {
        // Use BCrypt with default strength
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}