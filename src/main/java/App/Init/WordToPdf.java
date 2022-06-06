package App.Init;

import com.aspose.words.Document;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.file.FileSystemException;
import java.nio.file.Paths;
import java.util.Objects;

@Log
public class WordToPdf {

    private File file;

    public WordToPdf(String tmp_path) {
        this.file = new File(tmp_path);
        file.deleteOnExit();
        file.mkdir();
        log.info(String.format("[Word To Pdf] service online directory is %s", tmp_path));
    }

    public File Word2Pdf(File file) throws Exception {
        String name = file.getName();
        int beginIndex = name.indexOf(".");

        if (beginIndex == -1) {
            beginIndex = 0;
        } else {
            beginIndex++;
        }

        String type = name.substring(beginIndex);

        if (!Objects.equals(type, "doc") && !Objects.equals(type, "docx")) {
            throw new ConvertToPdfException(String.format("File type [%s] is not support to convert to pdf.",type));
        }

        return convertToPdf(file);
    }

    private File convertToPdf(File file) throws Exception {
        Document document = new Document(file.toPath().toString());
        String path = file.getPath();
        Integer random = path.hashCode();
        String name = file.getName();
        name =
                name.substring(0, name.lastIndexOf(".")).concat(String.format("_%d.%s", random, "pdf"));
        file = Paths.get(this.file.getPath(), name).toFile();

        if (!file.exists()) {
            document.save(file.getPath());
        }

        return file;
    }

    public class ConvertToPdfException extends Exception {
        public ConvertToPdfException(String message) {
            super(message);
        }
    }


}
