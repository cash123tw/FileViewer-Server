package Web.Service.FileDataEditor;

import Bean.FileDetail;
import Data.Entity.FilePath;
import Data.Entity.Tag;

import java.io.FileNotFoundException;
import java.util.Set;

public interface Serv_DataEditor {

    FilePath saveDataToDataBase(FilePath filePath) throws FileNotFoundException, IllegalAccessException;
    boolean saveDataToDataBase(FileDetail fileDetail);
    boolean saveDataToDataBase(FileDetail fileDetail, Set<Tag> tags);

}
