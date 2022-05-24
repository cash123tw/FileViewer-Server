package Data.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.IndexColumn;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class TagType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    @NonNull
    private String typeName;
    private String comment;

    public TagType() {
    }

    public TagType(String typeName, String comment) {
        this.typeName = typeName;
        this.comment = comment;
    }

    public TagType(String typeName) {
        this(typeName,null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagType tagType = (TagType) o;
        return Objects.equals(id, tagType.id) && Objects.equals(typeName, tagType.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, typeName);
    }
}
