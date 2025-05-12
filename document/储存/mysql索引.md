## 索引失效场景
### 1.索引列进行了运算或函数操作
如果对索引列进行了运算或使用了函数，MySQL无法使用索引，会导致索引失效。例如，对于以下查询语句：

SELECT * FROM table_name WHERE YEAR(date_column) = 2022;

如果date_column是索引列，但由于使用了YEAR函数，索引失效。

### 2. 使用了不匹配索引的LIKE查询：
当使用LIKE进行模糊查询时，如果通配符在开头，MySQL无法使用索引。 
例如，对于以下查询语句：

SELECT * FROM table_name WHERE column_name LIKE '%value%';

索引失效。
### 3.类型不匹配
当查询条件的数据类型与索引列的数据类型不匹配时，MySQL无法使用索引。例如，对于以下查询语句：

SELECT * FROM table_name WHERE int_column = 'value';

如果int_column是整数类型的索引列，但查询条件是字符串类型，会导致索引失效。
### 4.范围查询中的左前缀：

当使用范围查询时，如果索引列只在范围的右边，MySQL无法使用索引。例如，对于以下查询语句：

SELECT * FROM table_name WHERE indexed_column > 10 AND non_indexed_column = 'value';

索引失效。

### 5.使用OR连接的条件

当查询条件中使用了OR连接多个条件时，如果其中有一个条件无法使用索引，整个查询可能会导致索引失效。例如，对于以下查询语句：

SELECT * FROM table_name WHERE indexed_column = 'value1' OR non_indexed_column = 'value2';
1
如果non_indexed_column未创建索引，整个查询可能会导致索引失效。

### 6. 索引列使用 is not null 查询。
