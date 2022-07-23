package Web.Service.FileGet;

import Bean.PathProvider;
import Data.Criteria.MineCriteria;
import Data.Entity.FilePath;
import Data.Entity.FileType;
import Data.Repository.FilePathRepository;
import Web.Bean.FileDetailResult;
import Web.Bean.RequestResult;
import Web.Controller.Controller_FileExplore;
import Web.Service.TypeEditor.Serv_Type_Impl;
import com.sun.istack.NotNull;
import org.apache.logging.log4j.util.Strings;
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

    private Integer DATA_LENGTH = 30;

    private FilePathRepository filePathRepository;
    private Serv_Type_Impl typeService;
    private MineCriteria criteria;
    private PathProvider pathProvider;

    private FileType dir_type;

    @Autowired
    public Serv_GetFile_FromDatabase_Impl(PathProvider pathProvider,FilePathRepository filePathRepository, Serv_Type_Impl typeService, MineCriteria criteria) {
        this.filePathRepository = filePathRepository;
        this.typeService = typeService;
        this.criteria = criteria;
        this.pathProvider = pathProvider;
    }


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

    public List<FilePath> searchFilePath(Controller_FileExplore.SearchParam param) {
        String pathName = param.getPathName();
        FilePath target = null;

        if (Strings.isEmpty(pathName)) {
            target = getRootFilePath();
        }else{
            target = this.getFileByFilePath(pathName);
        }

        if (Objects.isNull(target)) {
            throw new IllegalArgumentException(String.format("Path is not present", pathName));
        }else if(param.isEmpty()){
            throw new IllegalArgumentException(String.format("No param set"));
        }

        return criteria
                .findByParam(
                        target,
                        param.getFileName(),
                        param.getFileType(),
                        param.getTags(),
                        param.getPage() == null ? 0 : param.getPage() * DATA_LENGTH,
                        DATA_LENGTH);
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

    public FilePath getFileByFilePath(String path) {
        return filePathRepository.findFilePathByPath(Paths.get(path));
    }

    public FilePath getRootFilePath(){

        String searchTarget;

        if(pathProvider.isHideRealPath()){
            searchTarget = "/";
        }else{
            searchTarget = pathProvider.getRootPath();
        }

        return getFileByFilePath(searchTarget);
    }

    public Data.Entity.FileType getDirType() {
        if (Objects.isNull(dir_type)) {
            dir_type = typeService.findTypeOrCreate("directory");
        }
        return dir_type;
    }
}
