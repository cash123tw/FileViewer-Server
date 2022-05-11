package Web.Bean;

import Data.Entity.FileType;
import Data.Entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
public class FileDetailResult {

    private Integer file_id;
    private String file_name;
    private String file_path;
    private Set<FileType> file_types;
    private Set<Tag> file_tags;
    private boolean isDir;
    private Map<String, Object> others;


    public FileDetailResult() {
        this.others = new HashMap<>();
    }

    public FileDetailResult(Integer file_id,
                            String file_name,
                            String file_path,
                            Set<FileType> file_types,
                            Set<Tag> file_tags,
                            boolean isDir) {
        this();
        this.file_id = file_id;
        this.file_name = file_name;
        this.file_path = file_path;
        this.file_types = file_types;
        this.file_tags = file_tags;
        this.isDir = isDir;
    }

    public void addOthers(String key, Object obj) {
        getOthers().put(key, obj);
    }

    public Object getOthers(String key) {
        return getOthers().get(key);
    }
}
