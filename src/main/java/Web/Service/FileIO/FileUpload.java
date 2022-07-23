package Web.Service.FileIO;

import Bean.FileDetail;
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
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
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
    private Serv_DataEditor_Impl dataService;
    private FileDownloader fileDownloader;

    private FilePathRepository fileRep;
    private FileSaverWorker saverWorker;
    private FileExploreWorker finderWorker;

    @Autowired
    public FileUpload(Serv_Type_Impl typeService, Serv_GetFile_FromDatabase_Impl fileService, Serv_Tag_Provider_Impl tagService, Serv_DataEditor_Impl dataService, FileDownloader fileDownloader, FilePathRepository fileRep, FileSaverWorker saverWorker, FileExploreWorker finderWorker) {
        this.typeService = typeService;
        this.fileService = fileService;
        this.tagService = tagService;
        this.dataService = dataService;
        this.fileDownloader = fileDownloader;
        this.fileRep = fileRep;
        this.saverWorker = saverWorker;
        this.finderWorker = finderWorker;
    }

    public FilePath createNewDirectory(Path path, String dir_name) throws FileAlreadyExistsException, IllegalAccessException, FileNotFoundException {
        if (Strings.isEmpty(dir_name)) {
            throw new IllegalAccessException("資料夾名稱不為空!");
        }

        path = Paths.get(path.toString(), dir_name);
        FilePath filePath = fileService.getFileByFilePath(path.toString());

        if (Objects.nonNull(filePath)) {
            throw new FileAlreadyExistsException(String.format("資料夾 [%s] 已經存在 ", dir_name));
        }

        path = finderWorker.getPathProvider().getPath(path.toString());
        File file = makeDirectory(path);
        filePath = new FilePath(
                dir_name,
                Path.of(finderWorker.getPathProvider().hideRealPath(path.toString())),
                fileService.getDirType());
        return dataService.saveDataToDataBase(filePath);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public FilePath SaveNewDataToLocal(FilePath filePath,
                                       MultipartFile file) throws Exception {
        FilePath target
                = saveNewFile(filePath, file);
        target = fileService.getFile(target.getId());
        return target;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public FilePath UpdateFilePath(FilePath filePath) throws Exception {
        Boolean success = false;
        String new_file_name = filePath.getFile_name();

        Assert.notNull(filePath.getId(), "FilePath Id is null\ncan't not update");
        Assert.notNull(filePath.getParentPath(), "ParentPath is null\ncan't not update");
        Assert.hasText(new_file_name, "File name can't not be empty");

        String origin_file_name = "";
        FileType fileType = filePath.getFileType();
        DeleteFileTask deleteFileTask = null;
        SaveFileTask saveFileTask = null;
        FilePath newFilePath = null;
        FilePath old_filePath = fileService.getFile(filePath.getId());
        String old_path = old_filePath.getPath();
        File old_file = fileDownloader.getFile(old_filePath.getId());


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

            origin_file_name =
                    new_file_name.concat(".").concat(fileType.getTypeName());
            newFilePath =
                    makeFilePath(filePath, origin_file_name);

            if (fileMove) {
                String full_file_name
                        = getLastFileNameFromPath(Paths.get(old_path)).replace(old_file_name, new_file_name);
                MockMultipartFile multiPartFile
                        = new MockMultipartFile(full_file_name, new FileInputStream(old_file));
                saveFileTask = saveFileToLocal(multiPartFile, newFilePath.getPath());
                checkFilePathExists(newFilePath);
            }

            newFilePath = saveFilePath(newFilePath);
            success = true;
        } catch (Exception e) {
            if (Objects.nonNull(saveFileTask))
                saveFileTask.rollBack();
            throw e;
        } finally {
            if (Objects.nonNull(deleteFileTask) && success) {
                deleteFileTask.flush();
            }
        }

        return newFilePath;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
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

    private File makeDirectory(Path path) throws FileAlreadyExistsException {
        File file = path.toFile();
        if (file.exists() && file.isDirectory()) {
            throw new FileAlreadyExistsException(String.format("資料夾已存在"));
        } else {
            file.mkdir();
        }

        return file;
    }

    private FilePath saveFilePath(FilePath filePath) throws FileNotFoundException, IllegalAccessException, FileSaveException {
        filePath.setVersion(UUID.randomUUID());
        filePath = dataService.saveDataToDataBase(filePath);
        return filePath;
    }

    private void checkFilePathExists(FilePath filePath) throws FileSaveException {
        FilePath checkExists
                = fileService.getFileByFilePath(filePath.getPath());
        if (Objects.nonNull(checkExists)) {
            throw new FileSaveException("File path already exists");
        }
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
        if (Paths.get(parentPath0).getNameCount() == 0) {
            parentPath0 = "";
        }
        String localPath;

        if (type.getTypeName().equals("file") || save_file_name.startsWith(".")) {
            localPath = Paths.get(parentPath0, save_file_name).toString();
        } else {
            localPath = Paths.get(parentPath0,
                    save_file_name.concat(".").concat(type.getTypeName())).toString();

        }

        filePath.setFileType(type);
        filePath.setParentPath(parentPath);
        filePath.setPath(localPath);
        filePath.setFile_name(save_file_name);
        filePath.setVersion(UUID.randomUUID());

        return filePath;
    }

    private FilePath saveNewFile(FilePath filePath, MultipartFile file)
            throws Exception {

        SaveFileTask saveFileTask = null;

        try {
            FilePath tmp = makeFilePath(filePath, file.getOriginalFilename().replaceAll("[\\\\/:*?\"<>|]", "_"));
            String path = tmp.getPath();
            saveFileTask = saveFileToLocal(file, path);
            filePath = saveFilePath(tmp);
            return filePath;
        } catch (Exception e) {
            if (Objects.nonNull(saveFileTask))
                saveFileTask.rollBack();
            throw e;
        }
    }

    private SaveFileTask saveFileToLocal(MultipartFile file, String path) throws IOException, IllegalAccessException {
        return SaveFileTask.saveFile(this.saverWorker, this.finderWorker, Paths.get(path), file.getInputStream());
    }

    public static String getLastFileNameFromPath(Path path) {
        int length = path.getNameCount();
        if (length != 0) {
            Path name = path.getName(length - 1);
            return name.toString();
        } else {
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

    @Log
    static public class SaveFileTask {
        private Path path;

        public SaveFileTask(Path path) {
            this.path = path;
        }

        static SaveFileTask saveFile(FileSaverWorker saverWorker, FileExploreWorker finder, Path path, InputStream in) throws IOException, IllegalAccessException {
            ByteBuffer bytes
                    = ByteBuffer.wrap(in.readAllBytes());
            FileSaverWorker.SaveResult result
                    = saverWorker.saveNewFile(path.toString(), "", false, false, bytes);
            if (!result.isResult()) {
                String message = result
                        .getMessages()
                        .stream()
                        .collect(Collectors.joining("\n"));
                throw new FileSaveException(message);
            }
            return new SaveFileTask(Paths.get(finder.getFile(path.toString()).getFilePath()));
        }

        public void rollBack() {
            File file = this.path.toFile();
            if (file.exists()) {
                boolean delete = file.delete();
                if (delete) {
                    log.warning(String.format("File Saver roll back ,delete file [%s]", path));
                }
            }
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
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
}
