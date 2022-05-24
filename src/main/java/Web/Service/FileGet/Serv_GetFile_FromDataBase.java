package Web.Service.FileGet;

import Bean.FileDetail;
import Data.Entity.FilePath;
import Web.Bean.FileDetailResult;
import Web.Bean.FileDetail_Impl;
import Web.Bean.RequestResult;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileNotFoundException;
import java.util.List;

public interface Serv_GetFile_FromDataBase {

    List<FilePath> listFile(Integer root_id);

    List<FilePath> searchFile(Integer root_id, String fileName);

    FilePath getFile(Integer id) throws FileNotFoundException;

    List<FilePath> getPathDirectory(String path);
}

