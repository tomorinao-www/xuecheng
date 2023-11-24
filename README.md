# xuecheng

黑马程序员-学成在线-微服务java项目
https://www.bilibili.com/video/BV1j8411N7Bm

写一些与视频中不同的地方，优化、见解、踩坑等

黑马原项目前后端单词拼写、大小写等错误太多，不一一写了。。。
能复制就不要手敲o(╥﹏╥)o

## 整体

修改xuecheng-plus名字为xuecheng

使用jdk17

使用springboot3.1.4

未使用swagger

使用apifox 公开文档
https://xuecheng-tomorinao.apifox.cn

使用elastic search8.11.1

## 内容管理模块

### 合并了course_base表与course_market表为course_info表。

原来的两张表通过主键相同实现一一对应关系，
但课程接口中只有主页分页查询接口返回的是base信息，
其他的增删改查都传递课程全部信息info。
合并两表方便了增删改查的接口编写。

实际业务中可能会有base基本信息查询接口调用次数远超其他接口的情况，
黑马可能是想模拟这种情况分表。
但分表后他的合并两表写在service层，代码比较混乱，
或许用sql表连接效果会更好。

### 使用mybatis-plus自动填充字段

像创建时间、更新时间这种通用字段，使用mp自动填充功能更方便

### 工具：只拷贝不为null的属性

spring的BeanUtils.copyProperties()会把所有同名同类属性复制覆盖，
这可能会导致null覆盖有值的属性。
因为没找到现成的工具，
所以抄了个copyPropertiesIgnoreNull()

### 合并addCourseDto与editCourseDto

没必要分两个，可能是课程设计问题

### 删除SaveTeachplanDto

没必要

### 修改课程章节orderby字段生成逻辑

新增章节时，orderby字段，视频中使用count + 1，
改为max(orderby) + 1

### 添加teachplan表多列索引(course_id, parentid, orderby)

实现上移、下移接口，在mapper层提供查询上或下一个章节的功能，
再交换orderby的值来实现。
为了更快查询，建立了这样的三列索引

其实，用上移、下移来排序，体验不如拖拽。
拖拽完一批后，再提交给后端，压力也更小。

### 关于课程老师表

黑马仅用一张表course_teacher存储课程的老师信息，
用course_id字段绑定老师对应的课程,
添加了course_id、name两字段联合唯一索引，
这样同一个课程不能有两个重名的老师，不太好。

好的做法是，用teacher表存储老师信息，
用course_teacher关系表存储老师与所教课程关系。

此外，黑马有个接口设计bug。
删除课程老师/content/courseTeacher/course/{courseId}/{id}
不需要courseId，因为按黑马的表设计，主键id即为课程老师id

## 媒资管理模块

### 关于文件信息数据库表

黑马使用md5值做主键，这不太好，
且id与file_id字段都是md5，重复（后面file_id优化为etag）

黑马用md5进行文件去重，
但是生成minio桶内文件路径时使用日期/yyyy/MM/dd/。
如果两个用户上传了同样的文件，md5值是相同的， 但上传的日期是不同的，
去重之后，后上传的文件既不会在数据库表增加记录，也不会在minio存储第二份，
而是使用相同的一行表记录和minio文件，这就导致：

1. 没有达到按日期分路径存储的效果，后上传的文件与先上传的文件路径完全相同
2. 如果有任何一个用户删除了文件，一般只删除数据库表，但相同文件的拥有者却全都找不到这个文件，因为数据库表也不重复记录。

如果不删除数据库表，那么日期路径与真实日期不一致。
如果别的表存储了文件访问路径，不需要访问文件表，那么文件表没有必要创建。

如果需要去重，就不该使用日期路径存储。
如果需要用日期路径存储，就不应该仅按md5去重。
主键一定不能用md5，否则重复文件无法记录。

因为接口定义是那样的，不好修改。。

### 新增抽离minio工具类+nacos配置更新重构client

各种文件上传都要上传到minio，那么minio工具类就该被抽离出来。

用@ConfigurationProperties+@Configuration+@Bean的组合，提供minioUtil工具bean

静态内部类实现嵌套配置，简洁优雅

```java

@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private Bucket bucket;

    @Data
    public static class Bucket {
        private String media;
        private String video;
    }
}
```

minio的配置是用来构建MinioClient的。
这种客户端一般不会重复构造，而是初始化时构造并放在属性里。

nacos配置更新只会更新绑定了配置的配置类MinioConfig，而不会更新MinioClient。
为了实现配置更新后，重新构造MinioClient，需要监听事件。

nacos配置自动刷新本身是靠事件监听实现的，监听Spring的ApplicationReadyEvent事件。

nacos完成配置刷新后，会发送一个RefreshScopeRefreshedEvent事件，
监听这个事件，重新构造MinioClient。

nacos配置刷新，不会改变MinioConfig配置类实例的引用，而是修改它的属性，
而我们保留了MinioConfig的引用，所以能够拿到更新后的MinioConfig

```java
public class MinioUtil {
    private MinioConfig minioConfig;
    private MinioClient minioClient;
    private String endpoint;
    // 公开存储桶，方便调用方查看桶、选择桶
    public MinioConfig.Bucket bucket;


    public MinioUtil(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
        refresh();
    }

    /*
    配置属性在刷新完成之后，会发送一个RefreshScopeRefreshedEvent事件，
    监听这个事件，重新构造MinioClient
     */
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void refresh() {
        this.endpoint = minioConfig.getEndpoint();
        this.bucket = minioConfig.getBucket();
        this.minioClient = MinioClient.builder()
                .endpoint(this.endpoint)
                .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
                .build();
    }
}
```

其实，像数据库连接、tomcat服务端口这些配置，仅在程序启动时读取并进行初始化操作，
nacos配置更新并不会引起数据库重新连接、端口重新开放等。

### 优化：合并文件校验改用etag

黑马校验分块合并文件，是服务器从minio下载整个文件，并计算md5来校验。
这样做耗时、占用网络且没有必要。

其实，minio有etag生成策略。

小文件上传，etag就是md5的16进制表示。

大文件分块上传，etag长这样：{sum_md5}-{num}

减号前的sum_md5，是每块文件的md5拼接后再求md5hex；
减号后的num，是分块的个数。

直接用etag就可以校验大文件完整性，不需要再下载一遍。

为了使用etag校验，需要修改前端项目，让前端计算etag并传给后端。

在uploadtools.ts的主函数uploadByPieces中加入以下代码，并将其改为异步函数。
在提交合并api mergeChunks调用中增加字段etag: etag

```ts
const blob2md5 = async blob => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onloadend = () => {
            resolve(CryptoJS.MD5(CryptoJS.enc.Latin1.parse(reader.result)))
        }
        reader.onerror = reject
        reader.readAsBinaryString(blob)
    })
}

const calcEtag = async () => {
    const view_uint8 = new Uint8Array(16 * chunkCount)
    for (let i = 0; i < chunkCount; i++) {
        let i_chunk = getChunkInfo(file, i, chunkSize)
        let i_md5 = (await blob2md5(i_chunk)) as any
        let i_md5hex = i_md5.toString()
        for (let j = 0; j < 16; j++) {
            const byteString = i_md5hex.substring(j * 2, j * 2 + 2) // 从字符串中截取两个字符
            const byteValue = parseInt(byteString, 16) // 将两个字符解析为 16 进制数值
            view_uint8[i * 16 + j] = byteValue // 存入 Uint8Array 视图
        }
    }
    let etag_md5 = await blob2md5(new Blob([view_uint8.buffer]))
    return `${etag_md5}-${chunkCount}`
}
const etag = await calcEtag()
```

这样后端就只需要拿到前端传的etag和minio的etag，对比一下就好啦~

正好黑马有个file_id字段和主键md5hex重复，用它存etag。
原始字段varchar(32)不够用，改成varchar(36)

### 升级: 使用es8.11新java-api客户端

新版官方文档
https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/getting-started-java.html

参考博客
- https://blog.csdn.net/weixin_43407520/article/details/127351598
- https://blog.csdn.net/anjiongyi/article/details/123328856

新版官方文档少得可怜，全靠大家自己摸索，看源码doc

注意，如果属性中有日期时间，还需要在创建es客户端时注册时间模块

新版es默认开启安全认证，使用环境变量禁用安全认证
xpack.security.enabled: false

```java
@Configuration
public class ElasticsearchConfig {
    @Bean
    public ElasticsearchClient esClient() {
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
```
