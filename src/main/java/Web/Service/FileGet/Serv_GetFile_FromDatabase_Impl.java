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
    public RequestResult<List<FileDetailResult>> listFile(@NotNull Integer root_id) {
        root_id =
                (root_id == null) ? 0 : root_id;
        List<FileDetailResult> list
                = null;
        RequestResult<List<FileDetailResult>> result;

        try {
            List<FilePath> paths;
            if (root_id == 0) {
                paths = filePathRepository
                        .ListRootChild();
            } else {
                paths = filePathRepository
                        .findChildById(Arrays.asList(root_id));
            }
            list = paths
                    .stream()
                    .map(this::transferFilePath2FileFileDetailResult)
                    .collect(Collectors.toList());

            result = createRequestResult(true, list, root_id + "");
        } catch (Exception e) {
            result = createRequestResult(false, null, e.getMessage());
        }
        return result;
    }

    @Override
    public RequestResult<List<FileDetailResult>> searchFile(Integer root_id, String fileName) {

        List<FilePath> filePaths
                = filePathRepository.findAllByRootIdAndFileName(root_id, fileName);

        List<FileDetailResult> lists = filePaths
                .stream()
                .map(this::transferFilePath2FileFileDetailResult)
                .collect(Collectors.toList());

        RequestResult<List<FileDetailResult>> result
                = createRequestResult(true, lists, root_id + "");

        return result;
    }

    @Override
    public RequestResult<FileDetailResult> getFile(Integer id) {

        RequestResult<FileDetailResult> result;
        FilePath file
                = filePathRepository.findByIdAll(id);

        if (Objects.nonNull(file)) {
            FilePath parent
                    = file.getParentPath();
            FileDetailResult obj
                    = transferFilePath2FileFileDetailResult(file);
            obj.setFile_tags(file.getTags(true));
            result = createRequestResult(true, obj, id + "");

            if (Objects.nonNull(parent)) {
                result.getObj().addOthers("parent",
                        new FileDetailResult(
                                parent.getId(),
                                parent.getFile_name(),
                                parent.getPath(),
                                null,
                                null,
                                true
                        )
                );
            }
        } else {
            result = createRequestResult(false, null, "File not Exists");
        }

        return result;
    }

    public RequestResult<List<FileDetailResult>> getPathDirectory(String path) {
        RequestResult<List<FileDetailResult>> result;
        if (path == null || path.equals("/") || path.equals("")) {
            result = listFile(null);
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

        String path = null;
        FilePath parent
                = filePath.getParentPath();
        Set<FileType> types
                = filePath.getFileType();
        boolean dir
                = types.contains(getDirType());
        if (parent != null) {
            path = parent.getPath();
        }

        FileDetailResult result = new FileDetailResult(
                filePath.getId(),
                filePath.getFile_name(),
                path,
                types,
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
