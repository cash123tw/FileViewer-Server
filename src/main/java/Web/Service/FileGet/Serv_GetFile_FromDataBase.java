package Web.Service.FileGet;

import Bean.FileDetail;
import Web.Bean.FileDetailResult;
import Web.Bean.FileDetail_Impl;
import Web.Bean.RequestResult;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface Serv_GetFile_FromDataBase {

    RequestResult<List<FileDetailResult>> listFile(Integer root_id);

    RequestResult<List<FileDetailResult>> searchFile(Integer root_id, String fileName);

    RequestResult<FileDetailResult> getFile(Integer id);

    RequestResult<List<FileDetailResult>> getPathDirectory(String path);
}

