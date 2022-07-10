package Web.Controller;

import Data.Entity.FileType;
import Web.Service.TypeEditor.Serv_Type_Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fileType")
public class Controller_FileType {

    @Autowired
    private Serv_Type_Impl fileTypeService;

    @GetMapping("")
    public List<FileType> getFileType(){
        return fileTypeService.findAllFileType();
    }

}
