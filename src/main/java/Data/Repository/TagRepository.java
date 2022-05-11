package Data.Repository;

import Data.Entity.Tag;
import Data.Entity.TagType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TagRepository extends CrudRepository<Tag,Integer> {

    @Query(value = "select * from tag where name like %?1%",nativeQuery = true)
    List<Tag> findByLikeName(@Param("name") String name);

    List<Tag> findAllByTagType_Id(Integer id);

    Tag findByName(String name);

    List<Tag> findAll();

    @Query(value = "select t from Tag t where t.name = :tagName and t.tagType.typeName = :tagType")
    Tag findTagByTagTypeAndName(String tagName,String tagType);

    @Query(value = "select t.* from tag t,tag_file_path tp where tp.file_path_id = ?1 AND tp.tag_id = t.id;",nativeQuery = true)
    Set<Tag> findTagByFilePathId(Integer id);

}
