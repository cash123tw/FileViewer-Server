package App.ConfigPanel;

import App.AppStarter;
import App.Init.InitHinernateParam;
import lombok.Getter;
import org.springframework.security.config.annotation.web.configurers.UrlAuthorizationConfigurer;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static App.Init.InitHinernateParam.StartMode.*;

public class ConfigSettingPanel {

    private final StartSetting startSetting;
    private final Scanner scanner;
    private final String menu1 = """
            啟動選項 :
                [1] 系統啟動
                [2] 參數設置
                [3] 系統重置
            輸入選項 : """;

    private final String show_param = """
            參數表 : 
                [--系統--]
                [1]掃描路徑 : %s
                **(路徑不可以包含中文)**
                [2]啟動模式 : %s (RESCAN,NON)
                [--資料庫--] 
                [3]帳號 : %s
                [4]密碼 : %s
                [5]URL : %s
                **(jdbc:mysql://(your database url)/(database name)?&createDatabaseIfNotExist=true)**
                **Only support MySQL 8** 
                [6]設定完成
            """;
    private final String example = """
            **欲設定參數請輸入 (號碼)(空格)(參數)**
               範例 -> 1 C:/test(設定掃描路徑C:/test)
               範例 -> 3 root(設定帳號root)
               欲輸入數值是空值的話，輸入null。""";

    public ConfigSettingPanel(String location, Scanner scanner) throws FileNotFoundException {

        Path target = Path.of(location);
        if (!Files.exists(target)) {
            throw new FileNotFoundException(String.format(
                    "File [%s] not found", target.toAbsolutePath()
            ));
        }
        this.startSetting
                = new StartSetting(ApplicationSetting.getApplicationSetting(location));
        this.scanner
                = scanner;
    }

    public void StartMenu0() throws FileNotFoundException {
        Boolean alreadyConfig = startSetting.alreadyConfig;
        if (Objects.isNull(alreadyConfig) || alreadyConfig == false) {
            startSetting.setAlreadyConfig(true);
            menu2();
        } else {
            System.out.print(menu1);
            out:
            while (scanner.hasNext()) {
                String opt
                        = scanner.nextLine();
                switch (opt.trim()) {
                    case "1" -> {
                        break out;
                    }
                    case "2" -> {
                        menu2();
                    }
//                    System Reset
                    case "3" ->{
                        System.out.println("確定執行此選項，數據將全部被清空。 Y/N");
                        opt = scanner.nextLine();
                        switch (opt.trim().toUpperCase()){
                            case "Y"->{
                                SystemReset();
                                System.out.println("系統重置設定完成");
                                break out;
                            }
                            default -> {
                                System.out.println("取消重置");
                            }
                        }
                    }
                    default -> {
                        System.err.println("-----[無效選項]-----");
                    }
                }
                System.out.print(menu1);
            }
        }
    }

    public Boolean isInitStart(){
        Boolean configStart = startSetting.getAlreadyConfig();
        return configStart==null?true:!configStart;
    }

    public void initStartOk() throws FileNotFoundException {
        startSetting.setAlreadyConfig(true);
        startSetting.setStartMode(RESCAN);
        startSetting.flush();
    }

    private void menu2() throws FileNotFoundException {
        System.out.print(Show_param());
        System.out.println(example);
        while (scanner.hasNextLine()) {
            String opt
                    = scanner.nextLine();
            String[] opts
                    = opt.trim().split(" ");
            if (opts.length == 1 && opts[0].equals("6")) {
                startSetting.setAlreadyConfig(true);
                startSetting.flush();
                System.out.println(Show_param());
                System.out.println("-----[設定完成]-----");
                return;
            }
            if (opts.length < 2) {
                System.err.println("輸入錯誤，請記得加入空格。");
                System.out.println(example);
                continue;
            } else {
                String num = opts[0];
                String value = "";

                for (int i = 1; i < opts.length; i++) {
                    value = value.concat(opts[i]).concat(" ");
                }
                value = value.trim();

                try {
                    int i = Integer.parseInt(num);
                    settingValue(i, value);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                } catch (Exception e) {
                    System.err.println("輸入錯誤");
                }
                System.out.println(Show_param());
                System.out.println(example);
            }
        }
    }

    private void settingValue(int num, String value) {
        if (value.equals("null")) {
            value = "";
        }

        switch (num) {
            case 1 -> {
                if (!Files.exists(Path.of(value))) {
                    throwArgumentException(String.format("路徑 [%s] 不存在", value));
                }
                startSetting.setScanPath(value);
            }
            case 2 -> {
                InitHinernateParam.StartMode startMode = valueOf(value.toUpperCase());
                if (Objects.isNull(startMode)||startMode.equals(NEW)) {
                    throwArgumentException(String.format("選項 [%s] 不存在", value));
                }
                startSetting.setStartMode(startMode);
            }
            case 3 -> {
                startSetting.setDatabaseUsername(value);
            }
            case 4 -> {
                startSetting.setDatabasePassword(value);
            }
            case 5 -> {
                startSetting.setDatabaseUrl(value);
            }
            case 6 -> {
                break;
            }
            default -> {
                throwArgumentException("選項輸入錯誤");
            }
        }
    }

    private void SystemReset() throws FileNotFoundException {
        startSetting.setAlreadyConfig(false);
        startSetting.setDatabasePassword("");
        startSetting.setDatabaseUsername("");
        startSetting.setDatabaseUrl("");
        startSetting.setStartMode(NEW);
        startSetting.setScanPath("./");
        startSetting.flush();
    }


    private void throwArgumentException(String msg) {
        throw new IllegalArgumentException(msg);
    }

    private String Show_param() {
        return String.format(show_param,
                startSetting.getScanPath(),
                startSetting.getStartMode(),
                startSetting.getDatabaseUsername(),
                startSetting.getDatabasePassword(),
                startSetting.getDatabaseUrl());
    }

    private static class StartSetting {
        private final String SCAN_PATH = "file-explore.explore.scan-path";
        private final String START_MODE = "file-explore.explore.start-mode";
        private final String SETTING = "file-explore.already-config";
        private final String DATABASE_USERNAME = "spring.datasource.username";
        private final String DATABASE_PASSWORD = "spring.datasource.password";
        private final String DATABASE_URL = "spring.datasource.url";

        private ApplicationSetting setting;

        @Getter
        private String scanPath;
        @Getter
        private String startMode;
        @Getter
        private Boolean alreadyConfig;
        @Getter
        private String databaseUsername;
        @Getter
        private String databasePassword;
        @Getter
        private String databaseUrl;

        public StartSetting(ApplicationSetting setting) {
            this.setting = setting;
            this.scanPath = setting.get(SCAN_PATH, String.class);
            this.startMode = setting.get(START_MODE, String.class);
            this.alreadyConfig = setting.get(SETTING, Boolean.class);
            this.databaseUsername = setting.get(DATABASE_USERNAME, String.class);
            this.databasePassword = setting.get(DATABASE_PASSWORD, String.class);
            this.databaseUrl = setting.get(DATABASE_URL, String.class);
        }

        public void setStartMode(InitHinernateParam.StartMode startMode) {

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

        public void setScanPath(String scanPath) {
            this.scanPath = scanPath;
            setting.set(SCAN_PATH, scanPath);
        }

        public void setAlreadyConfig(Boolean alreadyConfig) {
            this.alreadyConfig = alreadyConfig;
            setting.set(SETTING, alreadyConfig);
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
            databaseUrl = databaseUrl.concat("?&createDatabaseIfNotExist=true");
            this.databaseUrl = databaseUrl;
            setting.set(DATABASE_URL, databaseUrl);
        }

        public void flush() throws FileNotFoundException {
            this.setting.flushData();
        }
    }

}
