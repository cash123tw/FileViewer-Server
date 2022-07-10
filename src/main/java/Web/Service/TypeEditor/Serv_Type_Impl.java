package Web.Service.TypeEditor;

import Data.Entity.FileType;
import Data.Repository.FileTypeRepository;
import Web.Service.FileDataEditor.Serv_DataEditor_Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Service
public class Serv_Type_Impl implements Serv_Type {

    @Autowired
    private FileTypeRepository fileTypeRep;
    @Autowired
    private Serv_DataEditor_Impl dataService;

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
        if (type == null) {
            type = new FileType();
            type.setTypeName(typeName);
            type = saveType(type);
        }

        return type;
    }

    @Override
    public List<FileType> findAllFileType() {
        return fileTypeRep.findAll();
    }

    public FileType getFileType(Path path) {
        FileType result;
        String path_str
                = path.toString();
        int index1 = path_str.lastIndexOf('/');
        path_str
                = path_str.substring(index1 == -1 ? 0 : index1);
        int index2
                = path_str.lastIndexOf(".");
        if (index2 == 0 || index2 == -1) {
            result = findTypeOrCreate(dataService.TAG_NAME_FILE);
        } else {
            String type = path_str.substring(index2+1);
            result = findTypeOrCreate(type);
        }
        return result;
    }
}
