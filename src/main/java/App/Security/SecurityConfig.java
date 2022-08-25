package App.Security;

import App.Security.Filter.CookieAuthenticateFilter;
import App.Security.Filter.UserPasswordAuthenticateFilter;
import Data.Entity.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.impl.JWTParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public final static String LOGIN = "/log/login";
    public final static String SIGNIN = "/log/signIn";
    public final static String REGIST = "/log/regist";
    public final static String LOG_PREFIX = "/log";

    @Value("${file-explore.secret-key}")
    private String JWT_SECRET_KEY;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .exceptionHandling()
                .accessDeniedPage("/login")
                .authenticationEntryPoint(new CustomAuthenticateEntryPoint())
                .and()
                .formLogin()
                .loginPage("/login")
                .and()
                .addFilterBefore(new UserPasswordAuthenticateFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new CookieAuthenticateFilter(), UserPasswordAuthenticateFilter.class)
                .logout().deleteCookies(CookieAuthenticateFilter.COOKIE_NAME)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, LOG_PREFIX+"/*").permitAll()
                .antMatchers(HttpMethod.GET,LOG_PREFIX+"/**","/css/**","/js/**").permitAll()
                .antMatchers("/admin/**").hasAnyAuthority(Role.ADMIN.name())
                .anyRequest().authenticated()
        ;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

    @Bean
    public JWT_Token_Center jwt_token_center(PasswordEncoder passwordEncoder){
        return new JWT_Token_Center(this.JWT_SECRET_KEY,passwordEncoder);
    }
}
