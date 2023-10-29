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
