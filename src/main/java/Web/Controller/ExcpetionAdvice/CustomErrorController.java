package Web.Controller.ExcpetionAdvice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

@Controller
public class CustomErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("/error")
    public void errorHandle(HttpServletResponse response) throws IOException {
        String message = URLEncoder.encode("不要亂跑呦!!", Charset.forName("UTF-8"));
        response.sendRedirect("/?message="+message);
    }

}
