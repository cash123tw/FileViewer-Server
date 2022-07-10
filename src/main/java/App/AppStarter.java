package App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@SpringBootApplication(scanBasePackages = {"Web.*","App.*","Data.Criteria"})
@EntityScan(basePackages = {"Data.Entity"})
@EnableJpaRepositories(basePackages = {"Data.Repository"})
public class AppStarter {

    public static void main(String[] args) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        SpringApplication.run(AppStarter.class);

//        if(desktop.isSupported(Desktop.Action.BROWSE)){
//            URI uri = URI.create("http://localhost:9090/");
//            desktop.browse(uri);
//        }
    }

}
