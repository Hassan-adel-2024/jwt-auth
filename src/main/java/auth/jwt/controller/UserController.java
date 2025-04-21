package auth.jwt.controller;

import auth.jwt.entity.AppUser;
import auth.jwt.entity.Role;
import auth.jwt.entity.UserRoleDto;
import auth.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getUsers() {
        log.info("getUsers====================================");
        return ResponseEntity.ok(userService.getUsers());
    }
    @PostMapping("/role/save")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/role/save").build().toUri();
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }
    @PostMapping("/user/save")
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/user/save").build().toUri();
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }
    @PostMapping("/role/setToUser")
    public ResponseEntity<Void> setRole(@RequestBody UserRoleDto userRoleDto) {
        userService.setRoleToUser(userRoleDto.getEmail(), userRoleDto.getRole());
        return ResponseEntity.ok().build();
    }


}
