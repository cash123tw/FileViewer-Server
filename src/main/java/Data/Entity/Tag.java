package Data.Entity;

import lombok.Data;

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
//    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
//            org.hibernate.annotations.CascadeType.MERGE})
    @JoinColumn(name = "type_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "tag_type_connect"))
    private TagType tagType;
//
//    @ManyToMany()
//    @JoinTable(
//            name = "Tag_FilePath",
//            joinColumns = {@JoinColumn(name = "tag_id")},
//            inverseJoinColumns = {@JoinColumn(name = "filePath_id")},
//            foreignKey = @ForeignKey(name = "tag2FilePath"),
//            inverseForeignKey = @ForeignKey(name = "FilePath2tag")
//    )
//    @Cascade(org.hibernate.annotations.CascadeType.REFRESH)
//    @JsonIgnore
//    private Set<FilePath> filePaths;

    public Tag() {
        this("");
    }

    public Tag(String name) {
        this(name, new TagType("default"));
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
