package Web.Controller;

import App.Security.AuthenticationService;
import App.Security.Filter.CookieAuthenticateFilter;
import App.Security.JWT_Token_Center;
import Data.Entity.UserInfo;
import Web.Service.UserInfoService;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/log")
public class Controller_Log {

    @Autowired
    private UserInfoService userInfoService;
    private AuthenticationService authenticationService;
    @Autowired
    private JWT_Token_Center jwtTokenCenter;

    private ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/login")
    public ModelAndView getPage(@AuthenticationPrincipal UserInfo userInfo,
                                @RequestAttribute(value = "message", required = false) String message,
                                ModelAndView mav,
                                HttpServletResponse response,
                                HttpServletRequest request
    ) throws IOException {

        mav.setViewName("/log/login");
        mav = checkIsSignInAlready(userInfo, mav, response);

        if (Objects.nonNull(mav))
            request.setAttribute("message", message);

        return mav;
    }

    @GetMapping("/regist")
    public ModelAndView getRegistPage(@AuthenticationPrincipal UserInfo userInfo, ModelAndView mav, HttpServletResponse response) throws IOException {
        mav.setViewName("/log/regist");
        mav = checkIsSignInAlready(userInfo, mav, response);
        return mav;
    }

    @GetMapping("/checkUsername")
    public ResponseEntity<String> checkUsernameExists(String userName) throws JsonProcessingException {
        Boolean exists = userInfoService.getUserByUserName(userName).isPresent();
        HashMap<Object, Object> map = new HashMap<>(1);
        map.put("exists", exists);
        return ResponseEntity.ok(mapper.writeValueAsString(map));
    }

    public ModelAndView checkIsSignInAlready(@AuthenticationPrincipal UserInfo userInfo, ModelAndView mav, HttpServletResponse response) throws IOException {
        if (Objects.nonNull(userInfo)) {
            response.sendRedirect("/");
            return null;
        } else {
            return mav;
        }
    }

    @PostMapping("/signIn")
    public void signIn(@AuthenticationPrincipal UserInfo userInfo,
                       HttpServletResponse response) throws IOException {

        String token
                = jwtTokenCenter.GetJWT_UserInfo(userInfo);

        Cookie cookie = new Cookie(CookieAuthenticateFilter.COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setSecure(true);
        response.addCookie(cookie);
        response.sendRedirect("/");
    }

    @PostMapping("/regist")
    @ResponseBody
    public ResponseEntity<String> registNewAccount(@RequestBody @Validated UserInfo user) throws Exception {
        userInfoService.saveUserInfo(user);
        String message = "註冊成功，等待管理人員審核。";
        ResponseEntity<String> result
                = ResponseEntity.status(200)
                .contentType(MediaType.TEXT_PLAIN)
                .header("Location", "/?message=" + message)
                .body(message);
        return result;
    }
}
