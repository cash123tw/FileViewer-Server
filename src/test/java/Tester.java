import App.AppStarter;
import App.Security.AuthenticationService;
import Data.Criteria.MineCriteria;
import Data.Entity.*;
import Data.Repository.FilePathRepository;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import Web.Service.FileIO.FileUpload;
import Web.Service.UserInfoService;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest(classes = AppStarter.class)
@EnableWebSecurity
//@AutoConfigureMockMvc
//@WebAppConfiguration
public class Tester {

    @Autowired
    SessionFactory f;
    @Autowired
    ApplicationContext context;
    @Autowired
    MineCriteria criteria;
    @Autowired
    FilePathRepository fr;
    @Autowired
    AuthenticationService authService;
    @Autowired
    UserInfoService userInfoService;
//    @Autowired
//    MockMvc mockMVC;
//    @Autowired
//    WebApplicationContext context;


//        @BeforeAll
//    public void init(){
//        this.mockMVC = MockMvcBuilders.webAppContextSetup(context).build();
//    }


    @Test
    public void test1() throws Exception {
        Optional<UserInfo> user =
                userInfoService.getUserByUserName("ccwa56c");
        UserInfo userInfo
                = user.get();
        userInfo.setUsername("aaaaxxxx");
        userInfo.setPassword("123a");
        userInfoService.updateUserInfo(userInfo);
    }

    //    @Test
    public void test2() {
        Page<FilePath> all = fr.findAll(PageRequest.of(1, 100));
        System.out.println();
    }

    @Autowired
    FileUpload u;
    @Autowired
    Serv_GetFile_FromDatabase_Impl fs;

}
