package App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"Web.*","App.*"})
@EntityScan(basePackages = {"Data.Entity"})
@EnableJpaRepositories(basePackages = {"Data.Repository"})
public class AppStarter {

    public static void main(String[] args) {
        SpringApplication.run(AppStarter.class);
    }

}
