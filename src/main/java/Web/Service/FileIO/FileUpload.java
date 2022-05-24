package Web.Service.FileIO;

import Data.Entity.FilePath;
import Data.Entity.FileType;
import Data.Entity.Tag;
import Data.Repository.FilePathRepository;
import Web.Service.FileDataEditor.Serv_DataEditor_Impl;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import Web.Service.TagService.Serv_Tag_Provider_Impl;
import Web.Service.TypeEditor.Serv_Type_Impl;
import Worker.FileExploreWorker;
import Worker.FileSaverWorker;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.standard.expression.EqualsNotEqualsExpression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@Log
public class FileUpload {

    private Serv_Type_Impl typeService;
    private Serv_GetFile_FromDatabase_Impl fileService;
    private Serv_Tag_Provider_Impl tagService;
    private FileDownloader fileDownloader;

    private FilePathRepository fileRep;
    private FileSaverWorker saverWorker;
    private FileExploreWorker finderWorker;

    @Autowired
    public FileUpload(Serv_Type_Impl typeService, Serv_GetFile_FromDatabase_Impl fileService, Serv_Tag_Provider_Impl tagService, FileDownloader fileDownloader, FilePathRepository fileRep, FileSaverWorker saverWorker, FileExploreWorker finderWorker) {
        this.typeService = typeService;
        this.fileService = fileService;
        this.tagService = tagService;
        this.fileDownloader = fileDownloader;
        this.fileRep = fileRep;
        this.saverWorker = saverWorker;
        this.finderWorker = finderWorker;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public FilePath SaveNewDataToLocal(FilePath filePath,
                                       MultipartFile file) throws Exception {
        FilePath target
                = saveFile(filePath, file);
        target = fileService.getFile(target.getId());
        return target;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public FilePath UpdateFilePath(FilePath filePath) throws Exception {
        String new_file_name = filePath.getFile_name();

        Assert.notNull(filePath.getId(), "FilePath Id is null\ncan't not update");
        Assert.notNull(filePath.getParentPath(), "ParentPath is null\ncan't not update");
        Assert.hasText(new_file_name, "File name can't not be empty");

        DeleteFileTask deleteFileTask = null;
        FilePath newFilePath = null;
        FilePath old_filePath
                = fileService.getFile(filePath.getId());
        String old_path
                = old_filePath.getPath();
        File old_file
                = fileDownloader.getFile(old_filePath.getId());

        if (!Objects.equals(old_filePath.getVersion(), filePath.getVersion())) {
            throw new FileVersionNotMatchException("File Version Not Match", old_filePath, filePath);
        }

        try {
            Integer parent_id = filePath.getParentPath().getId();
            Integer compare_parent_id = old_filePath.getParentPath().getId();
            String old_file_name = old_filePath.getFile_name();

            boolean fileMove
                    = (!Objects.equals(parent_id, compare_parent_id))
                    || (!Objects.equals(new_file_name, old_file_name));

            if (fileMove) {
                deleteFileTask
                        = new DeleteFileTask(fileDownloader, old_file);
            }

            newFilePath
                    = saveFilePath(makeFilePath(filePath, new_file_name));

            if (fileMove) {
                String full_file_name
                        = getLastFileNameFromPath(Paths.get(old_path)).replace(old_file_name,new_file_name);
                MockMultipartFile multiPartFile
                        = new MockMultipartFile(full_file_name, new FileInputStream(old_file));
                saveFileToLocal(multiPartFile, newFilePath.getPath());
            }
        } finally {
            if (Objects.nonNull(deleteFileTask)) {
                deleteFileTask.flush();
            }
        }

        return newFilePath;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void DeleteFilePath(FilePath filePath) throws FileNotFoundException, FileVersionNotMatchException, IllegalAccessException {
        Assert.notNull(filePath.getId(), "File Id is not exists\ncan't not delete");

        FilePath target_filePath
                = fileService.getFile(filePath.getId());
        DeleteFileTask deleteFileTask = null;

        if (!Objects.equals(filePath.getVersion(), target_filePath.getVersion())) {
            throw new FileVersionNotMatchException();
        }

        try {
            File file
                    = fileDownloader.getFile(target_filePath.getId());
            deleteFileTask = new DeleteFileTask(fileDownloader, file);
        } finally {
            fileService.deleteFilePath(target_filePath);
            if (Objects.nonNull(deleteFileTask)) {
                deleteFileTask.flush();
            }
        }
    }

    private FilePath saveFilePath(FilePath filePath){
        filePath.setVersion(UUID.randomUUID());
        filePath = fileRep.save(filePath);
        return filePath;
    }

    private FilePath makeFilePath(FilePath filePath, String origin_file_name)
            throws Exception {
        Integer parentID
                = filePath.getParentPath().getId();
        Optional<FilePath> opt_parent
                = fileRep.findById(parentID);
        if (opt_parent.isEmpty())
            throw new FileSaveException("FileParent Lose");

        Set<Tag> tags = filePath.getTags();
        tags = tagService.saveTagsIfNotPresent(tags);
        filePath.setTags(tags);

        Path origin_path
                = Paths.get(origin_file_name);
        FileType type
                = typeService.getFileType(origin_path);
        String save_file_name
                = filePath.getFile_name();

        if (save_file_name == null || "".equals(save_file_name.trim())) {
            save_file_name
                    = Serv_DataEditor_Impl.removeTypeFromPath(origin_file_name);
        }

        FilePath parentPath
                = opt_parent.get();
        String parentPath0
                = parentPath.getPath();
        if(Paths.get(parentPath0).getNameCount() == 0){
            parentPath0 = "";
        }
        String localPath
                = Paths.get(parentPath0,
                save_file_name.concat(".").concat(type.getTypeName())).toString();

        filePath.setFileType(type);
        filePath.setParentPath(parentPath);
        filePath.setPath(localPath);
        filePath.setFile_name(save_file_name);
        filePath.setVersion(UUID.randomUUID());

        return filePath;
    }

    private FilePath saveFile(FilePath filePath, MultipartFile file)
            throws Exception {
        filePath = saveFilePath(makeFilePath(filePath, file.getOriginalFilename()));
        String path = filePath.getPath();
        saveFileToLocal(file, path);
        return filePath;
    }

    private void saveFileToLocal(MultipartFile file, String path) throws IOException, IllegalAccessException {
        ByteBuffer bytes
                = ByteBuffer.wrap(file.getBytes());
        FileSaverWorker.SaveResult result
                = saverWorker.saveNewFile(path, "", false, false, bytes);
        if (!result.isResult()) {
            String message = result
                    .getMessages()
                    .stream()
                    .collect(Collectors.joining("\n"));
            throw new FileSaveException(message);
        }
    }

    public static String getLastFileNameFromPath(Path path){
        int length = path.getNameCount();
        if(length!=0) {
            Path name = path.getName(length - 1);
            return name.toString();
        }else{
            throw new IllegalArgumentException("file name can't be empty");
        }
    }

    public static class FileSaveException extends FileSystemException {
        public FileSaveException(String file) {
            super(file);
        }

        public FileSaveException(String file, String other, String reason) {
            super(file, other, reason);
        }
    }

    public static class FileVersionNotMatchException extends Exception {
        private FilePath oldFilePath, newFilePath;

        public FileVersionNotMatchException() {
            super("File Version Not Match");
        }

        public FileVersionNotMatchException(String message, FilePath oldFilePath, FilePath newFilePath) {
            super(message);
            this.oldFilePath = oldFilePath;
            this.newFilePath = newFilePath;
        }

    }

    @Data
    public class DeleteFileTask {
        private FileDownloader fileDownloader;
        private File file;

        public DeleteFileTask(FileDownloader fileDownloader) {
            this(fileDownloader, null);
        }

        public DeleteFileTask(FileDownloader fileDownloader, File file) {
            this.fileDownloader = fileDownloader;
            this.file = file;
        }

        public void flush() {
            if (Objects.nonNull(this.file)) {
                log.info(String.format("Delete File [%s]", this.file.getPath()));
                if(file.exists()){
                    file.delete();
                }
            }
        }
    }
}