package App;

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
import java.net.*;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static App.Init.Init.StartMode;
import static App.Init.Init.StartMode.*;

@SpringBootApplication(scanBasePackages = {"Web.*", "App.*", "Data.Criteria"})
@EntityScan(basePackages = {"Data.Entity"})
@EnableJpaRepositories(basePackages = {"Data.Repository"})
@Log
public class AppStarter {

    private static ConfigurableApplicationContext app;

    public static void main(String[] args) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        StartSystem();

        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            URI uri = URI.create("http://localhost:9090/");
            desktop.browse(uri);
        }
    }

    public static void printNetLocal() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostAddress();
        log.info(String.format("Wifi : [%s]\tURL : [%s]","Hello","http://"+hostName+":9090"));
    }

    public static void reStartSystem() throws IOException, InterruptedException {
        if (Objects.nonNull(app)) {
            app.close();
            app = null;
            Thread.sleep(1000);
        }

        StartSystem();
    }

    private static void StartSystem() throws IOException {
        if (Objects.isNull(app)) {
            app = SpringApplication.run(AppStarter.class);
        }
        printNetLocal();
    }

    public static class ApplicationSetting {
        private final Yaml yaml;
        private final Map<String, Object> setting;
        private final URL url;
        private StartSetting startSetting;

        public static ApplicationSetting getApplicationSetting() throws FileNotFoundException {
            return new ApplicationSetting();
        }

        private ApplicationSetting() throws FileNotFoundException {
            this.url
                    = AppStarter.class.getClassLoader().getResource("application.yml");
            this.yaml
                    = new Yaml();
            this.setting
                    = yaml.load(new FileInputStream(url.getFile()));

        }

        public Map<String, Object> getSetting() {
            return setting;
        }

        public Object get(String keyName) {
            return get(keyName, Object.class);
        }

        public <T> T get(String keyName, Class<T> t) {

            String[] keys = keyName.split("\\.");
            Object result = this.setting;
            int length = keys.length;
            boolean end = false;

            for (int i = 0; i < length; i++) {
                String key = keys[i];
                end = length == i + 1;

                if (result instanceof Map m) {
                    result = m.get(key);
                } else {
                    break;
                }
            }

            if (end) {
                return (T) result;
            } else {
                return null;
            }
        }

        public void set(String keyName, Object value) {
            Object result = this.setting;
            String[] keys = keyName.split("\\.");
            int length = keys.length;
            boolean end = false;

            for (int i = 0; i < length; i++) {
                String key = keys[i];
                end = length == i + 1;

                if (result instanceof Map m) {
                    if (end) {
                        if (m.containsKey(key)) {
                            Object target = m.get(key);
                            if (target.getClass() == value.getClass()) {
                                m.put(key, value);
                            } else {
                                throwTypeNotObjectException(keyName);
                            }
                        } else {
                            m.put(key, value);
                        }
                    } else if (m.containsKey(key)) {
                        result = m.get(key);
                    } else {
                        m.put(key, new HashMap());
                        result = m.get(key);
                    }
                } else {
                    throwTypeNotObjectException(keyName);
                }
            }
        }

        public StartSetting getStartSetting() {
            if (this.startSetting == null) {
                this.startSetting = new StartSetting(this);
            }
            return startSetting;
        }

        public void flushData() throws FileNotFoundException {
            OutputStreamWriter writer
                    = new OutputStreamWriter(new FileOutputStream(url.getPath()));
            this.yaml.dump(this.getSetting(), writer);
        }

        public void throwTypeNotObjectException(String keyName) {
            throw new IllegalArgumentException(String.format("Can't not set value because path [%s] is not object style", keyName));
        }
    }

    private static class StartSetting {

        private final String SCAN_PATH = "file-explore.explore.scan-path";
        private final String START_MODE = "file-explore.explore.start-mode";
        private final String INITED = "file-explore.inited";
        private final String DATABASE_USERNAME = "spring.datasource.username";
        private final String DATABASE_PASSWORD = "spring.datasource.password";
        private final String DATABASE_URL = "spring.datasource.url";

        private ApplicationSetting setting;

        @Getter
        private boolean inited;
        @Getter
        private String scanPath;
        @Getter
        private String startMode;
        @Getter
        private String databaseUsername;
        @Getter
        private String databasePassword;
        @Getter
        private String databaseUrl;

        public StartSetting(ApplicationSetting setting) {
            this.setting = setting;
            this.scanPath = setting.get(SCAN_PATH, String.class);
            this.inited = (Boolean) setting.get(INITED);
            this.startMode = setting.get(START_MODE, String.class);
            this.databaseUsername = setting.get(DATABASE_USERNAME, String.class);
            this.databasePassword = setting.get(DATABASE_PASSWORD, String.class);
            this.databaseUrl = setting.get(DATABASE_URL, String.class);
        }

        public void setStartMode(StartMode startMode) {

            switch (startMode) {
                case NEW -> {
                    this.startMode = NEW.name();
                }
                case RESCAN -> {
                    this.startMode = RESCAN.name();
                }
                default -> {
                    this.startMode = NON.name();
                }
            }

            setting.set(START_MODE, this.startMode);
        }

        public void setInited(boolean inited) {
            this.inited = inited;
            setting.set(INITED, inited);
        }

        public void setScanPath(String scanPath) {
            this.scanPath = scanPath;
            setting.set(SCAN_PATH, scanPath);
        }

        public void setDatabaseUsername(String databaseUsername) {
            this.databaseUsername = databaseUsername;
            setting.set(DATABASE_USERNAME, databaseUsername);
        }

        public void setDatabasePassword(String databasePassword) {
            this.databasePassword = databasePassword;
            setting.set(DATABASE_PASSWORD, databasePassword);
        }

        public void setDatabaseUrl(String databaseUrl) {
            this.databaseUrl = databaseUrl;
            setting.set(DATABASE_URL, databaseUrl);
        }

        public void flush() throws FileNotFoundException {
            this.setting.flushData();
        }
    }

}
