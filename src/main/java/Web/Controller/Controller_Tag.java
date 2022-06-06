package Web.Controller;

import Data.Entity.Tag;
import Web.Bean.RequestResult;
import Web.Service.TagService.Serv_Tag_Provider;
import Web.Service.TypeEditor.Serv_Tag_Type;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/tag")
public class Controller_Tag {

    private Serv_Tag_Provider tagService;
    private Serv_Tag_Type tagTypeService;
    private ObjectMapper mapper;
    private Controller_TagType tagType_controller;

    @Autowired
    public Controller_Tag(Serv_Tag_Provider tagService, Serv_Tag_Type tagTypeService, ObjectMapper mapper, Controller_TagType tagType_controller) {
        this.tagService = tagService;
        this.tagTypeService = tagTypeService;
        this.mapper = mapper;
        this.tagType_controller = tagType_controller;
    }

    @GetMapping("/")
    public ModelAndView getView(ModelAndView mav) {
        mav.setViewName("/tag");
        mav.addObject("stat", true);
        return mav;
    }

    @GetMapping({"/find/{id}"})
    public ModelAndView findOneById(@PathVariable(value = "id", required = false) Integer id, ModelAndView mav) {
        RequestResult result = null;

        if (id != null) {
            result = findOneById(id);
        } else {
            Optional<Tag> tag = tagService.findById(id);
            if (tag.isEmpty()) {
                result = RequestResult.makeSuccessResult(tag, "");
            } else {
                result = RequestResult.makeFailResult(null, String.format("No Tag ID [%d] found", id));
            }
        }

        mav.setViewName("/tag");
        mav.addObject("result", result);
        mav.addObject("stat", false);

        return mav;
    }

    @GetMapping("/all")
    public RequestResult getAllTag(@RequestParam(name = "name", required = false, defaultValue = "") String name) {
        List<Tag> tags
                = tagService.findAllTag(name);
        RequestResult result
                = RequestResult.makeSuccessResult(tags, "");
        return result;
    }

    @GetMapping(value = "/relateTagType/{id}", produces = "application/json")
    public String getAllByTagType(@PathVariable(value = "id",required = false) Integer id,
                                  @RequestParam(value = "name", required = false) String tagName,
                                  HttpServletResponse response) {
        try {
            List<Tag> result = tagService.findByTagTypeAndTagName(id, tagName);

            return mapper.writeValueAsString(result);
        } catch (Exception e) {
            return tagType_controller.handleException(response, e);
        }
    }

    @PostMapping({"/find/{id}"})
    public RequestResult findOneById(@PathVariable("id") Integer id) {
        Optional<Tag> tag = tagService.findById(id);
        if (tag.isEmpty()) {
            return RequestResult.makeFailResult(null, "Tag is not found");
        } else {
            return RequestResult.makeSuccessResult(tag.get(), "Success");
        }
    }

    @PostMapping("/create")
    public RequestResult addNewTagList(@RequestBody List<Tag> tags) {
        RequestResult result = null;
        try {
            for (int i = 0; i < tags.size(); i++) {
                Tag tag = tags.get(i);
                tag = tagService.findOrCreate(tag);
                tags.set(i, tag);
            }
            result = RequestResult.makeSuccessResult(tags, "Create Success");
        } catch (Exception e) {
            result = handleException(e);
        }

        return result;
    }

    @PostMapping("/create1")
    public RequestResult addNewTag(@RequestBody Tag tag) throws Exception {
        RequestResult result = addNewTagList(Arrays.asList(tag));
        if (result.getResult().equals(RequestResult.RequestResultType.SUCCESS)) {
            List<Tag> tags = (List<Tag>) result.getObj();
            tag = tags.get(0);
            result.setMessage("redirect:".concat("/tag/find/").concat(tag.getId() + ""));
        }
        return result;
    }

    @PutMapping("/")
    public RequestResult updateTag(@RequestBody Tag tag) {
        try {
            tag = tagService.updateTag(tag);
            return RequestResult.makeSuccessResult(tag, "".concat("redirect:").concat("/tag/find/").concat(tag.getId() + ""));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/")
    public RequestResult deleteTag(@RequestBody Tag tag) {
        try {
            tag = tagService.deleteTag(tag);
            return RequestResult.makeSuccessResult(tag, "".concat("redirect:").concat("/tag/"));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    public RequestResult handleException(Exception e) {
        return RequestResult.makeFailResult(e, e.getMessage());
    }

}
