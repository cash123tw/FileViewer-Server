import App.AppStarter;
import Data.Entity.FilePath;
import Data.Entity.FileType;
import Data.Entity.Tag;
import Data.Entity.TagType;
import Data.Repository.FilePathRepository;
import Data.Repository.TagRepository;
import Data.Repository.TagTypeRepository;
import Web.Service.FileDataEditor.Serv_DataEditor_Impl;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import Web.Service.FileIO.FileUpload;
import Web.Service.LocalFileWalker.Serv_localWalk_impl;
import Web.Service.TagService.Serv_Tag_Provider_Impl;
import Web.Service.TypeEditor.Serv_Type_Impl;
import Worker.FileExploreWorker;
import com.sun.source.doctree.IndexTree;
import org.assertj.core.util.Lists;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

@SpringBootTest(classes = AppStarter.class)
@AutoConfigureMockMvc
//@WebAppConfiguration
public class Tester {

    @Autowired
    Serv_DataEditor_Impl d;
    @Autowired
    Serv_localWalk_impl walk;
    @Autowired
    Serv_Type_Impl types;
    @Autowired
    FilePathRepository fr;
    @Autowired
    TagRepository tr;
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

    @Autowired
    SessionFactory f;
    @Autowired
    Serv_Tag_Provider_Impl ts;

    @Test
    public void test1() throws Exception {
        Session session = f.openSession();
        CriteriaBuilder b = session.getCriteriaBuilder();

        List<Integer> list = Lists.list(1, 2, 3);

        CriteriaQuery<FilePath> q1 = b.createQuery(FilePath.class);
        Root<FilePath> fr = q1.from(FilePath.class);
        fr.fetch("fileType");

        SetJoin<Object, Object> t = fr.joinSet("tags");
        t.on(t.get("id").in(list));
        q1
                .groupBy(fr.get("id"))
                .having(b.equal(b.count(t.get("id")), list.size()));

        Query<FilePath> q = session.createQuery(q1);
        List<FilePath> l = q.list();
        l.forEach(ll -> {
            System.out.println(ll.getId());
        });
    }

    @Test
    public void test2() {
        List<Integer> is = Arrays.asList(1, 2);
        Session session = f.openSession();
        CriteriaBuilder b = session.getCriteriaBuilder();

        CriteriaQuery<FilePath> f = b.createQuery(FilePath.class);
        Root<FilePath> fr = f.from(FilePath.class);
        ParameterExpression<List> p = b.parameter(List.class);

        f.where(fr.get("id").in());

        Query<FilePath> q = session.createQuery(f);
        q.setParameter(p, is);
        List<FilePath> list = q.list();
        System.out.println(list.size());
    }

    @Autowired
    FileUpload u;
    @Autowired
    Serv_GetFile_FromDatabase_Impl fs;

    @Test
//    @Transactional
    public void test3() throws Exception {
        FilePath filePath = fr.findById(3).get();
//        filePath.setParentPath(null);
//        filePath = fr.save(filePath);
        fr.delete(filePath);
    }

    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("/");
        System.out.println(uri.getPath());
    }

}
