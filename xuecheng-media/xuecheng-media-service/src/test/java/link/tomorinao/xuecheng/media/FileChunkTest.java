package link.tomorinao.xuecheng.media;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.digest.DigestUtil;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.StatObjectResponse;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.media.minio.MinioUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.*;


@SpringBootTest
public class FileChunkTest {


    String filename = "公主连结MMD 爱梅斯大人的舞蹈练习.mp4";
    File srcfile = new File("E:\\aaayuan\\视频\\MMD全年龄\\mytmp\\" + filename);
    String chunckPath = "E:\\aaayuan\\视频\\MMD全年龄\\mytmp\\chunk";
    File merge_file = new File("E:\\aaayuan\\视频\\MMD全年龄\\mytmp\\merge\\" + filename);
    File chunk_folder = new File(chunckPath);

    @Test
    void chunk() throws Exception {

        int chunk5MB = 1024 * 1024 * 5;
        int chunkNum = (int) Math.ceil(srcfile.length() * 1.0 / chunk5MB);
        RandomAccessFile srcStream = new RandomAccessFile(srcfile, "r");
        byte[] buffer = new byte[1024];
        File[] files = chunk_folder.listFiles();
        for (File f : files) {
            f.delete();
        }
        for (int i = 0; i < chunkNum; i++) {
            File chunkfile = new File(chunckPath + "\\" + i);
            if (chunkfile.exists()) {
                chunkfile.delete();
            }
            chunkfile.createNewFile();
            try (RandomAccessFile chunkStream = new RandomAccessFile(chunkfile, "rw")) {
                int len = -1;
                while ((len = srcStream.read(buffer)) != -1) {
                    chunkStream.write(buffer, 0, len);
                    if (chunkfile.length() >= chunk5MB) {
                        break;
                    }
                }
            }
        }
        srcStream.close();
    }


    @Test
    void merge() throws Exception {
        if (merge_file.exists()) {
            merge_file.delete();
            merge_file.createNewFile();
        }

        File[] files = chunk_folder.listFiles();
        RandomAccessFile merge_stream = new RandomAccessFile(merge_file, "rw");
        byte[] buffer = new byte[1024];
//        List<File> fileList = Arrays.stream(files).sorted((f1, f2) -> Integer.valueOf(f1.getName()).compareTo(Integer.valueOf(f2.getName()))).toList();
        List<File> fileList = Arrays.stream(files).sorted(Comparator.comparing(f -> Integer.valueOf(f.getName()))).toList();
        for (File f : fileList) {
            try (RandomAccessFile chunk_stream = new RandomAccessFile(f, "r")) {
                int len = -1;
                while ((len = chunk_stream.read(buffer)) != -1) {
                    merge_stream.write(buffer, 0, len);
                }
            }
        }
        merge_stream.close();
    }

    @Test
    void md5test() throws Exception {
        try (FileInputStream fs1 = new FileInputStream(srcfile); FileInputStream fs2 = new FileInputStream(merge_file);) {
            String md51 = DigestUtil.md5Hex(fs1);
            String md52 = DigestUtil.md5Hex(fs2);
            System.out.println(md51);
            System.out.println(md52);
            System.out.println(md51.equals(md52));
        }
    }

    @Resource
    private MinioUtil minioUtil;

    @Test
    void upload_chunk() throws Exception {
        File[] files = chunk_folder.listFiles();
        for (File f : files) {
            String url = minioUtil.upload(f, "chunk/" + f.getName(), minioUtil.bucket.getTest());
        }
    }

    @Test
    void compose_chunk() throws Exception {
        List<String> src_names = Arrays.stream(chunk_folder.listFiles()).sorted(Comparator.comparing(f -> Integer.valueOf(f.getName()))).map(f -> "chunk/" + f.getName()).toList();
        minioUtil.compose(src_names, srcfile.getName(), minioUtil.bucket.getTest());

    }

    @Test
    void test_etag() throws Exception {
        StatObjectResponse stat = minioUtil.statObject(srcfile.getName(), minioUtil.bucket.getTest());
        System.out.println(stat);

        String minio_etag = stat.etag();
        File[] files = chunk_folder.listFiles();
        List<File> fileList = Arrays.stream(files).sorted(Comparator.comparing(f -> Integer.valueOf(f.getName()))).toList();


//        Object[] array = Arrays.stream(files).map(DigestUtil::md5).toArray();//报错
//        System.out.println(array[0].getClass());
//        byte[][] md5_arr = (byte[][]) Arrays.stream(files).map(DigestUtil::md5).toArray(); //报错
//        byte[] md5_sum = ArrayUtil.addAll(md5_arr);
//        String etag = DigestUtil.md5Hex(md5_sum);

        byte[][] md5_arr = new byte[fileList.size()][];
        for (int i = 0; i < fileList.size(); i++) {
            md5_arr[i] = DigestUtil.md5(fileList.get(i));
        }

        byte[] md5_sum = ArrayUtil.addAll(md5_arr);
        String etag = DigestUtil.md5Hex(md5_sum);

        System.out.println(Arrays.deepToString(md5_arr));
        System.out.println(etag);
        System.out.println(minio_etag);
        System.out.println(Objects.equals(etag, minio_etag));
    }

}
