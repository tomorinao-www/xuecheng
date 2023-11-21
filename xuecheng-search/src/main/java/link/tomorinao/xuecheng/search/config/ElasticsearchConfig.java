package link.tomorinao.xuecheng.search.config;

import cn.hutool.core.util.ArrayUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.hostlist}")
    private String hostlist;

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        //解析hostlist配置信息
        String[] split = hostlist.split(",");
        //创建HttpHost数组，其中存放es主机和端口的配置信息
        HttpHost[] httpHostArray = new HttpHost[split.length];
        for(int i=0;i<split.length;i++){
            String item = split[i];
            httpHostArray[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "http");
        }
        //创建RestHighLevelClient客户端
        return new RestHighLevelClient(RestClient.builder(httpHostArray));
    }


    @Bean
    public ElasticsearchClient elasticsearchClient(){
        // URL and API key
        String serverUrl = "http://localhost:9200";
        String apiKey = "VnVhQ2ZHY0JDZGJrU...";

        // Create the low-level client
        List<String> serverUrlList = new ArrayList<>();
        serverUrlList.add("http://localhost:9200");

        List<HttpHost> httpHostList = serverUrlList.stream().map(HttpHost::create).toList();
        HttpHost[] httpHostArray = ArrayUtil.toArray(httpHostList, HttpHost.class);

        RestClient restClient = RestClient.builder(httpHostArray)
                //                .setDefaultHeaders(new Header[]{
                //                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                //                })
                .build();

        // Create the transport with a Jackson mapper
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper();
        // 重要！这里不注册时间模块，会报错，找了好久问题
        jsonpMapper.objectMapper().registerModule(new JavaTimeModule());
        ElasticsearchTransport transport = new RestClientTransport(restClient, jsonpMapper);

        // And create the API client
        ElasticsearchClient elasticsearchClient = new ElasticsearchClient(transport);
        return elasticsearchClient;
    }
}