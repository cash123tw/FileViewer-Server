package App.ConfigPanel;

import App.AppStarter;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplicationSetting {
    private final Yaml yaml;
    private final Map<String, Object> setting;
    private final File url;

    public static ApplicationSetting getApplicationSetting(String location) throws FileNotFoundException {
        return new ApplicationSetting(location);
    }

    private ApplicationSetting(String location) throws FileNotFoundException {
        this.url
                = new File(location);
        this.yaml
                = new Yaml();
        System.out.println(url.getAbsolutePath());
        this.setting
                = yaml.load(new FileInputStream(url));
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
                        if (Objects.isNull(target)||target.getClass() == value.getClass()) {
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

    public void flushData() throws FileNotFoundException {
        OutputStreamWriter writer
                = new OutputStreamWriter(new FileOutputStream(url.getPath()));
        this.yaml.dump(this.getSetting(), writer);
    }

    public void throwTypeNotObjectException(String keyName) {
        throw new IllegalArgumentException(String.format("Can't not set value because path [%s] is not object style", keyName));
    }
}

