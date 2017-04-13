/**
 * 打开控制台并使用下面的命令
 * jjs example.js
 * 运行结果 78952000000000000003.20
 */

var BigDecimal = Java.type('java.math.BigDecimal');

function calculate(amount, percentage) {
    var result = new BigDecimal(amount).multiply(
        new BigDecimal(percentage)).divide(
        new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_EVEN);
    return result.toPlainString();
}
var result = calculate(568000000000000000023, 13.9);

print(result);