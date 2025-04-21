package auth.jwt.service;

import auth.jwt.entity.AppUser;
import auth.jwt.entity.Role;

import java.util.List;

public interface UserService {
    AppUser saveUser(AppUser user);
    Role saveRole(Role role);
    void setRoleToUser(String email, String roleName);
    AppUser getUserByEmail(String email);
    List<AppUser> getUsers();

}
