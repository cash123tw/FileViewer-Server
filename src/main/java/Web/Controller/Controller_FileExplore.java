package Web.Controller;

import Bean.FileDetail;
import Data.Entity.FilePath;
import Data.Entity.FileType;
import Data.Entity.Tag;
import Web.Bean.FileDetailResult;
import Web.Bean.RequestResult;
import Web.Service.FileGet.Serv_GetFile_FromDataBase;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.java.Log;
import net.bytebuddy.implementation.bind.annotation.This;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/explore")
@Log
public class Controller_FileExplore {

    @Autowired
    private Serv_GetFile_FromDatabase_Impl fileService;

    @GetMapping({"/"})
    public ModelAndView requestWebPage(ModelAndView mav) {
        mav.setViewName("/seeker2");
        return mav;
    }

//    @GetMapping({"/search"})
//    public ModelAndView requestSearchPage(ModelAndView mav) {
//        List<FilePath> files = fileService.listFile("/");
//        mav.addObject("files", files);
//        mav.addObject("move", "search");
//        mav.setViewName("/seeker2");
//        return mav;
//    }

    /**
     * Get file list by root id.
     */
    @GetMapping("")
    public List<FilePath> getAllFilePath(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false, defaultValue = "/") String path) {
        List<FilePath> result;
        if (Objects.nonNull(id)) {
            result = fileService.listFile(id);
        } else {
            result = fileService.listFile(path);
        }

        result.stream()
                .forEach(this::ensureParentPathNotLoopGet);

        return result;
    }

    /**
     * Search in path by file name.
     */
    @PostMapping("/findByParam")
    @CrossOrigin()
    public List<FilePath> searchFile(@RequestBody SearchParam searchParam) {
        List<FilePath> filePaths
                = fileService.searchFilePath(searchParam);
        filePaths.stream()
                .forEach(this::ensureParentPathNotLoopGet);

        return filePaths;
    }

    @GetMapping("/back")
    public List<FilePath> backToPath(String path) {
        List<FilePath> result
                = getAllFilePath(null, path);
        result.stream()
                .forEach(this::ensureParentPathNotLoopGet);
        return result;
    }

    public FilePath ensureParentPathNotLoopGet(FilePath filePath) {
        filePath.setParentPath(null);
//        FilePath parentPath = filePath.getParentPath();
//        if (Objects.nonNull(parentPath)) {
//            parentPath.setParentPath(null);
//        }
        return filePath;
    }

    @Data
    public static class SearchParam {
        private Integer pathId;
        private String pathName;
        private Integer fileTypeId;
        private String fileName;
        private Set<Tag> tags;
        private Integer page;

        public FileType getFileType() {
            return nonNull(fileTypeId) ? new FileType() {{
                setId(fileTypeId);
            }} : null;
        }

        public String getFileName() {
            return Strings.isEmpty(fileName) ? "" : fileName;
        }

        public boolean isEmpty() {
            return
                            Objects.isNull(fileTypeId) &&
                            Strings.isEmpty(fileName) &&
                            (Objects.isNull(tags) || Objects.equals(tags.size(), 0));
        }

        private boolean nonNull(Object obj) {
            return Objects.nonNull(obj);
        }
    }
}
