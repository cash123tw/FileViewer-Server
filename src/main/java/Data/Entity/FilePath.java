package Data.Entity;

import Web.Bean.Convert.PathConvert;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
//@SqlResultSetMapping(
//        name = "Test",
//        classes = {
//                @ConstructorResult(
//                        targetClass = FilePath.class,
//                        columns = {
//                                @ColumnResult(name = "id", type = int.class),
//                                @ColumnResult(name = "file_name", type = String.class)
//                        })})
//@org.hibernate.annotations.NamedNativeQuery(
//        name = "filepath.test",
//        query = "select id,name as file_name from tag where id = :id",
//        resultSetMapping = "Test"
//)
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
                        @NamedAttributeNode(value = "tags"),
                        @NamedAttributeNode(value = "parentPath")
                }
        )
})
public class FilePath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String file_name;

    @Convert(converter = PathConvert.class)
    private Path path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "parent_connect"))
    private FilePath parentPath;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(
            name = "Type_and_Path",
            joinColumns = {@JoinColumn(name = "file_id")},
            inverseJoinColumns = {@JoinColumn(name = "type_id")},
            foreignKey = @ForeignKey(name = "file_type_ids"),
            inverseForeignKey = @ForeignKey(name = "type_file_ids"))
    @Fetch(FetchMode.SUBSELECT)
    private Set<FileType> fileType;

    @ManyToMany()
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @JoinTable(
            name = "Tag_FilePath",
            joinColumns = {@JoinColumn(name = "filePath_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")},
            foreignKey = @ForeignKey(name = "FilePath2tag"),
            inverseForeignKey = @ForeignKey(name = "tag2FilePath")
    )
    @Fetch(FetchMode.SUBSELECT)
    private Set<Tag> tags;

    public FilePath(Integer id, String file_name) {
        this(file_name, null, null);
        this.id = id;
        this.file_name = file_name;

    }

    public FilePath(String file_name, Path path, FileType fileType) {
        this();
        this.file_name = file_name;
        this.path = path;
        if (this.fileType == null) {
            this.fileType = new HashSet<>();
        }
        this.fileType.add(fileType);
    }

    public FilePath() {
        this.tags = new HashSet<>();
    }

    public String getPath() {
        return path.toString().replace('\\', '/');
    }

    public void setPath(String path) {
        this.path = Paths.get(path);
    }

    public Set<Tag> getTags() {
        return getTags(false);
    }

    public Set<Tag> getTags(boolean io) {
        if (!io) return null;
        else return tags;
    }

    public Set<FileType> getFileType() {
        if (fileType == null) {
            fileType = createFileSet();
        }
        return fileType;
    }

    public void setFileType(Set<FileType> fileType) {
        this.fileType = fileType;
    }

    public void setFileType1(FileType fileType) {
        getFileType().add(fileType);
    }

    public void addTag(Tag tag) {
        getTags(true).add(tag);
    }

    public Set<FileType> createFileSet() {
        return new HashSet<>();
    }

}
