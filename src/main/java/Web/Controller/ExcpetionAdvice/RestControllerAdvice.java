package Web.Controller.ExcpetionAdvice;

import App.Init.WordToPdf;
import Web.Service.FileIO.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @Autowired
    ConfigurableApplicationContext app;

    Map beans = new HashMap<Class, Object>();

    @ExceptionHandler
    public ResponseEntity<Object> restExceptionHandler(Exception e) {
        String message = "";

        if (e instanceof MaxUploadSizeExceededException) {
            MultipartProperties bean = getBean(MultipartProperties.class);
            DataSize size = bean.getMaxFileSize();
            message = String.format("檔案最大不可超過 : %d MB", size.toMegabytes());
        } else if (e instanceof FileUpload.FileVersionNotMatchException) {
            message = "檔案有被更改過，請重新整理確定更改資訊。";
        } else if (e instanceof WordToPdf.ConvertToPdfException){
            message = "檔案不支援轉成 PDF";
        }else{
            message = e.getMessage();
        }
        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_ENCODING,"UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .body(message);
    }

    private <T> T getBean(Class<T> clazz) {
        Object result;

        if (beans.containsKey(clazz)) {
            result = beans.get(clazz);
        } else {
            result = app.getBean(clazz);
            beans.put(clazz, result);
        }
        return (T) result;
    }

}
