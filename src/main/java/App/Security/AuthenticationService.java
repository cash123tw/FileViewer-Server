package App.Security;

import Data.Entity.Role;
import Data.Entity.UserInfo;
import Data.Repository.UserInfoRepository;
import Web.Service.UserInfoService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static Data.Entity.Role.*;

@Service
public class AuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private JWT_Token_Center jwtTokenCenter;

    public UserInfo authenticate(String user, String password) throws AuthenticationException {
        UserInfo userInfo = getUserInfo(user);

        if (matchPassword(password, userInfo.getPassword())) {
            return userInfo;
        }
        throw new AuthenticationException("密碼錯誤");
    }

    public UserInfo authenticateToken(String token) throws AuthenticationException {
        try {
            UserInfo userInfo
                    = jwtTokenCenter.VerifyJWT_UserInfo(token);
            return userInfo;
        }catch (JWTVerificationException e){
            e.printStackTrace();
            throw new AuthenticationException("不合法的Token");
        }
    }

    public Optional<UserInfo> getUserInfo0(String user) {
        Optional<UserInfo> info
                = userInfoService.getUserByUserName(user);
        return info;
    }

    private UserInfo getUserInfo(String user) throws AuthenticationException {
        Optional<UserInfo> info = getUserInfo0(user);
        if (info.isEmpty()) {
            throw new AuthenticationException("帳號不存在");
        }
        return info.get();
    }

    public boolean matchPassword(String password, String masterPassword) {
        return passwordEncoder.matches(CharBuffer.wrap(password), masterPassword);
    }

//    ===================================================================================
}
