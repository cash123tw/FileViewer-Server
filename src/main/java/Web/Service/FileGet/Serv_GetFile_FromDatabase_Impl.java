package Web.Service.FileGet;

import Data.Entity.FilePath;
import Data.Entity.FileType;
import Data.Repository.FilePathRepository;
import Web.Bean.FileDetailResult;
import Web.Bean.RequestResult;
import Web.Service.TypeEditor.Serv_Type_Impl;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Serv_GetFile_FromDatabase_Impl implements Serv_GetFile_FromDataBase {

    @Autowired
    private FilePathRepository filePathRepository;
    @Autowired
    private Serv_Type_Impl typeService;
    private FileType dir_type;

    @Override
    public List<FilePath> listFile(@NotNull Integer root_id) {

        List<FilePath> paths;
        if (root_id == null) {
            paths = filePathRepository
                    .ListRootChild();
        } else {
            paths = filePathRepository
                    .findChildById(Arrays.asList(root_id));
        }

        return paths;
    }

    public List<FilePath> listFile(@NotNull String path) {
        FilePath filePath
                = filePathRepository.findFilePathByPath(Paths.get(path));
        return listFile(filePath.getId());
    }

    @Override
    public List<FilePath> searchFile(Integer root_id, String fileName) {

        List<Integer> ids
                = filePathRepository.findAllByRootIdAndFileName(root_id, fileName);

        List<FilePath> lists =
                filePathRepository.findFilePathsByIdIsInIds(ids);

        return lists;
    }

    public List<FilePath> searchFile(String path, String fileName) {

        FilePath target
                = filePathRepository.findFilePathByPathIsDirType0(Paths.get(path), getDirType().getTypeName());
        List<Integer> ids
                = filePathRepository.findAllByRootIdAndFileName(target.getId(), fileName);

        List<FilePath> lists =
                filePathRepository.findFilePathsByIdIsInIds(ids);

        return lists;
    }

    public void deleteFilePath(Integer id) throws FileNotFoundException {
        FilePath filePath = getFile(id);
        deleteFilePath(filePath);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteFilePath(FilePath filePath) {
        filePathRepository.delete(filePath);
    }

    @Override
    public FilePath getFile(Integer id) throws FileNotFoundException {

        FilePath file
                = filePathRepository.findByIdAll(id);

        if (Objects.nonNull(file)) {
            return file;
        } else {
            throw new FileNotFoundException("File Not Exists");
        }
    }

    public FilePath getFileByFilePath(String path){
        return filePathRepository.findFilePathByPath(Paths.get(path));
    }

    public List<FilePath> getPathDirectory(String path) {
        List<FilePath> result;
        if (path == null || path.equals("/") || path.equals("")) {
            result = listFile("/");
        } else {
            FilePath filePath
                    = filePathRepository.findPathIsDirType(Paths.get(path), getDirType().getTypeName());
            result = listFile(filePath.getId());
        }

        return result;
    }

    public <T> RequestResult<T> createRequestResult(boolean success, T obj, String msg) {
        RequestResult<T> result
                = new RequestResult<>();
        if (success) {
            result.setResult(RequestResult.RequestResultType.SUCCESS);
        } else {
            result.setResult(RequestResult.RequestResultType.FAIL);
        }

        result.setMessage(msg);
        result.setObj(obj);

        return result;
    }

    public FileDetailResult transferFilePath2FileFileDetailResult(FilePath filePath) {

        FilePath parent
                = filePath.getParentPath();
        FileType fileType
                = filePath.getFileType();
        boolean dir
                = fileType.equals(getDirType());

        FileDetailResult result = new FileDetailResult(
                filePath.getId(),
                filePath.getFile_name(),
                filePath.getPath(),
                fileType,
                filePath.getTags(),
                dir
        );

        return result;
    }

    public Data.Entity.FileType getDirType() {
        if (Objects.isNull(dir_type)) {
            dir_type = typeService.findTypeOrCreate("directory");
        }
        return dir_type;
    }
}
