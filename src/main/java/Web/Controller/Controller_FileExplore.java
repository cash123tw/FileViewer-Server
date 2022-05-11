package Web.Controller;

import Bean.FileDetail;
import Web.Bean.FileDetailResult;
import Web.Bean.RequestResult;
import Web.Service.FileGet.Serv_GetFile_FromDataBase;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/explore")
@Log
public class Controller_FileExplore {

    @Autowired
    private Serv_GetFile_FromDataBase fileService;

    @GetMapping({"", "/*"})
    public ModelAndView requestWebPage(ModelAndView mav) {
        mav.setViewName("/seeker");
        return mav;
    }

    @PostMapping("/list")
    public RequestResult getListFile(
            @RequestParam(name = "path", required = false, defaultValue = "0") int path) {
        try {
            RequestResult<List<FileDetailResult>> fileList
                    = fileService.listFile(path);
            log.info(String.format("List [%s]", path));
            return fileList;

        } catch (Exception e) {
            return handleException(e, "Error form list file.");
        }
    }

    @PostMapping("/search")
    public RequestResult searchFile(@RequestParam(name = "path", required = false, defaultValue = "1") int path,
                                    @RequestParam(name = "fileName") String fileName) {

        try {
            log.info(String.format("Search file in path [%s] file name : [%s]", path, fileName));
            RequestResult<List<FileDetailResult>> result
                    = fileService.searchFile(path, fileName);
            return result;
        } catch (Exception e) {
            return handleException(e, "Error from search file.");
        }
    }

    @GetMapping(value = "/detail/{id}")
    public ModelAndView getFileDetailPage(@PathVariable(name = "id") Integer id, ModelAndView mav) {
        mav.setViewName("/detail");
        return mav;
    }

    @PostMapping(value = "/detail/{id}")
    public RequestResult getFileDetail(@PathVariable(name = "id") Integer id) {
        RequestResult result = null;

        try {
            result
                    = fileService.getFile(id);
        } catch (Exception e) {
            result = handleException(e, String.format("Get File ID : [%d] Error", id));
            e.printStackTrace();
        }

        return result;
    }

    @PostMapping("/back_by_path")
    public RequestResult moveBackByPath(@RequestParam(name = "path",required = false,defaultValue = "") String path){
        RequestResult<List<FileDetailResult>> result
                = fileService.getPathDirectory(path);
        log.info(String.format("For back List [%s]",path));
        return result;
    }

    @PostMapping("/back")
    public RequestResult moveBackFileList(@RequestParam(name = "path", required = false) int path) {
        int id = 1;
        RequestResult<FileDetailResult> result
                = fileService.getFile(path);
        FileDetailResult f1
                = result.getObj();
        if (Objects.nonNull(f1)) {
            Object parent
                    = f1.getOthers("parent");
            if (Objects.nonNull(parent)) {
                id = ((FileDetailResult) parent).getFile_id();
            }
        }

        return getListFile(id);
    }

    public RequestResult<String>
    handleException(Exception e, String message) {
        RequestResult<String> result
                = new RequestResult<>();

        result.setResult(RequestResult.RequestResultType.FAIL);
        result.setMessage(message);
        result.setObj(e.getMessage());

        return result;
    }

}
