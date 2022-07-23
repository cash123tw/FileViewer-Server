package App.Wndow;

import App.AppStarter;
import App.Wndow.SettingPanel.Setting;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

public class WindowApp extends javax.swing.JFrame {

    private ConfigurableApplicationContext app;

    public WindowApp(String title) throws HeadlessException {
        super(title);

        this.setSize(600, 600);
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        Setting.SettingPanelBuilder builder = Setting.SettingPanelBuilder.getBuilder();
        Setting setting = builder
                .setSettingTextField("掃描路徑")
                .setSettingTextField("掃描模式")
                .build();
        this.add(setting, BorderLayout.CENTER);
        addLabel();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
                closeApp();
            }
        });
    }

    private void addLabel(){
        JLabel label = new JLabel("設定",SwingConstants.CENTER);
        label.setFont(new Font(Font.SERIF,Font.PLAIN,25));
        this.add(label,BorderLayout.NORTH);
    }

    public void startApp() throws IOException {
        if (Objects.isNull(this.app)) {
            this.app = SpringApplication.run(AppStarter.class);
            Desktop desktop = Desktop.getDesktop();

            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                URI uri = URI.create("http://localhost:9090/");
                desktop.browse(uri);
            }
        }
    }

    public void closeApp() {
        if (Objects.nonNull(this.app)) {
            this.app.close();
        }
    }
}
