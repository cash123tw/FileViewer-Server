import App.AppStarter;
import Data.Repository.FilePathRepository;
import Data.Repository.TagTypeRepository;
import Web.Service.FileDataEditor.Serv_DataEditor_Impl;
import Web.Service.LocalFileWalker.Serv_localWalk_impl;
import Web.Service.TagService.Serv_Tag_Provider_Impl;
import Worker.FileExploreWorker;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = AppStarter.class)
@AutoConfigureMockMvc
//@WebAppConfiguration
public class Tester {

    @Autowired
    Serv_DataEditor_Impl d;
    @Autowired
    Serv_localWalk_impl walk;
    @Autowired
    FilePathRepository fr;
    @Autowired
    SessionFactory factory;
    @Autowired
    TagTypeRepository tf;
    @Autowired
    FileExploreWorker worker;
    @Autowired
    Serv_Tag_Provider_Impl tagService;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMVC;

//    @BeforeAll
//    public void init(){
//        this.mockMVC = MockMvcBuilders.webAppContextSetup(context).build();
//    }

    @Test
    public void test1() throws Exception {
        MvcResult result = mockMVC.perform(MockMvcRequestBuilders.get("/tag0"))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        System.out.println();
    }


}
