package Web.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring5.util.SpringContentTypeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

@Controller
@RequestMapping("/")
public class Controller_Welcome {

    @GetMapping
    public ModelAndView welcome(ModelAndView mav) {
        mav.setViewName("/main");
        mav.addObject("message", "檔案歸類系統");
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
