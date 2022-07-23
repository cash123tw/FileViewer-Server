package Web.Service.WordToPdf;

import App.Init.WordToPdf;
import Bean.PathProvider;
import Data.Entity.FilePath;
import Data.Entity.FileType;
import File.Type.TypeUtils;
import Web.Controller.Controller_FileDetail;
import Web.Service.FileDataEditor.Serv_DataEditor;
import Web.Service.FileGet.Serv_GetFile_FromDataBase;
import Web.Service.FileIO.FileDownloader;
import Web.Service.TypeEditor.Serv_Type_Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

@Service
@Transactional(rollbackFor = Exception.class)
public class Serv_W2Pdf {

    private Controller_FileDetail controllerFileDetail;
    private Serv_DataEditor fileDataService;
    private Serv_Type_Impl fileTypeService;
    private Serv_GetFile_FromDataBase fileService;
    private FileDownloader fileDownloader;
    private PathProvider pathProvider;
    private WordToPdf w2p;

    @Autowired
    public Serv_W2Pdf(Controller_FileDetail controllerFileDetail, Serv_DataEditor fileDataService, Serv_Type_Impl fileTypeService, Serv_GetFile_FromDataBase fileService, FileDownloader fileDownloader, PathProvider pathProvider, WordToPdf w2p) {
        this.controllerFileDetail = controllerFileDetail;
        this.fileDataService = fileDataService;
        this.fileTypeService = fileTypeService;
        this.fileService = fileService;
        this.fileDownloader = fileDownloader;
        this.pathProvider = pathProvider;
        this.w2p = w2p;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public FilePath transfer2Pdf(FilePath filePath) throws Exception {
        return this.transfer2Pdf(filePath.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public FilePath transfer2Pdf(Integer id) throws Exception {
        FilePath sourceFilePath = fileService.getFile(id);

        if (sourceFilePath.getFileType().getTypeName().equals("pdf")) {
            return sourceFilePath;
        }

        File file = fileDownloader.getFile(id);
        Path pdfLocation = TypeUtils.replaceFileType(file.toPath(), "pdf");
        File pdfFile = transfer2Pdf(file, pdfLocation.toFile());

        FileType fileType = fileTypeService.getFileType(pdfFile.toPath());

        FilePath pdfFilePath = new FilePath(pdfFile.getName(), Paths.get(pathProvider.hideRealPath(pdfFile.getPath())), fileType);
        pdfFilePath.setTags(new HashSet<>(sourceFilePath.getTags()));
        pdfFilePath = fileDataService.saveDataToDataBase(pdfFilePath);

        return pdfFilePath;
    }

    /**
     * @param wordFile    target convert word file
     * @param pdfLocation if is null ,that will store in system default path
     * @return
     * @throws Exception
     */
    public File transfer2Pdf(File wordFile, File pdfLocation) throws Exception {
        File file = w2p.Word2Pdf(wordFile, pdfLocation);
        return file;
    }

}
