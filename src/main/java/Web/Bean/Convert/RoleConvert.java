package Web.Bean.Convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleConvert implements AttributeConverter<Set<String>,String> {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Set<String> strings) {
        if(Objects.isNull(strings)){
            return "[]";
        }
        String collect = strings.stream()
                .map(str -> "\"" + str + "\"")
                .collect(Collectors.joining(","));
        return "["+collect+"]";
    }

    @SneakyThrows
    @Override
    public Set<String> convertToEntityAttribute(String s) {
        Set<String> list = mapper.readValue(s,Set.class);
        return list;
    }
}
