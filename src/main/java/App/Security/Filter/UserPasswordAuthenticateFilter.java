package App.Security.Filter;

import App.Security.SecurityConfig;
import Data.Entity.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.cfg.annotations.reflection.PersistentAttributeFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserPasswordAuthenticateFilter extends OncePerRequestFilter {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (SecurityConfig.SIGNIN.equals(request.getServletPath())
                && request.getMethod().equals(HttpMethod.POST.name())) {
            UserInfo userInfo = parseRequestToUser(request);
            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(
                            userInfo.getUsername(),
                            userInfo.getPassword()
                    ));
        }

        filterChain.doFilter(request,response);
    }

    private UserInfo parseRequestToUser(HttpServletRequest request){
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        return new UserInfo(username,password,null);
    }
}
