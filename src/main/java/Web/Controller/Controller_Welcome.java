package Web.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class Controller_Welcome {

    @GetMapping
    public ModelAndView welcome(ModelAndView mav){
        mav.setViewName("/main");
        mav.addObject("message","恭喜發財");
        return mav;
    }
}
