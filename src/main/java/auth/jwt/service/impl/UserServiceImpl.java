package auth.jwt.service.impl;

import auth.jwt.entity.AppUser;
import auth.jwt.entity.Role;
import auth.jwt.repository.RoleRepo;
import auth.jwt.repository.UserRepo;
import auth.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@RequiredArgsConstructor
@Transactional
@Service
@Slf4j

public class UserServiceImpl implements UserService , UserDetailsService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving user into db: {}", user);
        return userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving role into db: {}", role);
        return roleRepo.save(role);
    }

    @Override
    public void setRoleToUser(String email, String roleName) {
        log.info("Setting role {} to user {}", roleName, email);
         AppUser user = userRepo.findByEmail(email);
         Role role = roleRepo.findByName(roleName);
         user.setRole(role);
         userRepo.save(user);
    }

    @Override
    public AppUser getUserByEmail(String email) {
        log.info("Getting user from db: {}", email);
        return userRepo.findByEmail(email);
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Getting users from db___________________");
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username);
    }
}
