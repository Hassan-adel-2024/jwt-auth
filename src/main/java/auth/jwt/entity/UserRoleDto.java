package auth.jwt.entity;

import lombok.Data;

@Data
public class UserRoleDto {
    private String role;
    private String email;
}
