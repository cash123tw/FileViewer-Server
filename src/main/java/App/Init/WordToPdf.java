package App.Init;

import com.aspose.words.Document;
import com.aspose.words.SaveOutputParameters;
import lombok.extern.java.Log;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

@Log
public class WordToPdf {

    private final String SYSTEM_MAKE_SUFFIX;
    private final File localPath;

    public WordToPdf(String tmp_path, String fileSuffix) {
        this.localPath = new File(tmp_path);
        this.SYSTEM_MAKE_SUFFIX = fileSuffix;
        localPath.deleteOnExit();
        localPath.mkdir();
        log.info(String.format("[Word To Pdf] service online directory is %s", tmp_path));
    }

    public File Word2Pdf(File file) throws Exception {
        return Word2Pdf(file, null);
    }

    public File Word2Pdf(File sourceFile, File transferFileLocate) throws Exception {
        checkFileIsDoc(sourceFile);

        if (Objects.isNull(transferFileLocate)) {
            String name = sourceFile.getName();
            name = name
                    .substring(0, name.lastIndexOf("."))
                    .concat(String.format("_%d.%s", SYSTEM_MAKE_SUFFIX, "pdf"));
            transferFileLocate = Paths.get(this.localPath.getPath(), name).toFile();
        }

        File result = convertToPdf(sourceFile, transferFileLocate);

        return result;
    }

    public void checkFileIsDoc(File file) throws ConvertToPdfException {
        String name = file.getName();
        int beginIndex = name.indexOf(".");

        if (beginIndex == -1) {
            beginIndex = 0;
        } else {
            beginIndex++;
        }

        String type = name.substring(beginIndex);

        if (!Objects.equals(type, "doc") && !Objects.equals(type, "docx")) {
            throw new ConvertToPdfException(String.format("File type [%s] is not support to convert to pdf.", type));
        }

    }

    private File convertToPdf(File sourceFile, File transferFileLocate) throws Exception {
        Document document = new Document(sourceFile.toPath().toString());
        if (!transferFileLocate.exists()) {
            document.save(transferFileLocate.getPath());
        }
        return transferFileLocate;
    }

    public class ConvertToPdfException extends Exception {
        public ConvertToPdfException(String message) {
            super(message);
        }
    }


}
