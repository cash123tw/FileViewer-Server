package Web.Bean.Convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoleConvert implements AttributeConverter<List<String>,String> {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        String collect = strings.stream()
                .map(str -> "\"" + str + "\"")
                .collect(Collectors.joining(","));
        return "["+collect+"]";
    }

    @SneakyThrows
    @Override
    public List<String> convertToEntityAttribute(String s) {
        List<String> list = mapper.readValue(s, List.class);
        return list;
    }
}
