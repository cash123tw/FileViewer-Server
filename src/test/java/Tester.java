import App.AppStarter;
import Data.Criteria.MineCriteria;
import Data.Entity.FilePath;
import Data.Entity.FileType;
import Data.Entity.Tag;
import Data.Repository.FilePathRepository;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import Web.Service.FileIO.FileUpload;
import org.assertj.core.util.Sets;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Set;

@SpringBootTest(classes = AppStarter.class)
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
//    @Autowired
//    MockMvc mockMVC;
//    @Autowired
//    WebApplicationContext context;


//        @BeforeAll
//    public void init(){
//        this.mockMVC = MockMvcBuilders.webAppContextSetup(context).build();
//    }


//    @Test
    public void test1() throws Exception {
        FilePath f = fr.findById(157).get();
        fr.delete(f);
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
