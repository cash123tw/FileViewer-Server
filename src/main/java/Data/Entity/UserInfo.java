package Data.Entity;

import Web.Bean.Convert.RoleConvert;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class UserInfo implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter
    @Getter
    private Integer id;
    @Setter
    @Column(unique = true)
    private String username;
    @Setter
    private String password;
    @Setter
    private boolean enabled = false;
    @Setter @Getter
    private String realName;
    @Convert(converter = RoleConvert.class)
    private List<String> roles;

    public UserInfo() {

    }

    public UserInfo(String username, String password, Role... role) {
        this.username = username;
        this.password = password;
        this.roles = new ArrayList<>();
        if (Objects.nonNull(role)) {
            Stream.of(role).forEach(r -> roles.add(r.name()));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this
                .roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }

    public void addAuthority(Role role) {
        this.roles.add(role.name());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
