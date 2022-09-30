# mbdb介绍
mbdb是一个用Java实现的基于文件存储的 关系型数据库管理系统，由个人开发者马博（mabo）独立开发

mbdb是一个关系型数据库管理系统，将不同表的数据保存在不同的目录下，而不是将所有数据放在一个大仓库内，这样就增加了速度并提高了灵活性

![image](https://user-images.githubusercontent.com/92293323/193221745-e9046dd1-e8f4-431b-9bf5-6fa396931abe.png)


每个表的目录下有三个文件:

data.mbdb存储数据

index.mbdb存储索引

tableDefinition.mbdb存储表结构信息


![image](https://user-images.githubusercontent.com/92293323/193222174-a6b7012c-8671-4d06-b63d-b23d54a46563.png)



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

![image](https://user-images.githubusercontent.com/92293323/193223249-48245148-955f-4e5c-8757-bf9840c3f374.png)

## 索引文件存储结构(index.mbdb)

![image](https://user-images.githubusercontent.com/92293323/193223562-3e522b68-c913-4105-810f-ba1157ea2dfc.png)


## 表结构文件存储结构(tableDefinition.mbdb)
表结构文件存储的是表结构的json字符串

[{"name":"userName","length":"10","remark":"用户姓名","type":"varchar","delete":"1","primary":"false"},{"name":"userAge","length":"10","remark":"用户年龄","type":"int","delete":"1","primary":"false"},{"name":"id","length":"10","remark":"ID","type":"char","delete":"1","primary":"true"}]

