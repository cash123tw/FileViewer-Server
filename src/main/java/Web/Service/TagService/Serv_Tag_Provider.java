package Web.Service.TagService;

import Data.Entity.Tag;
import Data.Entity.TagType;

import java.util.List;
import java.util.Optional;

public interface Serv_Tag_Provider {

    List<Tag> findAllTag(String name);

    List<Tag> findByTagTypeAndTagName(Integer id, String tagName);

    Tag saveTag(Tag tag);

    Tag findOrCreate(Tag tag) throws Exception;

    Tag findOne(String name);

    Optional<Tag> findById(Integer id);

    Tag updateTag(Tag tag);

    Tag deleteTag(Tag tag);
}
