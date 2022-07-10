package Web.Service.TypeEditor;

import Data.Entity.TagType;
import Data.Repository.TagTypeRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.io.ObjectStreamClass;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class Serv_Tag_Type_Impl implements Serv_Tag_Type {

    @Autowired
    TagTypeRepository tagTypeRepository;

    @Override
    public List<TagType> getAllTagType() {
        return tagTypeRepository.findAll();
    }

    @Override
    public TagType saveTagType(@NonNull TagType tagType) {
        String name = tagType.getTypeName();
        Integer id = tagType.getId();

        if ("".equals(name.trim()) || Objects.isNull(name)) {
            throw new IllegalArgumentException("Type name must exists");
        }

        tagType = tagTypeRepository.save(tagType);
        return tagType;
    }

    @Override
    public TagType findTagTypeByName(String name) {
        return tagTypeRepository.findByTypeName(name);
    }

    @Override
    public Optional<TagType> findTagTypeById(Integer id) {
        return tagTypeRepository.findById(id);
    }

    public TagType findTagOrCreateOne(String typeName) {
        TagType tagType
                = findTagOrCreateOne(new TagType(typeName));
        return tagType;
    }

    public TagType findTagOrCreateOne(@NotNull TagType tagType) {
        TagType tag
                = findTagTypeByName(tagType.getTypeName());
        if (tag == null) {
            tag = tagType;
            tag = saveTagType(tag);
        }

        return tag;
    }

    public boolean checkTagTypeNotEmpty(TagType tagType){
        return
                Objects.nonNull(tagType) &&
                (Objects.nonNull(tagType.getId()) ||
                Objects.nonNull(tagType.getTypeName()) ||
                Objects.nonNull(tagType.getComment())) ;
    }
}
