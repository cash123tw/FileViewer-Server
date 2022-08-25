package Data.Repository;

import Data.Entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo,Integer>, PagingAndSortingRepository<UserInfo,Integer> {

    Optional<UserInfo> findByUsername(String username);

    List<UserInfo> findAll();
}
