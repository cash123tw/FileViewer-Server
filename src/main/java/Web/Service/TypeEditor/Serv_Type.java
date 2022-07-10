package Web.Service.TypeEditor;


import Data.Entity.FileType;

import java.util.List;

public interface Serv_Type {

    FileType findType(String typeName);

    FileType saveType(FileType type);

    FileType findTypeOrCreate(String typeName);

    List<FileType> findAllFileType();

}
