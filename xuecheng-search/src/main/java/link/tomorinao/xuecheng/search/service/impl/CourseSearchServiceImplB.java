package link.tomorinao.xuecheng.search.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.BucketMetricValueAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.MultiBucketBase;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.ObjectBuilder;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.base.model.PageParams;
import link.tomorinao.xuecheng.search.dto.SearchCourseParamDto;
import link.tomorinao.xuecheng.search.dto.SearchPageResultDto;
import link.tomorinao.xuecheng.search.po.CourseIndex;
import link.tomorinao.xuecheng.search.service.CourseSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程搜索service实现类
 * @date 2022/9/24 22:48
 */
@Slf4j
@Service
@Primary
public class CourseSearchServiceImplB implements CourseSearchService {

    @Value("${elasticsearch.course.index}")
    private String courseIndexStore;
    @Resource
    private ElasticsearchClient esClient;


    @Override
    public SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto) {

        SearchResponse<CourseIndex> response = null;
        // 条件查询
        List<Query> mustList = new ArrayList<>();
        if (searchCourseParamDto != null) {
            String mt = searchCourseParamDto.getMt();
            if (StrUtil.isNotEmpty(mt)) {
                Query byMt = MatchQuery.of(m -> m
                        .field("mtName")
                        .query(mt)
                )._toQuery();
                mustList.add(byMt);
            }
            String st = searchCourseParamDto.getSt();
            if (StrUtil.isNotEmpty(st)) {
                Query bySt = MatchQuery.of(m -> m
                        .field("stName")
                        .query(st)
                )._toQuery();
                mustList.add(bySt);
            }
            String grade = searchCourseParamDto.getGrade();
            if (StrUtil.isNotEmpty(grade)) {
                Query byGrade = MatchQuery.of(m -> m
                        .field("grade")
                        .query(grade)
                )._toQuery();
                mustList.add(byGrade);
            }
            String keywords = searchCourseParamDto.getKeywords();
            if (StrUtil.isNotEmpty(keywords)) {
                mustList.add(MultiMatchQuery.of(m -> m
                        .fields("name", "description")
                        .query(keywords)
                        .minimumShouldMatch("70%")
                )._toQuery());
            }
        }
        try {
            response = esClient.search(search -> search
                            .index(courseIndexStore)
                            .query(q -> q.bool(b -> b.must(mustList))
                            )
                            .from(Math.toIntExact(pageParams.getPageNo()))
                            .size(Math.toIntExact(pageParams.getPageSize()))
                            .aggregations("mtAgg", a -> a
                                    .terms(terms -> terms
                                            .field("mtName")
                                            .size(100)))
                            .aggregations("stAgg", a -> a
                                    .terms(terms -> terms
                                            .field("stName")
                                            .size(100)))
                            .highlight(h -> h
                                    .fields("name", f -> f
                                            .preTags("<font color='red'>")
                                            .postTags("</font>")
                                    )
                            ),
                    CourseIndex.class);
        } catch (IOException e) {
            log.error("查询es出错;pageParams->{};searchCourseParamDto->{}", pageParams, searchCourseParamDto, e);
            XueChengException.cast("查询es出错");
        }

        // 封装结果
        List<Hit<CourseIndex>> hits = response.hits().hits();
        int total = hits.size();
        List<CourseIndex> items = new ArrayList<>(total);
        for (Hit<CourseIndex> hit : hits) {
            CourseIndex item = hit.source();
            // 高亮name
            List<String> hl_name = hit.highlight().get("name");
            if (hl_name != null) {
                StrBuilder strBuilder = new StrBuilder();
                hl_name.forEach(strBuilder::append);
                item.setName(strBuilder.toString());
            }
            items.add(item);
        }
        SearchPageResultDto<CourseIndex> pageResultDto = new SearchPageResultDto<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
        // 分类聚合
        Map<String, Aggregate> aggregations = response.aggregations();
        Aggregate mtAgg = aggregations.get("mtAgg");
        List<String> mtList = mtAgg.sterms().buckets().array().stream().map(bucket -> bucket.key()._get().toString()).toList();
        Aggregate stAgg = aggregations.get("stAgg");
        List<String> stList = stAgg.sterms().buckets().array().stream().map(bucket -> bucket.key()._get().toString()).toList();

        pageResultDto.setMtList(mtList);
        pageResultDto.setStList(stList);
        return pageResultDto;
    }
}
