package App.Security.ExceptionHandler;

import App.Security.Filter.CookieAuthenticateFilter;
import App.Security.SecurityConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

public class CustomAuthenticateEntryPoint implements AuthenticationEntryPoint {

    private final static String redirectFunc = """
            <!DOCTYPE html>
            <html lang="en" xmlns="http://www.w3.org/1999/xhtml"
                  xmlns:th="http://www.thymeleaf.org"
                  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width= device-width, initial-scale=1.0">
                <title>TagType Edit - FileExplore v1.0</title>
                <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
                      integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
                      crossorigin="anonymous">
            <script>
                window.onload=()=>{
                    window.location = "/log/login?message=%s";
                }
            </script>
            </head>
            </html>
            """;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String path = request.getServletPath();

        Optional<Cookie> first
                = Arrays
                .stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{new Cookie("TT","")}))
                .filter(c -> CookieAuthenticateFilter.COOKIE_NAME.equals(c.getName()))
                .findFirst();

        String message
                = Optional
                .ofNullable(authException.getMessage())
                .orElse("");

        if (first.isPresent()) {
            Cookie cookie = new Cookie(CookieAuthenticateFilter.COOKIE_NAME, "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            response.setHeader(HttpHeaders.CONTENT_TYPE,"text/html;charset=UTF-8");
            response.getOutputStream()
                    .write(String.format(redirectFunc, message)
                    .getBytes(StandardCharsets.UTF_8));
            return;
        }

        if (SecurityConfig.SIGNIN.equals(path)) {
            response.setStatus(403);
            response.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
        } else {
            response.setStatus(301);
            response.addHeader("Location", SecurityConfig.LOGIN);
        }
    }
}
