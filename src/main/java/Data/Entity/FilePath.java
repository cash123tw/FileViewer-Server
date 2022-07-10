package Data.Entity;

import Web.Bean.Convert.PathConvert;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Subselect;
import org.springframework.validation.ObjectError;

import javax.persistence.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "FilePath.withParentIdAndPath",
                attributeNodes = {
                        @NamedAttributeNode("fileType"),
                        @NamedAttributeNode(value = "parentPath", subgraph = "ParentIdAndPath")},
                subgraphs = @NamedSubgraph(
                        name = "ParentIdAndPath",
                        attributeNodes = {
                                @NamedAttributeNode("id"),
                                @NamedAttributeNode("path")
                        }
                )
        ),
        @NamedEntityGraph(
                name = "FilePath.findAll",
                attributeNodes = {
                        @NamedAttributeNode(value = "tags",subgraph = "tags"),
                        @NamedAttributeNode(value = "parentPath"),
                        @NamedAttributeNode(value = "fileType", subgraph = "fileType")
                },
                subgraphs = {@NamedSubgraph(name = "fileType",
                        attributeNodes = {
                                @NamedAttributeNode("id"),
                                @NamedAttributeNode("typeName")
                        }),
                        @NamedSubgraph(name = "tags",
                                attributeNodes = {
                                        @NamedAttributeNode(value = "tagType", subgraph = "tagType")
                                }),
                        @NamedSubgraph(name = "tagType",
                                attributeNodes = {
                                        @NamedAttributeNode(value = "id"),
                                        @NamedAttributeNode(value = "typeName")})
                }
        )
})
public class FilePath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String file_name;
    private UUID version;
    private Date lastModify;
    private Boolean missing = false;

    @Convert(converter = PathConvert.class)
    @Column(unique = true)
    private Path path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "parent_connect"))
    private FilePath parentPath;

    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinTable(
            name = "Tag_FilePath",
            joinColumns = {@JoinColumn(name = "filePath_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")},
            foreignKey = @ForeignKey(name = "FilePath2tag"),
            inverseForeignKey = @ForeignKey(name = "tag2FilePath")
    )
    @Fetch(FetchMode.SUBSELECT)
    private Set<Tag> tags;
    @OneToOne(fetch = FetchType.EAGER)
    private FileType fileType;

    public FilePath() {
        this.tags = new HashSet<>();
        this.version = UUID.randomUUID();
    }

    public FilePath(Integer id, String file_name) {
        this(file_name, null, null);
        this.id = id;
        this.file_name = file_name;

    }

    public FilePath(String file_name, Path path, FileType fileType) {
        this();
        this.file_name = file_name;
        this.path = path;
        this.fileType = fileType;
    }

    public String getPath() {
        return path.toString().replace('\\', '/');
    }

    public void setPath(String path) {
        this.path = Paths.get(path);
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        getTags().add(tag);
    }

    public UUID getVersion(){
        if(this.version == null){
            this.setVersion(UUID.randomUUID());
        }

        return this.version;
    }

}
