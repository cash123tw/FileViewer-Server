package Data.Entity;

import Web.Bean.Convert.RoleConvert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@ToString
public class UserInfo implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter
    @Getter
    private Integer id;
    @Setter
    @Column(unique = true,nullable = false,updatable = false)
    @Pattern(regexp ="[A-Za-z0-9]*",message="帳號格式錯誤")
    @Size(min = 4,max = 16,message = "長度不符合")
    @NotEmpty
    private String username;
    @Setter
    @NotEmpty
    private String password;
    @Setter
    private boolean enabled = false;
    @Setter @Getter
    @NotEmpty
    private String realName;
    @Convert(converter = RoleConvert.class)
    @Getter
    private Set<String> roles;

    public UserInfo() {
        this.roles = new HashSet<>();
    }

    public UserInfo(String username, String password, Role... role) {
        this();
        this.username = username;
        this.password = password;
        if (Objects.nonNull(role)) {
            Stream.of(role).forEach(r -> roles.add(r.name()));
        }
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(roles.size()==0){
            roles.add(Role.WATCH.name());
        }

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
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
