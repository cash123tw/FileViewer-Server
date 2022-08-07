package Data.Repository;

import Data.Entity.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo,Integer> {

    Optional<UserInfo> findByUsername(String username);

}
