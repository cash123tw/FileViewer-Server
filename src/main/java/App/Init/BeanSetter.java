package App.Init;

import App.AppStarter;
import Bean.PathProvider;
import Worker.FileExploreWorker;
import Worker.FileSaverWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.util.*;

import static App.Init.Init.*;

@Configuration
public class BeanSetter {

    @Value("${file-explore.explore.scan-path}")
    private String fileRoot;
    @Value("${file-explore.explore.hide-path}")
    private boolean hidePath;
    @Value("${file-explore.word2pdf.download-location}")
    private String w2p_location;
    @Value("${file-explore.word2pdf.suffix}")
    private String w2p_suffix;

    /**
     * Value type :
     * 1)new : create new database and insert value;
     * 2)rescan: run check method again,check file is all upload to database;
     * 3)non: start without do anything;
     */
    @Value("${file-explore.explore.start-mode}")
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
        return new Init((StartMode) getStartMode().get("rescan"));
    }

    @Bean
    @Order(1)
    public WordToPdf getWrodToPdf(){
        return new WordToPdf(w2p_location,w2p_suffix);
    }

    @Bean
    @Primary
    public HibernateProperties hibernateProperties(HibernateProperties properties){
        properties.setDdlAuto((String) getStartMode().get("ddl_auto"));
        return properties;
    }

    public Map<String, Object> getStartMode() {
        HashMap<String, Object> result = new HashMap<>();
        StartMode mode = StartMode.NON;
        String ddl_auto = "update";

        switch (start_mode.toLowerCase()) {
            case "rescan" -> {
                mode = StartMode.RESCAN;
                ddl_auto = "update";
            }
            case "new" -> {
                mode = StartMode.NEW;
                ddl_auto = "create";
            }
            default -> {
                mode = StartMode.NON;
                ddl_auto = "update";
            }
        }

        result.put("rescan",mode);
        result.put("ddl_auto",ddl_auto);
        return result;
    }

}
