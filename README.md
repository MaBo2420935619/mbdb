

# mbdb介绍
**github地址   https://github.com/MaBo2420935619/mbdb**


mbdb是一个用Java实现的基于文件存储的 关系型数据库管理系统，由个人开发者mabo独立开发

mbdb是一个关系型数据库管理系统，将不同表的数据保存在不同的目录下，而不是将所有数据放在一个大仓库内，这样就增加了速度并提高了灵活性

![image](https://img-blog.csdnimg.cn/img_convert/c5df7e16c7df2f26bf6ed10c93c1bb80.png)


每个表的目录下有三个文件:

data.mbdb存储数据

index.mbdb存储索引

tableDefinition.mbdb存储表结构信息


![image](https://img-blog.csdnimg.cn/img_convert/c39e2af73d31a6897cb7c1694456af0b.png)



# mbdb功能介绍

## 创建表
mbdb数据库目前只有表的概念，所有的操作都是基于表进行操作
mbdb支持json格式对表结构进行定义，添加数据之前需要首先定义表结构。
## 查
查询数据目前仅支持主键查询
mbdb支持两种查询方式，全表查询和索引查询
索引查询采用二分法读取索引文件，确定索引位置，再根据索引位置进行数据查询，目前测试的性能为一百万条数据查询速度为3.6s
## 增
mbdb增加数据采用追加的方式进行数据的新增操作，新增数据支持批量新增和单条新增
数据量较大时建议采用批量新增，因为新增数据时，会重新生成索引文件，对系统性能消耗较大
## 删
mbdb删除数据采用逻辑删除，即将该条数据的标志位改为1，则该条数据删除成功
这种方式可以减少其他数据的移动，减少系统性能的损耗

## 改
mbdb支持根据主键修改数据，底层逻辑为首先删除改数据，再将需要修改的数据新增至数据文件（data.mbdb文件）

# 数据存储结构介绍

## 数据文件存储结构(data.mbdb)
如表头所示，数据用符号 | 分割，delete是数据是否被删除的标志位，其余位分别对应数据的名称
![image](https://img-blog.csdnimg.cn/img_convert/332df363379ded88175f780da070dcc9.png)

## 索引文件存储结构(index.mbdb)
索引文件一条数据存储了三条数据用符号 | 进行分割，key存储数据的唯一id，position存储数据的指针，方便查询数据，表头的999008用于表示数据的数据量大小，因为查询数据采用二分法查询，所以需要记录数据的数据量。
![image](https://img-blog.csdnimg.cn/img_convert/06108a686a628d82510e23098feebd68.png)


## 表结构文件存储结构(tableDefinition.mbdb)
表结构文件存储的是表结构的json字符串

[{"name":"userName","length":"10","remark":"用户姓名","type":"varchar","delete":"1","primary":"false"},{"name":"userAge","length":"10","remark":"用户年龄","type":"int","delete":"1","primary":"false"},{"name":"id","length":"10","remark":"ID","type":"char","delete":"1","primary":"true"}]

该结构可以和数据库一样，存储表的字段信息
![在这里插入图片描述](https://img-blog.csdnimg.cn/bb66581a6bd74b0e80e7b2205d6f0495.png)


# 如何使用
![在这里插入图片描述](https://img-blog.csdnimg.cn/8ebcfe01eaac413f80042bdfd061d2cd.png)

#  优化点
目前madb仅支持基于主键查询、修改、删除操作。
查询算法采用二分法，有很大的优化空间。
数据的索引可以使用缓存进行存储。
索引中可以添数据的起始位置和结束位置，查询速度更优。
