package Data.Repository;

import Data.Entity.FilePath;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Repository
public interface FilePathRepository extends CrudRepository<FilePath, Integer> {

    @Query(value = "select * from file_path where path = getReplacePath(:path)", nativeQuery = true)
    FilePath findFilePathByPath(String path);

    @Query("select f from FilePath f inner join f.fileType t on t.typeName = (select t.typeName from FileType t where t.typeName = :type) where f.path = :path")
    FilePath findPathIsDirType(Path path,String type);

    @Query(value = "select f from FilePath f where f.parentPath.id in :ids")
    @EntityGraph(value = "FilePath.withParentIdAndPath",
            type = EntityGraph.EntityGraphType.FETCH)
    List<FilePath> findChildById(List<Integer> ids);

    @Query(value = "select f from FilePath f where f.parentPath.path = :path")
    List<FilePath> findChildByPath(Path path);

    @Query(value = "select f from FilePath f where f.parentPath.id = (select ff.id from FilePath ff where ff.parentPath is null)")
    @EntityGraph(value = "FilePath.withParentIdAndPath",
            type = EntityGraph.EntityGraphType.FETCH)
    List<FilePath> ListRootChild();

    @Query(value = "call find_child_by_root(:id,:fileName)", nativeQuery = true)
    List<FilePath> findAllByRootIdAndFileName(Integer id, String fileName);

    @Query(value = "select f.* from file_path f where path = getReplacePath(:path) and " +
            "find_in_set((select id from file_type where type_name = :type),(select group_concat(id) from type_and_path tp where tp.file_id = f.id )) = 0", nativeQuery = true)
    FilePath findFilePathByPathExcludeDirType(String path, String type);

    @Query("select f from FilePath f where find_in_set(f.id,(" +
                "select group_concat(ff.id) from FilePath ff join ff.fileType fft where fft.id = (" +
                    "select t.id from FileType t where t.typeName = :typeName)"+
           "))=0 and f.path = :path")
    FilePath findFilePathByPathExcludeDirType0(Path path,String typeName);

    @Query(value = "select f.* from file_path f where path = getReplacePath(:path) and " +
            "find_in_set((select id from file_type where type_name = :type),(select group_concat(id) from type_and_path tp where tp.file_id = f.id )) > 0", nativeQuery = true)
    FilePath findFilePathByPathIsDirType(String path,String type);

    @Query("select f from FilePath f where find_in_set(f.id,(" +
           "select group_concat(ff.id) from FilePath ff join ff.fileType fft where fft.id = (" +
           "select t.id from FileType t where t.typeName = :typeName)"+
           "))>0 and f.path = :path")
    FilePath findFilePathByPathIsDirType0(Path path,String typeName);

    Optional<FilePath> findById(Integer id);

    @Query("from FilePath f where f.id = :id")
    @EntityGraph(value = "FilePath.findAll"
            , type = EntityGraph.EntityGraphType.LOAD)
    FilePath findByIdAll(Integer id);

}
