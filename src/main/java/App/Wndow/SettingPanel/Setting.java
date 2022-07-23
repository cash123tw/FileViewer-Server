package App.Wndow.SettingPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Setting extends JPanel {

    private Setting(LayoutManager layout) {
        super(layout);
    }

    private Setting() {
    }

    public static class SettingPanelBuilder{

        private Map<JLabel,JComponent> textField = new HashMap<>();

        private SettingPanelBuilder(){}

        public static SettingPanelBuilder getBuilder(){
            return new SettingPanelBuilder();
        }

        public SettingPanelBuilder setSettingTextField(String textLabel){
            JLabel jLabel = new JLabel(textLabel);
            JTextField jTextField = new JTextField(16);
            textField.put(jLabel,jTextField);
            return this;
        }

        public Setting build(){
            Setting setting = new Setting();
            setting.setLayout(new GridLayout(10,1));
            textField.forEach((label,field)->{
                JPanel jPanel = new JPanel(new FlowLayout());
                jPanel.setSize(400,50);
                jPanel.add(label);
                jPanel.add(field);
                setting.add(jPanel);
            });
            return setting;
        }
    }
}
