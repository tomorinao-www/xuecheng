package link.tomorinao.xuecheng;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Collections;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/xc_content", "root", "local")
                .globalConfig(builder -> {
                    builder.author("tomorinao") // 设置作者
//                            .enableSwagger() // 开启 swagger 模式
                            .outputDir("E:\\work\\mytmp\\mp"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("link.tomorinao.xuecheng.content.model") // 设置父包名
//                            .moduleName("model") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "E:\\work\\mytmp\\mp")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("course_audit," +
                                    "course_base," +
                                    "course_category," +
                                    "course_market," +
                                    "course_publish," +
                                    "course_publish_pre," +
                                    "course_teacher," +
                                    "mq_message," +
                                    "mq_message_history," +
                                    "teachplan," +
                                    "teachplan_media," +
                                    "teachplan_work") // 设置需要生成的表名
                            .addTablePrefix("foo_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
