package Web.Service.FileGet;

import Bean.FileDetail;
import Web.Bean.RequestResult;
import Web.Service.FileGet.Serv_GetFile;
import Worker.FileExploreWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

//@Service
public class Serv_GetFile_impl implements Serv_GetFile {

    @Autowired
    private FileExploreWorker exploreWorker;

    @Override
    public RequestResult<List<FileDetail>> getFileList(String rootPath) throws FileNotFoundException, IllegalAccessException {
        List<FileDetail> listFile
                = exploreWorker.getFileList(rootPath);

        return RequestResult.makeSuccessResult(listFile,rootPath);
    }

    @Override
    public RequestResult<List<FileDetail>> searchFile(String path,String fileName) throws IOException, IllegalAccessException {
        List<FileDetail> listFile
                = exploreWorker.SearchFile(path, fileName);

        return RequestResult.makeSuccessResult(listFile,"/");
    }

    @Override
    public RequestResult<FileDetail> getFileDetail(String path) throws FileNotFoundException, IllegalAccessException {
        FileDetail file
                = exploreWorker.getFile(path);
        return RequestResult.makeSuccessResult(file,path);
    }

}
