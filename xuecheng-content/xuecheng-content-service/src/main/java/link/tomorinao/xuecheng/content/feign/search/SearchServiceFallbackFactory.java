package link.tomorinao.xuecheng.content.feign.search;

import link.tomorinao.xuecheng.content.feign.search.po.CourseIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SearchServiceFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable cause) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                log.info("添加课程索引发生熔断；courseIndex->{} cause->{}", courseIndex, cause.getMessage(), cause);
                return false;
            }
        };
    }
}
