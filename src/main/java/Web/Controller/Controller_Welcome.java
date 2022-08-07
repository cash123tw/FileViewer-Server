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

@Controller
@RequestMapping("/")
public class Controller_Welcome {

    @GetMapping
    public ModelAndView welcome(ModelAndView mav, HttpServletRequest request, @AuthenticationPrincipal UserInfo user) {

        String msg = "";

        if (Objects.nonNull(user)) {
            msg = user.getUsername();
        }
        mav.setViewName("/main");
        mav.addObject("message", "歡迎登入".concat(" ").concat(msg));

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
