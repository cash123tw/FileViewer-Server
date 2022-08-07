package Web.Controller;

import App.Security.Filter.CookieAuthenticateFilter;
import Data.Entity.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/log")
public class Controller_Log {

    @RequestMapping("/login")
    public ModelAndView getPage(@AuthenticationPrincipal UserInfo userInfo, ModelAndView mav, HttpServletResponse response) throws IOException {
        mav.setViewName("/log/login");
        mav = checkIsSignInAlready(userInfo, mav, response);
        return mav;
    }

    @RequestMapping("regist")
    public ModelAndView getRegistPage(@AuthenticationPrincipal UserInfo userInfo, ModelAndView mav, HttpServletResponse response) throws IOException {
        mav.setViewName("/log/regist");
        mav = checkIsSignInAlready(userInfo, mav, response);
        return mav;
    }

    public ModelAndView checkIsSignInAlready(@AuthenticationPrincipal UserInfo userInfo, ModelAndView mav, HttpServletResponse response) throws IOException {
        if (Objects.nonNull(userInfo)) {
            response.sendRedirect("/");
            return null;
        } else {
            return mav;
        }
    }

    @RequestMapping("/signIn")
    public void signIn(@AuthenticationPrincipal UserInfo userInfo, HttpServletResponse response) throws IOException {
        String username = userInfo.getUsername();
        Cookie cookie = new Cookie(CookieAuthenticateFilter.COOKIE_NAME, username);
        cookie.setPath("/");
        cookie.setSecure(true);
        response.addCookie(cookie);
        response.sendRedirect("/");
    }


}
