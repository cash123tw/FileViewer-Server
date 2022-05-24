package Web.Controller.FileUpdate;

import Data.Entity.FilePath;
import Data.Entity.Tag;
import Web.Service.FileIO.FileUpload;
import Web.Service.TagService.Serv_Tag_Provider_Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class FileUpdater {

    private Serv_Tag_Provider_Impl tagService;
    private FileUpload fileUploadService;

    @Autowired
    public FileUpdater(Serv_Tag_Provider_Impl tagService, FileUpload fileUploadService) {
        this.tagService = tagService;
        this.fileUploadService = fileUploadService;
    }

    @ModelAttribute("target")
    public FilePath parseToFilePath(FilePath filePath,
                                    @RequestParam(value = "tag.id", required = false, defaultValue = "") List<Integer> tag_ids,
                                    @RequestParam(value = "new_tag.name", required = false, defaultValue = "") List<String> new_tag) {
        if (Objects.isNull(filePath.getParentPath())) {
            throw new IllegalArgumentException("FileName or ParentPath not exists");
        }
        Set<Tag> tags = tag_ids
                .stream()
                .map(Tag::new)
                .collect(Collectors.toSet());
        Set<Tag> result = new_tag
                .stream()
                .map(Tag::new)
                .collect(Collectors.toSet());
        tags.addAll(result);
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
