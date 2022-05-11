package Data.Repository;

import Data.Entity.TagType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagTypeRepository extends CrudRepository<TagType,Integer> {

    List<TagType> findAll();

    TagType findByTypeName(String typeName);

    Optional<TagType> findById(Integer id);

}
