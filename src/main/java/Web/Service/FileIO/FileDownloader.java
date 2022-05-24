package Web.Service.FileIO;

import Bean.PathProvider;
import Data.Entity.FilePath;
import Web.Bean.FileDetailResult;
import Web.Bean.RequestResult;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import Web.Service.FileGet.Serv_GetFile_impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class FileDownloader {

    private PathProvider pathProvider;
    private Serv_GetFile_FromDatabase_Impl fileService;

    public FileDownloader(PathProvider pathProvider, Serv_GetFile_FromDatabase_Impl fileService) {
        this.pathProvider = pathProvider;
        this.fileService = fileService;
    }

    public File getFile(Integer id) throws FileNotFoundException, IllegalAccessException {
        FilePath file
                = fileService.getFile(id);
            File result
                    = pathProvider.getFile(file.getPath());
        return result;
    }

}
