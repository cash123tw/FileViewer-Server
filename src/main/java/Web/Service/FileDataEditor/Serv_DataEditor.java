package Web.Service.FileDataEditor;

import Bean.FileDetail;
import Data.Entity.FilePath;
import Data.Entity.Tag;

import java.util.Set;

public interface Serv_DataEditor {

    boolean saveDataToDataBase(FilePath filePath);
    boolean saveDataToDataBase(FileDetail fileDetail);
    boolean saveDataToDataBase(FileDetail fileDetail, Set<Tag> tags);

}
