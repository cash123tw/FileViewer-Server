package App.Init;

import Bean.PathProvider;
import Worker.FileExploreWorker;
import Worker.FileSaverWorker;
import org.apache.tomcat.util.net.AprEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.objenesis.strategy.PlatformDescription;
import org.springframework.orm.jpa.vendor.Database;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.ObjectStreamClass;
import java.util.*;

@Configuration
public class BeanSetter {

    @Value("${root.path}")
    private String fileRoot;
    @Value("${setter.hidePath}")
    private boolean hidePath;

    /**
     * Value type :
     * 1)new : create new database and insert value;
     * 2)rescan: run check method again,check file is all upload to database;
     * 3)non: start without do anything;
     */
    @Value("${setter.start-mode}")
    private String start_mode = "non";

    @Bean
    public PathProvider pathProvider() throws IllegalAccessException {
        PathProvider pathProvider = new PathProvider(fileRoot);
        pathProvider.setHideRealPath(hidePath);
        return pathProvider;
    }

    @Bean
    public FileExploreWorker fileExploreWorker(PathProvider provider) throws FileNotFoundException {
        return new FileExploreWorker(provider);
    }

    @Bean
    public FileSaverWorker fileSaverWorker(PathProvider provider) {
        return new FileSaverWorker(provider);
    }

    @Bean
    public Init getInit() {
        return new Init((boolean) getStartMode().get("rescan"));
    }

    @Bean
    @Primary
    public HibernateProperties hibernateProperties(HibernateProperties properties){
        properties.setDdlAuto((String) getStartMode().get("ddl_auto"));
        return properties;
    }

    public Map<String, Object> getStartMode() {
        HashMap<String, Object> result = new HashMap<>();
        boolean mode = false;
        String ddl_auto = "update";

        switch (start_mode.toLowerCase()) {
            case "rescan" -> {
                mode= true;
                ddl_auto = "update";
            }
            case "new" -> {
                mode= true;
                ddl_auto = "create";
            }
            default -> {
                mode= false;
                ddl_auto = "update";
            }
        }

        result.put("rescan",mode);
        result.put("ddl_auto",ddl_auto);
        return result;
    }

}
