package Web.Service.FileGet;

import Bean.FileDetail;
import Web.Bean.RequestResult;
import org.springframework.stereotype.Service;

import java.util.List;

public interface Serv_GetFile {

    RequestResult<List<FileDetail>> getFileList(String rootPath) throws Exception;

    RequestResult<List<FileDetail>> searchFile(String path,String fileName) throws Exception;

    RequestResult<FileDetail> getFileDetail(String path) throws Exception;

}
