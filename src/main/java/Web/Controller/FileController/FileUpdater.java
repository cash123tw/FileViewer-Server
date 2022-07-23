package Web.Controller.FileController;

import Data.Entity.FilePath;
import Data.Entity.Tag;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import Web.Service.FileIO.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class FileUpdater {

    private FileUpload fileUploadService;
    private Serv_GetFile_FromDatabase_Impl fileService;

    @Autowired
    public FileUpdater(FileUpload fileUploadService, Serv_GetFile_FromDatabase_Impl fileService) {
        this.fileUploadService = fileUploadService;
        this.fileService = fileService;
    }

    @ModelAttribute("target")
    public FilePath parseToFilePath(FilePath filePath,
                                    @RequestParam(value = "tags.name", required = false, defaultValue = "") List<String> new_tag,
                                    @RequestParam(value = "tags.name[]", required = false, defaultValue = "") List<String> new_tags) {
        if (Objects.isNull(filePath.getParentPath())) {
            throw new IllegalArgumentException("ParentPath not exists");
        }
        String file_name = filePath.getFile_name();
        if (Objects.isNull(file_name) || file_name.matches("[\\S]*[\\\\/:*?\"<>|.][\\S]*")) {
            throw new IllegalArgumentException("FileName empty or contains \\\\/:*?\"<>|. ");
        }
        Set<Tag> tags = new_tags
                .stream()
                .map(Tag::new)
                .collect(Collectors.toSet());
        Set<Tag> result = new_tag
                .stream()
                .map(Tag::new)
                .collect(Collectors.toSet());
        tags.addAll(result);

        if (Objects.nonNull(filePath.getTags())) {
            filePath.getTags().addAll(tags);
        }

        filePath.setTags(tags);

        return filePath;
    }

    @PostMapping
    public FilePath createFilePath(@ModelAttribute("target") FilePath filePath,
                                   @RequestParam(value = "file") MultipartFile file)
            throws Exception {

        FilePath result = fileUploadService
                .SaveNewDataToLocal(filePath, file);
        FilePath parent
                = result.getParentPath();
        if (Objects.nonNull(parent)) {
            parent.setTags(null);
            parent.setParentPath(null);
        }
        return result;
    }

    @PutMapping
    public ResponseEntity<String> alterFilePath(@ModelAttribute("target") FilePath filePath) throws Exception {
        fileUploadService.UpdateFilePath(filePath);
        return ResponseEntity
                .status(HttpStatus.RESET_CONTENT)
                .body("Save Complete");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteFilePath(@ModelAttribute("target") FilePath filePath) throws FileUpload.FileVersionNotMatchException, FileNotFoundException, IllegalAccessException, URISyntaxException {
        fileUploadService.DeleteFilePath(filePath);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{url:'/'}");
    }

}
