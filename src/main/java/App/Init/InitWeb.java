package App.Init;

import Data.Entity.UserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Configuration
public class InitWeb {

    @Bean
    public WebMvcConfigurer mvcConfig(){
        return new WebMvcConfigurer(){
            @Override
            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("http://127.0.0.1:5500/")
//                        .allowCredentials(true)
//                        .allowedMethods("GET", "POST", "PUT", "DELETE")
//                        .allowedHeaders("/*")
//                ;
            }
        };
    }

    @Bean
    public Filter setAuthenticationFilter(){
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                UserInfo user = null;
                Optional<Authentication> auth = Optional.of(SecurityContextHolder.getContext().getAuthentication());
                if(auth.isPresent()){
                    if(auth.get().getPrincipal() instanceof UserInfo u){
                        user = u;
                    }
                }
                request.setAttribute("user",user);

                filterChain.doFilter(request,response);
            }
        };
    }

}
