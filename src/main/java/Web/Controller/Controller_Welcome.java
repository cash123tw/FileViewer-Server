package Web.Controller;

import Data.Entity.UserInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring5.util.SpringContentTypeUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class Controller_Welcome {

    @GetMapping
    public ModelAndView welcome(String message,ModelAndView mav, @AuthenticationPrincipal UserInfo user) {
        message
                = Optional.ofNullable(message)
                        .orElse(String.format("歡迎登入 : %s",user.getRealName()));
        mav.setViewName("/main");
        mav.addObject("message",message);

        return mav;
    }

    @GetMapping(value = "favicon.ico", produces = {"image/x-icon"})
    @ResponseBody
    public byte[] getFavicon() throws IOException {
        InputStream in
                = getClass().getResourceAsStream("/static/favicon.ico");
        return in.readAllBytes();
    }
}
