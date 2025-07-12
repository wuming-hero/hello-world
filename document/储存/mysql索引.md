## 最左前缀原则 
MySQL索引遵循最左前缀原则（Leftmost Prefix Principle） 的核心原因在于其底层数据结构 B+树索引 的排序和存储方式。

### 有序存储
B+树索引是一种多路平衡搜索树，其叶子节点存储了实际的数据值（对于聚集索引是数据行，对于二级索引是主键值+索引列值），并且这些叶子节点通过指针相互连接，形成一个有序链表。

### 索引列的顺序至关重要： 
当创建一个联合索引（也叫复合索引）idx_col1_col2_col3 (col1, col2, col3) 时：
* 第一排序依据： 整个叶子节点中的数据首先严格按照 col1 的值进行排序。
* 第二排序依据： 在 col1 值相同的情况下，再按照 col2 的值进行排序。
* 第三排序依据： 在 col1 和 col2 的值都相同的情况下，最后才按照 col3 的值进行排序。

这种排序就像是字典的目录：
* 首先按第一个字母排序（相当于 col1）。
* 在第一个字母相同时，按第二个字母排序（相当于 col2）。
* 在第一个和第二个字母都相同时，按第三个字母排序（相当于 col3）。 

## 索引失效场景
### 1.索引列进行了运算或函数操作
如果对索引列进行了运算或使用了函数，MySQL无法使用索引，会导致索引失效。例如，对于以下查询语句：

SELECT * FROM table_name WHERE YEAR(date_column) = 2022;

如果date_column是索引列，但由于使用了YEAR函数，索引失效。

### 2. 使用了不匹配索引的LIKE查询：
当使用LIKE进行模糊查询时，如果通配符在开头，MySQL无法使用索引。 
例如，对于以下查询语句：

SELECT * FROM table_name WHERE column_name LIKE '%value%';

### 3.类型不匹配
当查询条件的数据类型与索引列的数据类型不匹配时，MySQL无法使用索引。例如，对于以下查询语句：

SELECT * FROM table_name WHERE int_column = 'value';

如果int_column是整数类型的索引列，但查询条件是字符串类型，会导致索引失效。

### 4.范围查询中的左前缀：

当使用范围查询时，如果索引列只在范围的右边，MySQL无法使用索引。例如，对于以下查询语句：

SELECT * FROM table_name WHERE indexed_column > 10 AND non_indexed_column = 'value';


### 5.使用OR连接的条件

当查询条件中使用了OR连接多个条件时，如果其中有一个条件无法使用索引，整个查询可能会导致索引失效。例如，对于以下查询语句：

SELECT * FROM table_name WHERE indexed_column = 'value1' OR non_indexed_column = 'value2';
1
如果non_indexed_column未创建索引，整个查询可能会导致索引失效。

### 6. 索引列使用 is not null 查询。
