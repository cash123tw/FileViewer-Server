package Web.Controller.FileController;

import App.Init.WordToPdf;
import Data.Entity.FilePath;
import Web.Service.FileIO.FileDownloader;
import Web.Service.WordToPdf.Serv_W2Pdf;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.beans.Encoder;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;

import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/io")
public class FIleIO {

    private FileDownloader downloader;
    private Serv_W2Pdf w2pService;


    @Autowired
    public FIleIO(FileDownloader downloader, Serv_W2Pdf w2pService) {
        this.downloader = downloader;
        this.w2pService = w2pService;
    }

    @GetMapping("")
    @Transactional
    public ResponseEntity<byte[]> downloadFile(@RequestParam("id") Integer id,
                                               @RequestParam(value = "type", required = false) String type) throws Exception {
        MediaType mediaType = null;
        File file
                = downloader.getFile(id);
        ResponseEntity.BodyBuilder builder
                = ResponseEntity.status(200);

        if (Strings.isNotEmpty(type)) {
            if ("pdf".equals(type.toLowerCase())) {
                FilePath filePath = w2pService.transfer2Pdf(id);
                mediaType = getMediaType(filePath.getPath());
                file = downloader.getFile(filePath.getId());
            }
        }

        if (Objects.isNull(mediaType)) {
            mediaType = getMediaType(file);
        }

        String fileName = toPercentEncode(file.getName());

        builder.header(HttpHeaders.CONTENT_DISPOSITION,
                String.format("attachment; filename=%s; filename*=UTF-8''%s;", fileName, fileName));
        InputStream in
                = new FileInputStream(file);

        return builder.contentType(mediaType).body(in.readAllBytes());

    }

    public MediaType getMediaType(String path) throws IOException {
        return this.getMediaType(Paths.get(path));
    }

    public MediaType getMediaType(Path path) throws IOException {
        return this.getMediaType(path.toFile());
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

    public String toPercentEncode(String fileName) {

        String fileType = "";
        int lastDot = fileName.lastIndexOf(".");

        if (lastDot != -1) {
            fileType = fileName.substring(lastDot);
        }

        fileName =
                URLEncoder.encode(fileName.replaceAll(fileType, ""), StandardCharsets.UTF_8).concat(fileType);

        return fileName;
    }
}
