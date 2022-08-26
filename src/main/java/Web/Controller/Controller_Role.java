package Web.Controller;

import Data.Entity.Role;
import Data.Entity.UserInfo;
import Web.Service.UserInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Only have authority role [ADMIN] user can enter this controller
 * Role Verify set in class : [App.Security.SecurityConfig]
 */
@RestController
@RequestMapping("/admin/user_info")
public class Controller_Role {

    @Autowired
    private UserInfoService userInfoService;
    private final ObjectMapper mapper = new ObjectMapper();


    @GetMapping("")
    public ModelAndView getRoleEditView(ModelAndView mav) {
        mav.setViewName("/admin/main");
        return mav;
    }

    @GetMapping("/")
    public List<String> getALlRoleType() {
        return Role.roles;
    }

    @GetMapping("/all")
    public Page<UserInfo> getAllUserInfo(@AuthenticationPrincipal UserInfo userInfo, Integer size, Integer page) {
        PageRequest request
                = PageRequest.of(page, size);
        Page<UserInfo> users
                = userInfoService.getAllUserInfo(request);
        return users;
    }

    /*
    If Edit UserInfo is nower Authentication owner
    This method will not edit anything.
     */
    @PutMapping("/")
    public UserInfo updateUserInfo(@AuthenticationPrincipal UserInfo nowerUser,@RequestBody UserInfo userInfo) {
        if(!nowerUser.getUsername().equals(userInfo.getUsername())) {
            userInfo
                    = userInfoService.updateUserInfo(userInfo);
        }
        return userInfo;
    }
}
