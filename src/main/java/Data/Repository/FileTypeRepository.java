package Data.Repository;

import Data.Entity.FileType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileTypeRepository extends CrudRepository<FileType,Integer> {

    @Query(value = "select * from file_type where type_name = ?1",nativeQuery = true)
    FileType findByName(@Param("name") String name);

    List<FileType> findAll();

}
