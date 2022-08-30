package App;

import App.ConfigPanel.ConfigSettingPanel;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static App.Init.InitHinernateParam.StartMode;
import static App.Init.InitHinernateParam.StartMode.*;

@SpringBootApplication(scanBasePackages = {"Web.*", "App.*", "Data.Criteria"})
@EntityScan(basePackages = {"Data.Entity"})
@EnableJpaRepositories(basePackages = {"Data.Repository"})
@Log
public class AppStarter {

    private static ConfigurableApplicationContext app;
    private static Desktop desktop;
    private static Scanner scanner = new Scanner(System.in);
    private static ReentrantLock lock = new ReentrantLock();
    private static ConfigSettingPanel configSettingPanel;

    private static final String config = "./config.yml";
    private static final String showText =
            """ 
                    歡迎來到操作介面，輸入以下框格內的文字可以進行操作:
                    [restart] 重啟服務
                    [close] 關閉服務
                    你的輸入 :　""";
    private static final String configSettingFile = """
            file-explore:
              explore: {scan-path: './', start-mode: RESCAN}
              secret-key: Mine_Key...
              word2pdf: {download-location: 'C:\\.tmp', suffix: System}
              already-config: false
            spring:
              datasource: {username: , password: '', url: ''}
            """;


    private static Boolean initStart = true;

    public static void main(String[] args) throws Exception {

        desktop = Desktop.getDesktop();

        try {
            configSettingPanel = new ConfigSettingPanel(config, scanner);
        } catch (Exception e) {
            File file = new File(config);
            FileOutputStream out = new FileOutputStream(file);
            out.write(configSettingFile.getBytes(StandardCharsets.UTF_8));
            out.flush();
            System.out.println("config.yml 檔案已經重置");
            return;
        }
        configSettingPanel.StartMenu0();
        initStart = configSettingPanel.isInitStart();
//        Outer Setting is optional,If not set use classpath:application.yml
        System.setProperty("spring.config.additional-location", "optional:file:" + config);

        if (initStart) {
            try {
                controlSystem(SystemOpt.RESTART);
            }catch (Exception e){}
            controlSystem(SystemOpt.CLOSE);
            System.out.println("系統已重置");
        } else {
            controlSystem(SystemOpt.RESTART);
            configSettingPanel.initStartOk();
            HumanPanelStart();
        }

    }

    public static void printNetLocal() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostAddress();
        log.info(String.format("URL : [%s]", "http://" + hostName + ":9090"));
    }

    public static void controlSystem(SystemOpt opt) throws Exception {
        if (lock.isLocked()) {
            return;
        } else {
            switch (opt) {
                case CLOSE -> {
                    closeSystem();
                }
                case RESTART -> {
                    reStartSystem();
                }
            }
        }

    }

    private static void reStartSystem() throws Exception {
        closeSystem();
        StartSystem();
    }

    private static void closeSystem() {
        if (Objects.nonNull(app)) {
            app.close();
            app = null;
        }
    }

    private static void StartSystem() throws IOException {
        if (Objects.isNull(app)) {
            app = SpringApplication.run(AppStarter.class);
        }
        printNetLocal();
//        StartWebView();
    }

    private static void StartWebView() {
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            URI uri = URI.create("http://localhost:9090/");
            try {
                desktop.browse(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void HumanPanelStart() throws Exception {
        System.out.print(String.format(showText));

        out:
        while (scanner.hasNextLine()) {
            String opt = scanner.nextLine();

            switch (opt.toLowerCase()) {
                case "close" -> {
                    controlSystem(SystemOpt.CLOSE);
                    break out;
                }
                case "restart" -> {
                    controlSystem(SystemOpt.RESTART);
                }
                default -> {
                    System.out.print(String.format(showText));
                }
            }
        }
    }

    public enum SystemOpt {
        CLOSE, RESTART
    }
}
