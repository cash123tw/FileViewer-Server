package Web.Controller;

import Data.Entity.UserInfo;
import Web.Service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/user")
public class Controller_UserInfo {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping()
    public ModelAndView getUserDetailPage(ModelAndView mav) {
        mav.setViewName("/user/detail");
        return mav;
    }

    @GetMapping("/")
    public UserInfo getMineUserDetail(@AuthenticationPrincipal UserInfo userInfo) {
        userInfo
                = userInfoService.getUserByUserName(userInfo.getUsername()).get();
        userInfo.setPassword(null);
        return userInfo;
    }

    @PutMapping("/")
    public ResponseEntity<String> updatePassword(
            @AuthenticationPrincipal UserInfo nowerUser,
            String oldPassword,
            String newPassword) {

        String message = "輸入數據異常";
        Integer status = 400;

        try {
            userInfoService.updatePassword(nowerUser, oldPassword, newPassword);
            status = 200;
            message = "變更成功";
        } catch (AuthenticationException e) {
            message = e.getMessage();
        }

        return ResponseEntity
                .status(status)
                .body(message);
    }

}
