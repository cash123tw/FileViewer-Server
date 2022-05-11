package Web.Bean;

import Bean.FileDetail;
import Data.Entity.FileType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class FileDetail_Impl extends FileDetail {

    private int file_id;
    private Set<FileType> fileType;


    public FileDetail_Impl(Bean.FileType type,
                           String fileName,
                           String filePath,
                           int file_id,
                           Set<FileType> fileType) {
        super(type, fileName, filePath);
        this.file_id = file_id;
        this.fileType = fileType;
    }
}
