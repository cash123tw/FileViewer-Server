package App.Security;

import App.Security.Filter.CookieAuthenticateFilter;
import App.Security.Filter.UserPasswordAuthenticateFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public final static String LOGIN = "/log/login";
    public final static String SIGNIN = "/log/signIn";
    public final static String REGIST = "/log/regist";
    public final static String LOG_PREFIX = "/log";

    @Autowired
    private UserAuthenticationProvider provider;

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
                .antMatchers(HttpMethod.GET,LOG_PREFIX+"/*","/css/*","/js/*").permitAll()
                .anyRequest().authenticated()
        ;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }
}
