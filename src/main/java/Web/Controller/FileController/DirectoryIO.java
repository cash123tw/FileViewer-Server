package Web.Controller.FileController;

import Data.Entity.FilePath;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import Web.Service.FileIO.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/directory")
public class DirectoryIO {

    @Autowired
    private FileUpload fileUploadService;
    @Autowired
    private Serv_GetFile_FromDatabase_Impl fileService;

    @PostMapping("/addDirectory")
    public FilePath addDirectory(@RequestParam String name, @RequestParam String path) throws FileAlreadyExistsException, FileNotFoundException, IllegalAccessException {
        path = Paths.get(path).toString();
        if (path.equals("/")) {
            path = path.replaceFirst("/", "");
        }

        FilePath newDirectory
                = fileUploadService.createNewDirectory(Paths.get(path), name);
        newDirectory = fileService.getFile(newDirectory.getId());
        newDirectory.setParentPath(null);

        return newDirectory;
    }

}
