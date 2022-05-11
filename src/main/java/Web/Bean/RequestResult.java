package Web.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestResult<T> {

    private RequestResultType result;
    private T obj;
    private String message;

    public RequestResult(RequestResultType result, T object) {
        this.result = result;
        this.obj = object;
    }

    public static <T>RequestResult makeSuccessResult(T obj,String message){
        return new RequestResult(
                RequestResultType.SUCCESS,
                obj,
                message);
    }

    public static <T>RequestResult makeFailResult(T obj,String message){
        return new RequestResult(
                RequestResultType.FAIL,
                obj,
                message);
    }

    public enum RequestResultType {
        SUCCESS,FAIL;
    }

}
