package auth.jwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    private String name;
    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private List<AppUser> users = new ArrayList<>();
    public Role(Long id , String name) {
        this.roleId = id;
        this.name = name;
    }
    @Override
    public String toString() {
        return "Role{" + "id=" + roleId + ", name='" + name + "'}";
    }
}
