package Web.Service.FileDataEditor;

import Bean.FileDetail;
import Bean.FileType;
import Bean.PathProvider;
import Data.Entity.FilePath;
import Data.Entity.Tag;
import Data.Repository.FilePathRepository;
import Data.Repository.FileTypeRepository;
import Data.Repository.TagRepository;
import Web.Service.TagService.Serv_Tag_Provider_Impl;
import Web.Service.TypeEditor.Serv_Type_Impl;
import Worker.FileExploreWorker;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@Log
@NoArgsConstructor
public class Serv_DataEditor_Impl implements Serv_DataEditor {

    @Value("Directory")
    public String TAG_NAME_DIR;
    @Value("file")
    public String TAG_NAME_FILE;

    private FileExploreWorker exploreWorker;
    private FilePathRepository filePathRep;
    private TagRepository tagRep;
    private FileTypeRepository fileTypeRep;
    private Serv_Type_Impl typeService;
    private Serv_Tag_Provider_Impl tagService;
    private PathProvider pathProvider;

    @Autowired
    public Serv_DataEditor_Impl(FileExploreWorker exploreWorker,
                                FilePathRepository filePathRep,
                                TagRepository tagRep,
                                FileTypeRepository fileTypeRep,
                                Serv_Type_Impl typeService,
                                Serv_Tag_Provider_Impl tagService,
                                PathProvider pathProvider) {
        this.exploreWorker = exploreWorker;
        this.filePathRep = filePathRep;
        this.tagRep = tagRep;
        this.fileTypeRep = fileTypeRep;
        this.typeService = typeService;
        this.tagService = tagService;
        this.pathProvider = pathProvider;
    }

    @Override
    public FilePath saveDataToDataBase(FilePath filePath) throws FileNotFoundException, IllegalAccessException {
        if (pathProvider.isHideRealPath()) {
            String path = Paths.get(filePath.getPath()).toString();
            path = pathProvider.hideRealPath(path);

            if (!path.startsWith("\\")) {
                path = "\\" + path;
            }

            filePath.setPath(path);
        }


        File file = pathProvider.getFile(filePath.getPath());

        filePath.setLastModify(new Date(file.lastModified()));
        filePath = findParentPath(null, filePath);
        filePath.setFile_name(removeTypeFromPath(filePath.getFile_name()));

        FilePath result;

        if (Objects.isNull(filePath.getId())) {
            result = filePathRep.findFilePathByPath(Paths.get(filePath.getPath()));
        }else{
            result = filePath;
        }

        if (Objects.nonNull(result) && Objects.nonNull(result.getId())) {
            filePath.setId(result.getId());
        }

        result = filePathRep.save(filePath);
        return result;
    }

    @Override
    public boolean saveDataToDataBase(FileDetail fileDetail) {
        return saveDataToDataBase(fileDetail, null);
    }

    @Override
    public boolean saveDataToDataBase(FileDetail fileDetail, Set<Tag> tags) {
        try {
            if (checkFileExists(fileDetail)) {
                return true;
            }
            FilePath filePath;
            Tag tag = null;

            if (isDirectory(fileDetail)) {
                filePath = DetailToFilePathByDir(fileDetail);
            } else {
                filePath = DetailToFilePathByFile(fileDetail);
            }

            getTags(filePath);
            filePath.addTag(tag);

            if (Objects.nonNull(tags)) {
                tags
                        .stream()
                        .iterator()
                        .forEachRemaining(filePath::addTag);
            }

            saveDataToDataBase(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            log.warning(e.getMessage());
            return false;
        }
        return true;
    }

    public FilePath DetailToFilePathByDir(FileDetail fileDetail) {

        String path
                = fileDetail.getFilePath();
        if ("".equals(path)) {
            path = "/";
        }
        FilePath filePath
                = filePathRep.findFilePathByPathIsDirType0(transferPath(path), TAG_NAME_DIR);

        if (Objects.isNull(filePath)) {
            filePath = new FilePath(
                    fileDetail.getFileName(),
                    Paths.get(fileDetail.getFilePath()),
                    typeService.findTypeOrCreate(TAG_NAME_DIR)
            );
        }

        return filePath;
    }

    public FilePath DetailToFilePathByFile(FileDetail fileDetail) {
        String typeName
                = fileDetail.getTypeName();
        String path_to_save
                = fileDetail.getFilePath();
        String fileName
                = fileDetail.getFileName();
        FilePath filePath
                = filePathRep
                .findFilePathByPathExcludeDirType0(transferPath(path_to_save), TAG_NAME_DIR);
        if (typeName == null || typeName.isEmpty() || typeName.equals("null")) {
            typeName = TAG_NAME_FILE;
        }

        if (Objects.isNull(filePath)) {
            filePath = new FilePath(
                    removeTypeFromPath(fileName),
                    Path.of(path_to_save),
                    null
            );
        }

        filePath.setFileType(typeService.findTypeOrCreate(typeName));

        return filePath;
    }

    public static String removeTypeFromPath(String file_path) {
        Path path
                = Paths.get(file_path);
        int count
                = path.getNameCount();
        if (count > 0) {
            String origin
                    = path.getName(count - 1).toString();
            String replace = origin;

            if (!origin.startsWith(".")) {
                int i
                        = origin.lastIndexOf('.');
                if (i != -1) {
                    replace = origin.substring(0, i);
                }
            }
            file_path = file_path.replace(origin, replace);
        }
        return file_path;
    }

    public FilePath findParentPath(FileDetail fileDetail, FilePath filePath) {
        FilePath parentPath
                = filePath.getParentPath();
        if (Objects.isNull(parentPath)) {
            Path path
                    = Paths.get(filePath.getPath());
            Path parent
                    = path.getParent();
            if (parent == null || "".equals(parent)) {
                filePath.setPath("/");
            }

            parentPath = filePathRep.findFilePathByPath(parent);
        }
        filePath.setParentPath(parentPath);
        return filePath;
    }

    public Tag createTag(String tagName, String tagType) throws Exception {
        Tag tag
                = tagService.findOrCreate(tagName, null, tagType, null);
        return tag;
    }

    public Tag createTag(Path path, String tagType) throws Exception {
        int count
                = path.getNameCount();
        String tagName;
        if (count == 0) {
            tagName = "root";
        } else {
            tagName
                    = path.getName(count - 1).toString();
        }

        return createTag(tagName, tagType);
    }

    public FilePath getTags(FilePath file) {
        Integer id
                = file.getId();
        Set<Tag> tags;
        if (id != null) {
            tags = tagService.findTagByFilePathId(id);
        } else {
            tags = new HashSet<>();
        }
        file.setTags(tags);

        return file;
    }

    public boolean checkFileExists(FileDetail fileDetail) {
        FilePath filePath = filePathRep
                .findFilePathByPathAndFileTypeName
                        (Paths.get(fileDetail.getFilePath()),
                                fileDetail.getType().equals(FileType.DIR) ? TAG_NAME_DIR : TAG_NAME_FILE);
        return Objects.nonNull(filePath);
    }

    public boolean isDirectory(FileDetail fileDetail) {
        Bean.FileType type
                = fileDetail.getType();
        return type == Bean.FileType.DIR;
    }

    public static Path transferPath(String path) {
        return Path.of(path);
    }

}
