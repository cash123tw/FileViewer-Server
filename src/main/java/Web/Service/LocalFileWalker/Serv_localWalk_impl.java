package Web.Service.LocalFileWalker;

import Bean.FileDetail;
import Data.Repository.FilePathRepository;
import Data.Repository.FileTypeRepository;
import Data.Repository.TagRepository;
import Web.Service.FileDataEditor.Serv_DataEditor_Impl;
import Worker.FileExploreWorker;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

@Service
@Log
public class Serv_localWalk_impl implements Serv_localServWalk{

    private FileTypeRepository fileTypeRep;
    private TagRepository tagRep;
    private FilePathRepository filePathRep;
    private Serv_DataEditor_Impl dataEditor;
    private FileExploreWorker worker;

    @Autowired
    public Serv_localWalk_impl(FileTypeRepository fileTypeRep,
                               TagRepository tagRep,
                               FilePathRepository filePathRep,
                               Serv_DataEditor_Impl dataEditor,
                               FileExploreWorker worker) {
        this.fileTypeRep = fileTypeRep;
        this.tagRep = tagRep;
        this.filePathRep = filePathRep;
        this.dataEditor = dataEditor;
        this.worker = worker;
    }

    @Override
    public boolean walkLocalFile(Predicate<File> predicate) {
        try {
            worker.SearchFile("",predicate);
        } catch (IllegalAccessException|IOException e) {
            log.warning(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean walkLocalFileAndSaveToRepository(){
        return walkLocalFile(new Predicate<File>() {
            @Override
            public boolean test(File file) {
                if(!file.getName().startsWith(".")) {
                    FileDetail fileDetail
                            = worker.getPathProvider().makeFileDetail(file);
                    dataEditor.saveDataToDataBase(fileDetail);
                }
                return false;
            }
        });
    }

}
