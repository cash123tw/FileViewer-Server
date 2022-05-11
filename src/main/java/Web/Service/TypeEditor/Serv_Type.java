package Web.Service.TypeEditor;


import Data.Entity.FileType;

public interface Serv_Type {

    FileType findType(String typeName);

    FileType saveType(FileType type);

    FileType findTypeOrCreate(String typeName);


}
