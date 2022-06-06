package Web.Controller;

import Bean.FileDetail;
import Data.Entity.FilePath;
import Web.Bean.FileDetailResult;
import Web.Bean.RequestResult;
import Web.Service.FileGet.Serv_GetFile_FromDataBase;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/explore")
@Log
public class Controller_FileExplore {

    @Autowired
    private Serv_GetFile_FromDatabase_Impl fileService;

    @GetMapping({"/"})
    public ModelAndView requestWebPage(ModelAndView mav) {
        mav.setViewName("/seeker");
        return mav;
    }

    @GetMapping({"/test"})
    public ModelAndView requestSearchPage(ModelAndView mav) {
        List<FilePath> files = fileService.listFile("/");
        mav.addObject("files",files);
        mav.addObject("move","search");
        mav.setViewName("/seeker");
        return mav;
    }

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
    @GetMapping("/search")
    public List<FilePath> searchFile(String path, String fileName) {
        List<FilePath> result
                = fileService.searchFile(path, fileName);
        result.stream()
                .forEach(this::ensureParentPathNotLoopGet);
        return result;
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

}
