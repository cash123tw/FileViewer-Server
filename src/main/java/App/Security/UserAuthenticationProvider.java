package App.Security;

import Data.Entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    AuthenticationService authenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UserInfo userInfo = null;

        try {
            if (authentication instanceof UsernamePasswordAuthenticationToken token) {
                userInfo = authenticationService.authenticate((String) token.getPrincipal(), (String) token.getCredentials());
            } else if (authentication instanceof PreAuthenticatedAuthenticationToken token) {
                userInfo = authenticationService.authenticateToken((String) token.getPrincipal());
            }
        } catch (Exception e) {
            throw new AuthenticationServiceException(e.getMessage());
        }

        if (Objects.isNull(userInfo)) {
            return null;
        } else if (!userInfo.isEnabled()) {
            throw new AuthenticationServiceException("帳號未啟用，聯絡管理人員。");
        } else {
            return new UsernamePasswordAuthenticationToken(userInfo, null, userInfo.getAuthorities());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
