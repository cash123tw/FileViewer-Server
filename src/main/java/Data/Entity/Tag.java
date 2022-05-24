package Data.Entity;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String name;
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "tag_type_connect"))
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private TagType tagType;

    public Tag() {
        this("");
    }

    public Tag(Integer id) {
        this("");
        this.id = id;
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag(String name, TagType tagType) {
        setName(name);
        this.tagType = tagType;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return id == tag.id && Objects.equals(name, tag.name) && Objects.equals(tagType, tag.tagType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, comment, tagType);
    }
}
