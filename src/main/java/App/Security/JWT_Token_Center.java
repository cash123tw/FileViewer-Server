package App.Security;

import Data.Entity.Role;
import Data.Entity.UserInfo;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JWT_Token_Center {
    private final String JWT_SECRET_KEY;
    private final Algorithm algorithm;

    public static final String TAG_ENABLE = "enable";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_ROLES = "roles";
    private static final String KEY = "*QAGDQ-";

    private PasswordEncoder passwordEncoder;

    public JWT_Token_Center(String key, PasswordEncoder passwordEncoder) {
        this.JWT_SECRET_KEY = key;
        this.algorithm = Algorithm.HMAC256(this.JWT_SECRET_KEY);
        this.passwordEncoder = passwordEncoder;
    }

    public String GetEncoded(JWTCreator.Builder builder){
        return builder.sign(this.algorithm);
    }

    synchronized public Map<String, Claim> VerifyToken(String token) throws JWTVerificationException {
        DecodedJWT verify = JWT.require(algorithm).build().verify(token);
        return verify.getClaims();
    }

    public String GetJWT_UserInfo(UserInfo userInfo){
        boolean enabled = userInfo.isEnabled();
        String username = userInfo.getUsername();

        List<String> roles = userInfo.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String token = GetEncoded(
                JWT.create()
                        .withClaim(TAG_USERNAME, username)
                        .withClaim(TAG_ENABLE, enabled)
                        .withClaim(TAG_ROLES, roles)
        );

        return token;
    }

    public UserInfo VerifyJWT_UserInfo(String token){
        Map<String, Claim> claims = VerifyToken(token);
        String username = claims.get(TAG_USERNAME).asString();
        Boolean enableKey = claims.get(TAG_ENABLE).asBoolean();
        Role[] roles = claims.get(TAG_ROLES).asArray(Role.ADMIN.getClass());

        if (enableKey) {
            return new UserInfo(username,null,roles){{
                setEnabled(true);
            }};
        }else{
            return null;
        }
    }

    private String getEnableEncoded(boolean enable,String username){

        if(enable){
            username = username.concat(KEY);
            return passwordEncoder.encode(username);
        }else{
            return "non";
        }
    }

    private Boolean matchEnableEncoded(String username,String enableKey){
        username = username.concat(KEY);
        return passwordEncoder.matches(username,enableKey);
    }
}
