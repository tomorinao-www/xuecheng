package link.tomorinao.xuecheng.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.transport.Header;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@SpringBootTest
public class ElasticSearchTest {

    @Resource
    ElasticsearchClient esClient;

//    {
//        // URL and API key
//        String serverUrl = "http://localhost:9200";
//        String apiKey = "VnVhQ2ZHY0JDZGJrU...";
//
//        // Create the low-level client
//        RestClient restClient = RestClient.builder(HttpHost.create(serverUrl))
//                //                .setDefaultHeaders(new Header[]{
//                //                        new BasicHeader("Authorization", "ApiKey " + apiKey)
//                //                })
//                .build();
//
//        // Create the transport with a Jackson mapper
//        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper();
//        jsonpMapper.objectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//        DateFormat dateFormat = jsonpMapper.objectMapper().getDateFormat();
//        ElasticsearchTransport transport = new RestClientTransport(restClient, jsonpMapper);
//
//        // And create the API client
//        this.esClient = new ElasticsearchClient(transport);
//    }

    @Test
    void create_index() throws IOException {
        esClient.indices().create(c -> c.index("products"));
    }

    @Test
    void get_index() throws IOException {
        GetIndexResponse createIndexResponse = esClient.indices().get(e->e.index("products"));
        System.out.println(String.join(",", createIndexResponse.result().keySet()));

    }

    @Test
    void add_doc() throws IOException {
        Product product = new Product(1L, "City bike1", 123.0, LocalDateTime.now());

        IndexResponse response = esClient.index(i -> i
                .index("products")
                .id(product.getId().toString())
                .document(product)
        );
        System.out.println(response);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Product{
    Long id;
    String name;
    Double price;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createTime;
}
