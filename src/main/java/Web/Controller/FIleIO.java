package Web.Controller;

import Web.Service.FileIO.FileDownloader;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private FileDownloader downloader;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") Integer id) throws IOException, IllegalAccessException {
        File file
                = downloader.getFile(id);
        InputStream in
                = new FileInputStream(file);
        return ResponseEntity
                .status(200)
                .contentType(getMediaType(file))
                .body(in.readAllBytes());
    }

    public MediaType getMediaType(File file) throws IOException {
        String type
                = Files.probeContentType(file.toPath());
        if(Objects.isNull(type)){
            return APPLICATION_OCTET_STREAM;
        }

        switch (type){
            case APPLICATION_PDF_VALUE -> {
                return APPLICATION_PDF;
            }
            default -> {
                return APPLICATION_OCTET_STREAM;
            }
        }
    }

}
