package App.Security;

import Data.Entity.UserInfo;
import Data.Repository.UserInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.authentication.PasswordEncoderParser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.naming.AuthenticationException;
import java.nio.CharBuffer;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserInfoRepository userInfoRepository;

    public UserInfo authenticate(String user, String password) throws AuthenticationException {
        UserInfo userInfo = getUserInfo(user);

        if (matchPassword(password, userInfo.getPassword())) {
            return userInfo;
        }
        throw new AuthenticationException("密碼錯誤");
    }

    public UserInfo authenticateToken(String token) throws AuthenticationException {
        UserInfo userInfo = getUserInfo(token);
        return userInfo;
    }

    public void saveUserInfo(UserInfo userInfo) {
        if (Strings.isEmpty(userInfo.getUsername())
                && Strings.isEmpty(userInfo.getPassword())) {
            throw new IllegalArgumentException("帳號密碼有誤");
        }
        String password = userInfo.getUsername();
        password = passwordEncoder.encode(password);
        userInfo.setPassword(password);

        userInfoRepository.save(userInfo);
    }

    private UserInfo getUserInfo(String user) throws AuthenticationException {
        Optional<UserInfo> info
                = userInfoRepository.findByUsername(user);
        if (info.isEmpty()) {
            throw new AuthenticationException("帳號不存在");
        }
        return info.get();
    }


    private boolean matchPassword(String password, String masterPassword) {
        return passwordEncoder.matches(CharBuffer.wrap(password), masterPassword);
    }

}
