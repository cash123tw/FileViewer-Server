package Web.Service.TypeEditor;

import Data.Entity.TagType;
import com.sun.istack.NotNull;

import java.util.List;
import java.util.Optional;

public interface Serv_Tag_Type {

    List<TagType> getAllTagType();

    TagType saveTagType(TagType tagType);

    TagType findTagTypeByName(String name);

    Optional<TagType> findTagTypeById(Integer id);

}
