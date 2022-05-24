package Data.Repository;

import Data.Entity.FilePath;
import Data.Entity.FileType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Repository
public interface FilePathRepository extends CrudRepository<FilePath, Integer> {

    @Query("select f from FilePath f where f.path = :path")
    FilePath findFilePathByPath(Path path);

    @Query("select f from FilePath f join f.fileType t on t.typeName = :typeName where f.path = :path")
    FilePath findFilePathByPathAndFileTypeName(Path path, String typeName);

    @Query("select f,t from FilePath f join f.fileType t where f.id in :ids")
    @EntityGraph(value = "FilePath.findAll"
            , type = EntityGraph.EntityGraphType.FETCH)
    List<FilePath> findFilePathsByIdIsInIds(List ids);

    @Query("select f from FilePath f inner join f.fileType t on t.typeName = (select t.typeName from FileType t where t.typeName = :type) where f.path = :path")
    FilePath findPathIsDirType(Path path, String type);

    @Query(value = "select f from FilePath f where f.parentPath.id in :ids")
//    @EntityGraph(value = "FilePath.withParentIdAndPath",
    @EntityGraph(value = "FilePath.findAll",
            type = EntityGraph.EntityGraphType.LOAD)
    List<FilePath> findChildById(List<Integer> ids);

    @Query(value = "select f from FilePath f where f.parentPath.id = (select ff.id from FilePath ff where ff.parentPath is null)")
    @EntityGraph(value = "FilePath.withParentIdAndPath",
            type = EntityGraph.EntityGraphType.FETCH)
    List<FilePath> ListRootChild();

    @Query(value = "call find_child_by_root(:id,:fileName)", nativeQuery = true)
    List<Integer> findAllByRootIdAndFileName(Integer id, String fileName);

    @Query("select f from FilePath f where find_in_set(f.id,(" +
            "select group_concat(ff.id) from FilePath ff join ff.fileType fft where fft.id = (" +
            "select t.id from FileType t where t.typeName = :typeName)" +
            "))=0 and f.path = :path")
    FilePath findFilePathByPathExcludeDirType0(Path path, String typeName);

    @Query("select f from FilePath f where find_in_set(f.id,(" +
            "select group_concat(ff.id) from FilePath ff join ff.fileType fft where fft.id = (" +
            "select t.id from FileType t where t.typeName = :typeName)" +
            "))>0 and f.path = :path")
    FilePath findFilePathByPathIsDirType0(Path path, String typeName);

    Optional<FilePath> findById(Integer id);

    @Query("from FilePath f where f.id = :id")
    @EntityGraph(value = "FilePath.findAll"
            , type = EntityGraph.EntityGraphType.LOAD)
    FilePath findByIdAll(Integer id);

}
