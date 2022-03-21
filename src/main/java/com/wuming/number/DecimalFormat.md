# 一、前言
原来没有接触过DecimalFormat，没有想到还有这玩意。使用的时候还是不错的，详细的了解可以看官方的api。找到了中文的api,小伙子们可以直接跳转啊！
DecimalFormat 是 NumberFormat 的一个具体子类，用于格式化十进制数字。
DecimalFormat 包含一个模式 和一组符号。差点忘记说它是干嘛的。

符号含义：
上一个经典的表格，我们来说一说响应的符号的意义：

|符号|位置|本地化|含义
|---- | ---- | ---- | ----|
|0  |数字| 	是|	阿拉伯数字|
|#	|数字|	是|	阿拉伯数字如果不存在就显示为空|
|.	|数字|	是|	小数分隔符或货币小数分隔符|
|-	|数字|	是|	减号|
|,	|数字|	是|	分组分隔符|
|E	|数字|	是|	分割科学技术法中的尾数和指数。在前缀和后缀中无需添加引号|
|;	|子模式边界|	是|	分隔正数和负数子模式|
|%	|前缀或后缀|	是|	乘以100并显示为百分数|
|/u2030	|前缀或后缀|	是|	乘以1000并显示为千分数|
|¤ (\u00A4)	|前缀或后缀|	否|	货币记号，由货币符号替换。如果两个同时出现，则用国际货币符号替换。如果出现在某个模式中，则使用货币小数分隔符，而不使用小数分隔符|
|'	|前缀或后缀|	否|	用于在前缀或或后缀中为特殊字符加引号，例如 "'#'#" 将 123 格式化为 "#123"。要创建单引号本身，请连续使用两个单引号："# o''clock"|
# 二、 分析问题和实战

## 1.最基本的使用
### 1.1 0和#配合使用
网上的例子还是比较多的，我也感觉很有代表性，我也借鉴一下。下面直接上代码：
```java
double pi = 3.1415927;//圆周率
//取一位整数
System.out.println(new DecimalFormat("0").format(pi));//3
//取一位整数和两位小数
System.out.println(new DecimalFormat("0.00").format(pi));//3.14
//取两位整数和三位小数，整数不足部分以0填补。
System.out.println(new DecimalFormat("00.000").format(pi));// 03.142
//取所有整数部分
System.out.println(new DecimalFormat("#").format(pi));//3
//以百分比方式计数，并取两位小数
System.out.println(new DecimalFormat("#.##%").format(pi));//314.16%

/**
 * 上面的代码就是网上很经典的案例，下面我们来分析另外的一个值
 */      
  pi=12.34567;
  //取一位整数
  System.out.println(new DecimalFormat("0").format(pi));//12
  //取一位整数和两位小数
  System.out.println(new DecimalFormat("0.00").format(pi));//12.35
  //取两位整数和三位小数，整数不足部分以0填补。
  System.out.println(new DecimalFormat("00.000").format(pi));// 12.346
  //取所有整数部分
  System.out.println(new DecimalFormat("#").format(pi));//12
  //以百分比方式计数，并取两位小数
  System.out.println(new DecimalFormat("#.##%").format(pi));//1234.57%

/**
* 扩展，如果是其他的数字会是下面的效果
  */
  pi=12.34;
  //整数
  System.out.println(new DecimalFormat("6").format(pi));//612
  System.out.println(new DecimalFormat("60").format(pi));//612
  System.out.println(new DecimalFormat("06").format(pi));//126
  System.out.println(new DecimalFormat("00600").format(pi));//00126
  System.out.println(new DecimalFormat("#####60000").format(pi));//00126
  //小数
  System.out.println(new DecimalFormat(".6").format(pi));//12.6
  System.out.println(new DecimalFormat(".06").format(pi));//12.36
  System.out.println(new DecimalFormat(".60").format(pi));//12.36
  System.out.println(new DecimalFormat(".0600").format(pi));//12.3406
  System.out.println(new DecimalFormat(".6000").format(pi));//12.3406
  System.out.println(new DecimalFormat(".600000##").format(pi));//12.340006
```

上面的例子基本满足我们想要的格式化的一些东西了。我们来对比分析一下上面两个值，很明显.就是我们常用的小数点分隔符，前面是整数，后面是小数。

1. 整数：若是n个0，就从个位开始向高位填充，如果有值就是原来的值，没有就填充0。
若都是#，没有实际意义，不管是几个#，最后的结果都是原来的整数。
0和#配合使用，只能是"##00",不能是"00##",就是#在前0在后。实现是上面的合集。
2. 小数：是可以保留小数点后几位的（几个0后或几个#）。
若n个0，就是保留n位小数，小数不足的部分用0填充。
若n个#，就是保留n位小数，小数不足部分没有就是没有。
0和#配合使用，只能是".00##",不能是".##00",就是0在前#在后。实现和上面一样。
3. 数字（1-9）：上面的分析不是#就是0，如果是其他的数值会怎样呢？
上面的扩展很详细的说明这个问题。
整数：若没有0或#，默认在后面拼接整数；若有0或#，找到第一个0或#的位置，然后找出所有的0或#拼在一起，按照上面的规则，在第一个0或#出现的位置插入响应的格式化以后的值。
小数：若没有0或#，格式化是什么就显示什么；若有0或#，找出所有的0或#拼在一起，按照上面的规则，在小数点的后面插入响应的格式化以后的值。

有了上面的总结，想生成什么就是什么，就是这么人性！

## 2.科学计数法 E
在使用double的时候如果后面的小数为过多就会自动转换为科学计数法，你听听这名字多么高级，科学计数法。
来吧我们直接上代码然后分析：
```java
pi = 123456789.3456;
System.out.println(new DecimalFormat("0E0").format(pi));//1E8
System.out.println(new DecimalFormat("0E00").format(pi));//1E08
System.out.println(new DecimalFormat("#E0").format(pi));//.1E9
System.out.println(new DecimalFormat("##E0").format(pi));//1.2E8
System.out.println(new DecimalFormat("###E0").format(pi));//123E6
System.out.println(new DecimalFormat("####E0").format(pi));//1.235E8
System.out.println(new DecimalFormat("#####E0").format(pi));//1234.6E5
System.out.println(new DecimalFormat("######E0").format(pi));//123.457E6
System.out.println(new DecimalFormat("#######E0").format(pi));//12.34568E7
System.out.println(new DecimalFormat("########E0").format(pi));//1.2345679E8
System.out.println(new DecimalFormat("#########E0").format(pi));//123456789E0
System.out.println(new DecimalFormat("##########E0").format(pi));//123456789.3E0

pi = 12345678.3456;
System.out.println(new DecimalFormat("0E0").format(pi));//1E7
System.out.println(new DecimalFormat("0E00").format(pi));//1E07
System.out.println(new DecimalFormat("#E0").format(pi));//.1E8
System.out.println(new DecimalFormat("##E0").format(pi));//12E6
System.out.println(new DecimalFormat("###E0").format(pi));//12.3E6
System.out.println(new DecimalFormat("####E0").format(pi));//1235E4
System.out.println(new DecimalFormat("#####E0").format(pi));//123.46E5
System.out.println(new DecimalFormat("######E0").format(pi));//12.3457E6
System.out.println(new DecimalFormat("#######E0").format(pi));//12.34568E7
System.out.println(new DecimalFormat("########E0").format(pi));//12345678E0
System.out.println(new DecimalFormat("#########E0").format(pi));//12345678.3E0
System.out.println(new DecimalFormat("##########E0").format(pi));//12345678.35E0

/**
* 0的个数决定最后输出结果的位数
* 并且与0的位置无关
  */
  pi = 12345;
  System.out.println(new DecimalFormat("###.##E0").format(pi));//12.345E3
  System.out.println(new DecimalFormat("##0.##E0").format(pi));//12.345E3
  System.out.println(new DecimalFormat("##0.0##E0").format(pi));//12.345E3
  System.out.println(new DecimalFormat("##0.00000##E0").format(pi));//12.3450E3
  System.out.println(new DecimalFormat("#00.0000##E0").format(pi));//12.3450E3
  System.out.println(new DecimalFormat("#00.00000##E0").format(pi));//12.34500E3
  上面的例子我感觉还是比较全的，看看例子分析一下，就能明白了。
```

总结：
1.使用科学计数法，首先保证E前面有0或者#，否则就不是科学计数法。
2.E后面必须是0，0的个数对后面的显示是有影响的，多余就会填充0.
3.E前面只有一个#，得到的结果肯定是.开头的结果。
4.E前面#与0的总个数决定后面的指数，具体：总个数和指数比较，如果指数的值大于总个数，那么得到的指数的值是个数的倍数；如果指数的值小于等于总个数，那么得到的指数的值等于总个数；
5.整个模式中的0的总个数决定最后输出结果的位数，并且与0的位置无关。
6.如果整数部分需要保留几位数，就使用几个0。

## 3.分组分隔符和减号
### 3.1分组分隔符 ,
这不就是逗号么？不这是分隔符
直接上代码：
```java
pi = 1299792458;
//每三位以逗号进行分隔。
System.out.println(new DecimalFormat(",###").format(pi));//1,299,792,458
System.out.println(new DecimalFormat(",##").format(pi));//12,99,79,24,58
System.out.println(new DecimalFormat("###,##").format(pi));//12,99,79,24,58
```
上面的代码，最常用的就是千位分隔符。
不管模式中有多少个分隔符，最右边的那一个有效；每一组的个数就是最右边的分隔符之右的整数位数。

### 3.2 减号 -
-表示输出为负数， 要放在最前面。代码如下：
```java
pi = 3.14;
System.out.println(new DecimalFormat("-0.00").format(pi));//-3.14
```

##  4. 关于前缀、后缀
### 4.1 % 将数字乘以100
```java
   pi = 0.1234;
   System.out.println(new DecimalFormat("0.00%").format(pi));//12.34%
   System.out.println(new DecimalFormat("0%.00").format(pi));//12.34%
   System.out.println(new DecimalFormat("%0.00").format(pi));//%12.34
   %处理最前面不能放置之外，其他的地方都可以放置。
```

### 4.2 \u2030 将数字乘以1000
```java
pi = 0.1234;
System.out.println(new DecimalFormat("0.00\u2030").format(pi));//123.40‰
System.out.println(new DecimalFormat("0.0\u20300").format(pi));//123.40‰
System.out.println(new DecimalFormat("\u20300.00").format(pi));//‰123.40
\u2030和%用法是一样的。
```

### 4.3 ¤(\u00A4) 本地化货币符号
如果连续出现两次，代表货币符号的国际代号。
```java
pi = 1234.5678;
System.out.println(new DecimalFormat(",000.00¤").format(pi));//1,234.57￥
System.out.println(new DecimalFormat(",000.¤00").format(pi));//1,234.57￥
System.out.println(new DecimalFormat("¤,000.00").format(pi));//￥1,234.57
System.out.println(new DecimalFormat(",00¤0.¤00").format(pi));//1,234.57￥￥
System.out.println(new DecimalFormat("¤,000.¤00").format(pi));//￥1,234.57￥
System.out.println(new DecimalFormat(",000.00¤¤").format(pi));//1,234.57CNY
```

### 4.4 ' 用于引用特殊的字符，作为前缀或后缀。
```java
pi = 4.5678;
System.out.println(new DecimalFormat("'#'0.00").format(pi));//#4.57
System.out.println(new DecimalFormat("'^ _ ^'0.00").format(pi));//^ _ ^4.57
//使用'本身作为前缀或后缀
System.out.println(new DecimalFormat("''0.00").format(pi));//'4.57
```

## 5. 四舍五入
   说的就是我们数学上常说的四舍五入的问题。
   DecimalFormat 提供 RoundingMode 中定义的舍入模式进行格式化。默认情况下，它使用 RoundingMode.HALF_EVEN。

## 6. 同步
   DecimalFormat 通常不是同步的。建议为每个线程创建独立的格式实例。如果多个线程同时访问某个格式，则必须保持外部同步。

## 7. 特殊值
   NaN 被格式化为一个字符串，通常具有单个字符 \uFFFD。此字符串由 DecimalFormatSymbols 对象所确定。这是唯一不使用前缀和后缀的值。

无穷大的值被格式化为一个字符串，通常具有单个字符 \u221E，具有正数或负数前缀和后缀。无穷大值的字符串由 DecimalFormatSymbols 对象所确定。

将负零（"-0"）解析为

如果 isParseBigDecimal() 为 true，则为 BigDecimal(0)，
如果 isParseBigDecimal() 为 false 并且 isParseIntegerOnly() 为 true，则为 Long(0)，
如果 isParseBigDecimal() 和 isParseIntegerOnly() 均为 false，则为 Double(-0.0)。

# 总结
看完我们上面的文章，对于DecimalFormat的使用肯定是没有问题的。最主要的就是我们要多考虑几种情况。上面如果有不对的地方请大家留言，让我们继续前进。

[原文地址](https://www.jianshu.com/p/b3699d73142e)