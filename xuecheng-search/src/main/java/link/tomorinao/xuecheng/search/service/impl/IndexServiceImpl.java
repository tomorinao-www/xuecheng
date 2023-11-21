package link.tomorinao.xuecheng.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CreateResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.search.po.CourseIndex;
import link.tomorinao.xuecheng.search.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * @author Mr.M
 * @version 1.0
 * @description 课程索引管理接口实现
 * @date 2022/9/25 7:23
 */

@Primary
@Slf4j
@Service
public class IndexServiceImpl implements IndexService {

    @Resource(name = "elasticsearchClient")
    ElasticsearchClient esClient;

    @Override
    public Boolean putDoc(String indexName, String id, CourseIndex doc) {
        try {
            IndexResponse response = esClient.index(i -> i.index(indexName)
                    .id(id)
                    .document(doc));
            log.info(response.result().jsonValue());
            String resultName = response.result().name();
            return ("created".equalsIgnoreCase(resultName) || "updated".equalsIgnoreCase(resultName));
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Boolean updateCourseIndex(String indexName, String id, Object object) {
        try {
//            CourseIndex courseIndex = (CourseIndex) object;
            UpdateResponse<CourseIndex> update = esClient.update(i -> i.index(indexName)
                            .id(id)
                            .doc(object),
                    CourseIndex.class);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Boolean deleteCourseIndex(String indexName, String id) {
        try {
            DeleteResponse deleteResponse = esClient.delete(i -> i.index(indexName)
                    .id(id));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
