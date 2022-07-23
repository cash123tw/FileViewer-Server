package Web.Service.LocalFileWalker;

import Bean.FileDetail;
import Data.Entity.FilePath;
import Data.Repository.FilePathRepository;
import Data.Repository.FileTypeRepository;
import Data.Repository.TagRepository;
import Web.Service.FileDataEditor.Serv_DataEditor_Impl;
import Web.Service.FileGet.Serv_GetFile_FromDatabase_Impl;
import Worker.FileExploreWorker;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Service
@Log
public class Serv_localWalk_impl implements Serv_localServWalk {

    private FileTypeRepository fileTypeRep;
    private TagRepository tagRep;
    private FilePathRepository filePathRep;
    private Serv_DataEditor_Impl dataEditor;
    private FileExploreWorker worker;
    private Serv_GetFile_FromDatabase_Impl fileService;

    @Autowired
    public Serv_localWalk_impl(FileTypeRepository fileTypeRep,
                               TagRepository tagRep,
                               FilePathRepository filePathRep,
                               Serv_DataEditor_Impl dataEditor,
                               FileExploreWorker worker,
                               Serv_GetFile_FromDatabase_Impl fileService) {
        this.fileTypeRep = fileTypeRep;
        this.tagRep = tagRep;
        this.filePathRep = filePathRep;
        this.dataEditor = dataEditor;
        this.worker = worker;
        this.fileService = fileService;
    }

    @Override
    public boolean walkLocalFile(Predicate<File> predicate) {
        try {
            worker.SearchFile("", predicate);
        } catch (IllegalAccessException | IOException e) {
            log.warning(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Checking all FilePath relate File is exists,
     * if not,will set FilePath.exists =  false
     */
    public void checkAllFilePathExists() {
        int DATA_SIZE_PER_REQUEST = 200;
        int page = 0;
        Page<FilePath> paths = null;

        do {
            paths = filePathRep.findAll(PageRequest.of(page++, DATA_SIZE_PER_REQUEST));
            paths
                    .get()
                    .forEach(file -> {
                        try {
//                            If file not exists , will throw Exception
                            if(file.getId() == 731){
                                System.out.println();
                            }
                            worker.getFile(file.getPath());
                            if (file.getMissing()) {
                                file.setMissing(false);
                                filePathRep.save(file);
                            }
                        } catch (FileNotFoundException | IllegalAccessException e) {
                            if (e instanceof FileNotFoundException) {
                                if (fileService.getDirType().equals(file.getFileType())) {
                                    deleteAllConnectParent(file.getId());
                                }
                                filePathRep.delete(file);
                                log.warning(String.format("File is missing auto remove ID:[%d] PATH:[%s]", file.getId(), file.getPath()));
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
        } while (paths.getNumber() + 1 < paths.getTotalPages());
    }

    public void deleteAllConnectParent(Integer id) {
        List<FilePath> files
                = filePathRep.findChildById(Arrays.asList(id));
        files.forEach(file -> {
            file.setParentPath(null);
            filePathRep.save(file);
        });
    }

    public boolean walkLocalFileAndSaveToRepository() {
        return walkLocalFile(new Predicate<File>() {

            @Override
            public boolean test(File file) {
                if (!file.getName().startsWith(".")) {
                    FileDetail fileDetail
                            = worker.getPathProvider().makeFileDetail(file);
                    dataEditor.saveDataToDataBase(fileDetail);
                    return true;
                }
                return false;
            }
        });
    }

}
