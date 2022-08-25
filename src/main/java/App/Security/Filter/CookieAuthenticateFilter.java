package App.Security.Filter;

import App.Security.JWT_Token_Center;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class CookieAuthenticateFilter extends OncePerRequestFilter {

    public final static String COOKIE_NAME = "secret_cookie";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Optional<Cookie> token = Arrays
                .stream(
                        Optional.ofNullable(
                                request.getCookies()).orElse(new Cookie[1])
                ).filter(cookie -> cookie != null && COOKIE_NAME.equals(cookie.getName()))
                .findFirst();
        if (token.isPresent()) {
            SecurityContextHolder.getContext().setAuthentication(
                    new PreAuthenticatedAuthenticationToken(token.get().getValue(), null)
            );
        }

        filterChain.doFilter(request, response);
    }
}
