package Web.Service;

import App.Security.AuthenticationService;
import Data.Entity.Role;
import Data.Entity.UserInfo;
import Data.Repository.UserInfoRepository;
import org.apache.catalina.User;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<UserInfo> getUserByUserName(String userName) {
        if (Strings.isEmpty(userName))
            Optional.empty();

        return userInfoRepository.findByUsername(userName);
    }

    public UserInfo saveUserInfo(UserInfo userInfo) throws Exception {
        String password = userInfo.getPassword();

        if (Strings.isEmpty(userInfo.getUsername())
                && Strings.isEmpty(userInfo.getPassword())) {
            throw new IllegalArgumentException("帳號密碼有誤");
        }
        if (getUserByUserName(userInfo.getUsername()).isPresent()) {
            throw new Exception("UserName is already exists");
        }
        if (password.length() > 16 || password.length() < 4 || !password.matches("[A-Za-z0-9]*"))
            throw new Exception("密碼格式錯誤");

        password = passwordEncoder.encode(password);
        userInfo.setPassword(password);
        userInfo.addAuthority(Role.WATCH);
        userInfo.setEnabled(false);

        userInfo = userInfoRepository.save(userInfo);
        userInfo.setPassword(null);

        return userInfo;
    }

    public UserInfo updateUserInfo(@Validated UserInfo userInfo) {
        UserInfo old_userInfo
                = getUserByUserName(userInfo.getUsername()).get();
        userInfo.setPassword(old_userInfo.getPassword());
        userInfo.addAuthority(Role.WATCH);
        UserInfo new_save
                = userInfoRepository.save(userInfo);
        new_save.setPassword(null);
        return new_save;
    }

    public Page<UserInfo> getAllUserInfo(PageRequest request) {
        Page<UserInfo> result
                = userInfoRepository.findAll(request);
        result
                .getContent()
                .forEach(user->user.setPassword(""));
        return result;
    }

    public UserInfo updatePassword(UserInfo userInfo,String oldPasswordCheck,String newPassword) throws AuthenticationException {
//       If no throw any exception express password check ok.
        authenticationService.authenticate(userInfo.getUsername(), oldPasswordCheck);
        userInfo.setPassword(passwordEncoder.encode(newPassword));
        return updateUserInfo(userInfo);
    }
}
