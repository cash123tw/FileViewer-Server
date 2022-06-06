package Web.Controller.FileController;

import App.Init.WordToPdf;
import Web.Service.FileIO.FileDownloader;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/io")
public class FIleIO {

    private FileDownloader downloader;
    private WordToPdf w2p;

    @Autowired
    public FIleIO(FileDownloader downloader, WordToPdf w2p) {
        this.downloader = downloader;
        this.w2p = w2p;
    }

    @GetMapping("")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("id") Integer id,
                                               @RequestParam(value = "type", required = false) String type) throws Exception {
        File file
                = downloader.getFile(id);
        String fileName
                = file.getName();
        ResponseEntity.BodyBuilder builder
                = ResponseEntity.status(200);
        MediaType mediaType;

        if (Strings.isNotEmpty(type)) {
            if(type.toLowerCase().equals("pdf")){
                mediaType = getMediaType(file);

                if(!mediaType.equals(APPLICATION_PDF)){
                    file = w2p.Word2Pdf(file);
                    System.out.println("Convert");
                    mediaType = getMediaType(file);
                }
            }else {
                int endIndex = fileName.lastIndexOf(".");
                fileName = fileName
                        .substring(0, endIndex==-1?fileName.length():endIndex)
                        .concat(".").concat(type);
                mediaType = getMediaType(new File(fileName));
            }
        } else {
            mediaType = getMediaType(file);
            builder.header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;filename=%s", fileName));
        }

        InputStream in
                = new FileInputStream(file);

        return builder.contentType(mediaType).body(in.readAllBytes());

    }

    public MediaType getMediaType(File file) throws IOException {
        String type
                = Files.probeContentType(file.toPath());
        if (Objects.isNull(type)) {
            return APPLICATION_OCTET_STREAM;
        }

        switch (type) {
            case APPLICATION_PDF_VALUE -> {
                return APPLICATION_PDF;
            }
            case TEXT_PLAIN_VALUE -> {
                return TEXT_PLAIN;
            }
            case APPLICATION_XML_VALUE -> {
                return TEXT_XML;
            }
            default -> {
                return APPLICATION_OCTET_STREAM;
            }
        }
    }

}
