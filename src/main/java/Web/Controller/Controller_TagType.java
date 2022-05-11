package Web.Controller;

import Data.Entity.TagType;
import Web.Service.TypeEditor.Serv_Tag_Type_Impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/tagType")
public class Controller_TagType {

    private Serv_Tag_Type_Impl tagTypeService;
    private ObjectMapper mapper;

    @Autowired
    public Controller_TagType(Serv_Tag_Type_Impl tagTypeService, ObjectMapper mapper) {
        this.tagTypeService = tagTypeService;
        this.mapper = mapper;
    }

    @GetMapping("/")
    public ModelAndView getView(ModelAndView mav){
        mav.setViewName("/TagType/main");
        return mav;
    }

    @GetMapping(produces = "application/json")
    public String getAllTagType() throws JsonProcessingException {
        return mapper.writeValueAsString(tagTypeService.getAllTagType());
    }

    @PostMapping(produces = "application/json")
    public String createNewType(@RequestBody List<TagType> types, HttpServletResponse rep) {
        try {
            for (int i = 0; i < types.size(); i++) {
                TagType tagType = types.get(i);
                if (tagType.getId() != null) {
                    throw new IllegalArgumentException
                            ("Wrong request TagType have ID\nIf you want to update use [PUT] request");
                }
                tagType = tagTypeService.saveTagType(tagType);
                types.set(i, tagType);
            }
            return getAllTagType();
        } catch (DataIntegrityViolationException e){
            return handleException(rep,e,"Tag is already exists");
        } catch (Exception e) {
            return handleException(rep,e);
        }
    }

    @PutMapping(produces = "application/json")
    public String alterType(@RequestBody List<TagType> types,HttpServletResponse rep){
        try{
            for (int i = 0; i < types.size(); i++) {
                TagType type = types.get(i);
                type = tagTypeService.saveTagType(type);
                types.set(i,type);
            }
            return getAllTagType();
        }catch (Exception e){
            return handleException(rep,e);
        }
    }

    public String handleException(HttpServletResponse rep,Exception e){
        return handleException(rep,e,e.getMessage());
    }

    public String handleException(HttpServletResponse rep,Exception e,String msg){
        rep.setStatus(HttpServletResponse.SC_CONFLICT);
        e.printStackTrace();
        return msg;
    }
}
