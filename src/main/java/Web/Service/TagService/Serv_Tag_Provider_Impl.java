package Web.Service.TagService;

import Data.Entity.Tag;
import Data.Entity.TagType;
import Data.Repository.TagRepository;
import Web.Service.TypeEditor.Serv_Tag_Type_Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<Tag> findByTagType(Integer id) {
        if(Objects.isNull(id)){
            throw new IllegalArgumentException("Id is null");
        }
        List<Tag> result
                = tagRepository.findAllByTagType_Id(id);
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
        String tagTypeName
                = "";
        if ("".equals(tagName) || Objects.isNull(tagName)) {
            throw new IllegalArgumentException("TagName is Empty");
        }

        if (Objects.nonNull(tagType)) {
            tagTypeName
                    = tagType.getTypeName();
            tagType
                    = tagTypeService.findTagOrCreateOne(tagTypeName);
        }

        target
                = tagRepository.findTagByTagTypeAndName(tagName, tagType.getTypeName());

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

    public Tag findOrCreate(String tagName, String tagComment, String tagType, String typeComment) {

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

    public void CheckNonEmpty(Tag tag) {
        if (Objects.isNull(tag)) {
            throw new NullPointerException(format("Tag is null"));
        }
    }
}
