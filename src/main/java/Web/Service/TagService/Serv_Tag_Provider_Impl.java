package Web.Service.TagService;

import Data.Entity.Tag;
import Data.Entity.TagType;
import Data.Repository.TagRepository;
import Web.Service.TypeEditor.Serv_Tag_Type_Impl;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.*;

import static java.lang.String.*;

@Service
public class Serv_Tag_Provider_Impl implements Serv_Tag_Provider {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    Serv_Tag_Type_Impl tagTypeService;

    @Override
    public List<Tag> findAllTag(String name) {
        List<Tag> tags
                = tagRepository.findByLikeName(name.toLowerCase());
        return tags;
    }

    @Override
    public List<Tag> findByTagTypeAndTagName(Integer tagTypeId, String tagName) {
        List<Tag> result;

        if (Objects.isNull(tagName) || Strings.isEmpty(tagName)) {
            tagName = "%%";
        } else {
            tagName = "%".concat(tagName).concat("%");
        }


        if (Objects.equals(tagTypeId, -1) || Objects.equals(tagTypeId, null)) {
            result = tagRepository.findAllByNameLike(tagName);
        } else {
            result = tagRepository.findAllByNameLikeAndTagTypeId(tagTypeId, tagName);
        }
        return result;
    }

    @Override
    public Tag saveTag(Tag tag) {
        tag
                = tagRepository.save(tag);
        return tag;
    }

    @Override
    public Tag findOrCreate(Tag tag) {
        Tag target = null;

        if (Objects.isNull(tag)) {
            throw new IllegalArgumentException("No Tag Present");
        } else if (tag.getId() != null) {
            Optional<Tag> one = findById(tag.getId());
            if (one.isPresent()) {
                Tag tmp = one.get();
                if (Objects.equals(tag, tmp)) {
                    return tmp;
                } else {
                    throw new IllegalArgumentException(format("Tag [%s] is Present", tmp.getName()));
                }
            }
        }

        TagType tagType
                = tag.getTagType();
        String tagName
                = tag.getName();
        String typeName
                = "";
        if ("".equals(tagName) || Objects.isNull(tagName)) {
            throw new IllegalArgumentException("TagName is Empty");
        }

        if (Objects.nonNull(tagType) && Objects.nonNull(tagType.getId())) {
            Integer type_id
                    = tagType.getId();
            typeName
                    = tagType.getTypeName();
            if (Objects.nonNull(type_id)) {
                Optional<TagType> opt_type
                        = tagTypeService.findTagTypeById(type_id);
                if (opt_type.isEmpty()) {
                    throw new EntityExistsException("TagType is not present");
                } else {
                    tagType = opt_type.get();
                }
            } else {
                tagType
                        = tagTypeService.findTagOrCreateOne(typeName);
            }
        }else{
            tagType = null;
        }

        target
                = tagRepository.findTagByTagTypeAndName(tagName, typeName);

        if (target == null) {
            tag.setTagType(tagType);
            target = tagRepository.save(tag);
        }

        return target;
    }

    @Override
    public Tag findOne(String name) {
        Tag tag
                = tagRepository.findByName(name);
        return tag;
    }

    @Override
    public Tag updateTag(Tag tag) {
        CheckNonEmpty(tag);
        Integer id = tag.getId();
        boolean success = false;

        if(!tagTypeService.checkTagTypeNotEmpty(tag.getTagType())){
            tag.setTagType(null);
        }

        if (Objects.nonNull(id)) {
            Optional<Tag> find = findById(id);
            if (find.isPresent()) {
                tag = tagRepository.save(tag);
                success = true;
            }
        }
        if (!success) {
            throw new IllegalArgumentException(format("Tag id not present"));
        }
        return tag;
    }

    @Override
    public Tag deleteTag(Tag tag) {
        CheckNonEmpty(tag);
        Integer id = tag.getId();
        boolean success = false;
        if (Objects.nonNull(id)) {
            Optional<Tag> one = findById(id);
            if (one.isPresent()) {
                Tag tmp = one.get();
                if (Objects.equals(tag, tmp)) {
                    tagRepository.delete(tag);
                    success = true;
                }
            }
        }

        if (!success) {
            throw new NoSuchElementException("Delete tag fail!!");
        }

        return tag;
    }

    public Optional<Tag> findById(Integer id) {
        Optional<Tag> tag
                = tagRepository.findById(id);
        return tag;
    }

    public Tag findOrCreate(String tagName, String tagComment, String tagType, String typeComment) throws Exception {

        Tag tag
                = new Tag(tagName, new TagType(tagType, typeComment));
        tag.setComment(tagComment);
        tag = findOrCreate(tag);

        return tag;
    }

    public Set<Tag> findTagByFilePathId(Integer id) {
        Set<Tag> tags
                = tagRepository.findTagByFilePathId(id);
        if (Objects.isNull(tags)) {
            tags = new HashSet<>();
        }

        return tags;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Set<Tag> saveTagsIfNotPresent(Set<Tag> tags) {
        ArrayList<Integer> ids
                = new ArrayList<>();
        Map<String, Tag> names
                = new HashMap<>();

        tags.forEach(t -> {
            Integer id = t.getId();
            String name = t.getName();

            if (Objects.nonNull(id)) {
                ids.add(id);
            } else if (Objects.nonNull(name)) {
                names.put(name, t);
            } else {
                throw new IllegalArgumentException("Id And name both is null !!");
            }
        });

        Set<Tag> compareResult
                = tagRepository.findTagsInIdOrName(ids, names.keySet().stream().toList());
        compareResult.forEach(tag -> {
            String name = tag.getName();
            if (names.containsKey(name)) {
                names.remove(name);
            }
        });

        names.forEach((key, value) -> {
            Tag tag = saveTag(value);
            compareResult.add(tag);
        });

        return compareResult;
    }

    public void CheckNonEmpty(Tag tag) {
        if (Objects.isNull(tag)) {
            throw new NullPointerException(format("Tag is null"));
        }
    }
}
