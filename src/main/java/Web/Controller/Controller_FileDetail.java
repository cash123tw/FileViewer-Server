package Web.Controller;

import Data.Entity.FilePath;
import Data.Entity.FileType;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileNotFoundException;
import java.util.Objects;

@Controller
@RequestMapping("/explore/detail")
public class Controller_FileDetail {

    private Serv_GetFile_FromDatabase_Impl fileService;
    private Controller_FileExplore explore;

    @Autowired
    public Controller_FileDetail(Serv_GetFile_FromDatabase_Impl fileService, Controller_FileExplore explore) {
        this.fileService = fileService;
        this.explore = explore;
    }

    @GetMapping("/")
    public ModelAndView getNewDetailPage(@RequestParam("path") String path,
                                         @ModelAttribute FilePath filePath,
                                         ModelAndView mav){
        if(path.equals("")){
            path = "/";
        }
        FilePath file = fileService.getFileByFilePath(path);
        filePath.setParentPath(file);
        filePath.setFileType(new FileType());
        mav.setViewName("/detail");
        mav.addObject("data", filePath);

        return mav;
    }

    @GetMapping("/{id}")
    public ModelAndView getDetail(@PathVariable("id") Integer id, ModelAndView mav) throws FileNotFoundException {
        FilePath file
                = fileService.getFile(id);
        FilePath parent
                = file.getParentPath();
        if (Objects.nonNull(parent)) {
            parent.setParentPath(null);
        }

        mav.setViewName("/detail");
        mav.addObject("data", file);

        return mav;
    }


}
