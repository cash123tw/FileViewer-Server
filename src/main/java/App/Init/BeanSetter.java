package App.Init;

import Bean.PathProvider;
import Worker.FileExploreWorker;
import Worker.FileSaverWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;

@Configuration
public class BeanSetter {

    @Value("${root.path}")
    private String fileRoot;
    @Value("${setter.hidePath}")
    private boolean hidePath;

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
    public FileSaverWorker fileSaverWorker(PathProvider provider){
        return  new FileSaverWorker(provider);
    }



}
