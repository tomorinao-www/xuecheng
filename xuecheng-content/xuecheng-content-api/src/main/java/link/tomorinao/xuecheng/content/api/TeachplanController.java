package link.tomorinao.xuecheng.content.api;

import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.content.model.dto.TeachplanDto;
import link.tomorinao.xuecheng.content.service.ITeachplanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/teachplan")
public class TeachplanController {

    @Resource
    private ITeachplanService iTeachplanService;

    @GetMapping("/{id}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long id){

        return iTeachplanService.getTreeNodes(id);
    }

    @PostMapping
    public void addOrUpdateById(@RequestBody TeachplanDto dto){
        iTeachplanService.addOrUpdateById(dto);
    }


    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        iTeachplanService.deleteById(id);
    }

    @PostMapping("/moveup/{id}")
    public void moveup(@PathVariable Long id){
        iTeachplanService.moveup(id);
    }

    @PostMapping("/movedown/{id}")
    public void movedown(@PathVariable Long id){
        iTeachplanService.movedown(id);
    }
}
