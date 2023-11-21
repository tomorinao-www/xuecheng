package link.tomorinao.xuecheng.content.feign.search;


import link.tomorinao.xuecheng.content.feign.search.po.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "search",fallbackFactory = SearchServiceFallbackFactory.class)
public interface SearchServiceClient {
    @PostMapping("/search/index/course")
    Boolean add(@RequestBody CourseIndex courseIndex);
}
