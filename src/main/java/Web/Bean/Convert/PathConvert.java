package Web.Bean.Convert;

import javax.persistence.AttributeConverter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConvert implements AttributeConverter<Path,String> {

    @Override
    public String convertToDatabaseColumn(Path path) {
        return path.toString().replace('\\','/');
    }

    @Override
    public Path convertToEntityAttribute(String s) {
        return Paths.get(s);
    }
}
