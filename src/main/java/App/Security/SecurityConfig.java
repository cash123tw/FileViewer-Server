package App.Security;

import App.Security.ExceptionHandler.CustomAuthenticateEntryPoint;
import App.Security.Filter.CookieAuthenticateFilter;
import App.Security.ExceptionHandler.RoleAccessFailHandler;
import App.Security.Filter.UserPasswordAuthenticateFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static Data.Entity.Role.*;

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
                .accessDeniedHandler(new RoleAccessFailHandler())
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
                //Need role path
                .antMatchers("/admin/**").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.POST,"/explore/findByParam").authenticated()
                .antMatchers(HttpMethod.PUT,"/user/").authenticated()
                .antMatchers(HttpMethod.POST).hasAnyAuthority(EDIT.name(), ADMIN.name())
                .antMatchers(HttpMethod.PUT).hasAnyAuthority(EDIT.name(), ADMIN.name())
                .antMatchers(HttpMethod.DELETE).hasAnyAuthority(EDIT.name(), ADMIN.name())
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
