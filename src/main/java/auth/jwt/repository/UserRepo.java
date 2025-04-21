package auth.jwt.repository;

import auth.jwt.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<AppUser, Long> {
   AppUser findByEmail(String e);



}
