package Web.Controller;

import Data.Entity.Tag;
import Web.Service.TagService.Serv_Tag_Provider_Impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/tag0")
public class Tag0 {

    private Serv_Tag_Provider_Impl tagService;
    private ObjectMapper mapper;

    @Autowired
    public Tag0(Serv_Tag_Provider_Impl tagService, ObjectMapper mapper) {
        this.tagService = tagService;
        this.mapper = mapper;
    }

    @GetMapping("/")
    public ModelAndView getView(ModelAndView mav){
        mav.setViewName("/tag");
        return mav;
    }

    @GetMapping()
    public List<Tag> getAllTag(){
        List<Tag> results
                = tagService.findAllTag("");
        return results;
    }

    @PostMapping()
    public Tag newTag(@RequestBody Tag tag){
        tag
                = tagService.findOrCreate(tag);
        return tag;
    }

    @PutMapping
    public Tag updateTag(@RequestBody Tag tag){
        tag
                = tagService.updateTag(tag);
        return tag;
    }

    @DeleteMapping
    public Tag deleteTag(@RequestBody Tag tag){
        tag
                = tagService.deleteTag(tag);
        return tag;
    }
}
