package Web.Service.TypeEditor;

import Data.Entity.FileType;
import Data.Repository.FileTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Serv_Type_Impl implements Serv_Type{

    @Autowired
    private FileTypeRepository fileTypeRep;

    @Override
    public FileType findType(String typeName) {
        return fileTypeRep.findByName(typeName.toLowerCase());
    }

    @Override
    public FileType saveType(FileType type) {
        type.setTypeName(type.getTypeName().toLowerCase());
        return fileTypeRep.save(type);
    }

    @Override
    public FileType findTypeOrCreate(String typeName) {
        FileType type
                = findType(typeName);
        if(type == null){
            type = new FileType();
            type.setTypeName(typeName);
            type = saveType(type);
        }

        return type;
    }
}
