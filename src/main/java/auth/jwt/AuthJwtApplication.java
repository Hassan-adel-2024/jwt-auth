package auth.jwt;

import auth.jwt.entity.AppUser;
import auth.jwt.entity.Role;
import auth.jwt.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AuthJwtApplication {

    public static void main(String[] args) {

        SpringApplication.run(AuthJwtApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_MANAGER"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));
            String encoded_password = passwordEncoder.encode("1234");
            userService.saveUser(new AppUser(null, "John Doe", "john@example.com", encoded_password, null));
            userService.saveUser(new AppUser(null, "Alice Smith", "alice@example.com", encoded_password, null));
            userService.saveUser(new AppUser(null, "Bob Admin", "bob@example.com", encoded_password, null));
            userService.saveUser(new AppUser(null, "Charlie", "charlie@example.com", encoded_password, null));
            userService.setRoleToUser("john@example.com", "ROLE_USER");
            userService.setRoleToUser("alice@example.com", "ROLE_MANAGER");
            userService.setRoleToUser("bob@example.com", "ROLE_ADMIN");
            userService.setRoleToUser("charlie@example.com", "ROLE_ADMIN");
        };

    }

}
