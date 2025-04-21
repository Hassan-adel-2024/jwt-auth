package auth.jwt.repository;

import auth.jwt.entity.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepo extends CrudRepository<Role, Long> {
    Role findByName(String name);
}
