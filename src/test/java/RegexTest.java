import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则需要转义字符：'$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|'
 * 异常现象： java.util.regex.PatternSyntaxException: Dangling meta. character '*' near index 0
 * 解决方法： 对特殊字符加\\转义即可。
 * <p>
 * * 0次或多次
 * + 1次或多次
 * ？0次或1次
 * {n} 刚好 n 次
 * {n,m} 从 n 到 m 次
 * <p>
 * \b 元字符是用来说明匹配单词的边界，它可以是空格或任何一种不同的标点符号(包括逗号，句号等)。
 * \d [0-9]     匹配一个数字
 * \D [^0-9]    非数字
 * \w [a-zA-Z0-9]   可以匹配一个字母或数字
 * \W [^a-zA-Z0-9]  不是字母或数字
 * \s [\t\n\r\f]    匹配一个空格（也包括Tab等空白符）
 * \S [^\t\n\r\f]   非空格
 * <p>
 * java 正则中 使用双反斜杠(\\)表示转义字符反斜杠(\)
 * <p>
 * * @author wuming
 * Created on 2017/9/27 13:59
 */
public class RegexTest {

    /**
     * 最简单的正则表达式
     * 123456
     */
    @Test
    public void simpleRegTest() {
        String inputValue = "123456777";
        Pattern pattern = Pattern.compile("123456");
        Matcher matcher = pattern.matcher(inputValue);
        System.out.println(matcher.matches());
    }

    /**
     * 最简单的手机号码验证正则表达式
     * * 1\d{10}
     */
    @Test
    public void simpleRegTest2() {
        String mobile = "1312393668";
        Pattern pattern = Pattern.compile("1\\d{10}");
        Matcher matcher = pattern.matcher(mobile);
        System.out.println(matcher.matches());
    }

    /**
     * 简单的正则表达式
     * hi
     */
    @Test
    public void simpleRegTest3() {
        String mobile = "hi";
        Pattern pattern = Pattern.compile("hi");
        Matcher matcher = pattern.matcher(mobile);
        System.out.println(matcher.matches());
    }

    @Test
    public void simpleRegTest4() {
        String mobile = "10000";
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(mobile);
        System.out.println(matcher.matches());
    }

    /**
     * 匹配中汉字
     */
    @Test
    public void StringRegTest() {
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = pattern.matcher("a");
        System.out.println(matcher.matches());
    }


    /*******************小试身手**********************/

    /**
     * 手机号码正则验证优化
     * 正则表达式验证手机号码是否有效
     */
    @Test
    public void mobileRegTest() {
        Pattern pattern = Pattern.compile("1[345678]\\d{9}");
//        Pattern pattern = Pattern.compile("1\\d{10}");
        Matcher matcher = pattern.matcher("13123936686");
        System.out.println(matcher.matches());
    }

    @Test
    public void pddRefundTest() {
        String content = "退货退款详情寄回商品商家收货退款成功?商家处理请寄回商品请在20天23小时内填写快递单号官方补贴退货夫本单免费退1小时上门丢损赔偿我要寄回，" +
                "运费全免自己联系快递寄出，需填写快递单号请先垫付运费，退款成功后预计补贴给您6.5元退货地址：蛮极0571-1234567复制浙江省金华市义乌市填写快递单号退货包运费保障中运费保障查看全部退货遇到问题撤销售后";
        content = "公口3：52S小：退货退款详情商家收货退款成功商家处理寄回商品?请寄回商品请在6天23小时内填写快递单号官方补贴退货E士夫+免填单号1小时上门丢损赔偿先寄后付0元退回，自己联系快递寄出，需填写快递单号请不要邮寄到付复退货地址：昊多多19179460353江西省抚州市南城县第三工业园区麻姑山天然矿泉水厂里面填写快递单号查看全部退货遇到问题问：如何寄回商品、找谁寄快递？撤销售后";
        content = "8:150080日D1退货退款详情?商家处理寄回商品商家收货退款成功请寄回商品请在6天23小时内填写快递单号官方补贴退货比自己联系快递便宜4.5元田丢损赔偿免填单号2小时上门我要退回，" +
                "低至7.5元退货地址：昊多多19179460353（）江西省抚州市南城县第三工业园区麻姑山天然矿泉水厂里面自己联系快递寄出，去填单号退货遇到问题查看全部问：#如何寄回商品、找谁寄快递？问：什么是退货快递单号，怎么上传？撤销售后";
        content = "23:121售后详情核通过，请寄回商品1请在2023-04-3023:11:50前上传物流单号您的售后申请已通过，请尽快寄出。寄回商品官方推荐心安心又方便键寄出免填单号和地址0?" +
                "百重免费寄上门取件-填写物流单号自行寄回运费最高只补贴6元小郑13643021283广东省汕头市潮阳区谷饶镇茂二工业区茂兴布仓氧心公司(到付件拒收，温馨提示：包裹需写纸条备注下产品原因）8892758.【超值2件装】商品：【每件不到1两“轻而亦举”重！】KISSY氧心薄如..撤销申请";
        content = "拼多多17:43D69三1退货退款详情请在6天23小时内填写快递单号官方补贴退货本单免费退丢损赔偿2小时上门我要寄回，运费全免自己联系快递寄出，需填写快递单号请先垫付运费，退款成功后预计补贴给您9.8元" +
                "退货地址：倪童桐【EL】【）13534535555广东省揭阳市揭东区广东省揭阳市揭东区新享镇六乡路美宜佳对面牛圩商业街西一巷6号铺倪童桐13534535555填写快递单号" +
                "退货包运费保障中运费保障查看全部退货遇到问题如何寄回商品、找谁寄快递？问：撤销售后";
        content = "拼多多17:43D69三1退货退款详情请在6天23小时内填写快递单号官方补贴退货本单免费退丢损赔偿2小时上门我要寄回，运费全免自己联系快递寄出，需填写快递单号请先垫付运费，退款成功后预计补贴给您9.8元" +
                "退货地址：倪童桐【EL】【）13534535555广东省揭阳市揭东区广东省揭阳市揭东区新享镇六乡路美宜佳对面牛圩商业街西一巷6号铺倪童桐13534535555填写快递单号" +
                "退货包运费保障中运费保障查看全部退货遇到问题如何寄回商品、找谁寄快递？问：撤销售后";
        content = "下午1:36100%退货退款详情寄回商品退款成功②商家处理商家收货请寄回商品请在6天04小时内填写快递单号官方补贴退货大*本单免费退2小时上门丢损赔偿我要寄回，" +
                "运费全免张超15505222135退货地址：/江苏省宿迁市宿城区龙河镇罗圩街道代理点自己联系快递寄出，去填单号" +
                "退货包运费保障中运费保障退货遇到问题查看全部如何寄回商品、找谁寄快递？问：问：什么是退货快递单号，怎么上传？撤销售后";
        content = "O国*HC2:16°B8S退回给商家免费退·极速退款·全天随时退丢损赔偿尊享：·杨刚17764063522消费者湖北省武汉市江岸区后湖五路和兴业南路交汇二七新江岸生活广场HI栋2单元2302" +
                "马乐园15562173700商家山东省临沂市河东区凤凰岭驻地退件方式去快递柜上门取件去服务点二七新江岸生活广场菜鸟驿站服务点二七新江岸生活广场菜鸟驿站离最近o！" +
                "距您步行265米优惠退货包运费-8元预估费用¥0￥8不锈钢双节棍防身实战儿童跆拳道李小龙.平头款2.2直径260克无声（多多），双截棍裸棍立即下单即表示您已阅读并同意《拼多多退货服务协议》立即下单";
//        content = "下午1:36100%退货退款详情寄回商品退款成功②商家处理商家收货请寄回商品请在6天04小时内填写快递单号官方补贴退货大*本单免费退2小时上门丢损赔偿我要寄回，" +
//                "运费全免张超15505222135退货地址：/江苏省宿迁市宿城区龙河镇罗圩街道代理点自己联系快递寄出，去填单号退货包运费保障中运费保障退货遇到问题查看全部如何寄回商品、" +
//                "找谁寄快递？问：问：什么是退货快递单号，怎么上传？撤销售后";
        content = "为周兰眠晚上9:02|41.IK/s@退货退款详情房回商品商家处理商家收货退款成功e请旧商品请布6天23小时内填写快递单号官为补贴退货比自乙联系快递便宜4元日天丢损偿免填单号" +
                "2小上门钢回低至8元我要?[退货地址：陈露13886134632湖北省省直辖县被行政区划天门市小板镇方寺村自己联系快递出，玄填单号查看金部退货遇到问题如何回商品、" +
                "问：找谁快递！元什么是退货快递单号，怎么上传？间：退货须知请确保商品不影响二水销售（质量问题除外）撤销售后";

        Pattern pattern = null;
        // 姓名
        pattern = Pattern.compile(".*退货地址：(.*?)(1[3-9]\\d{9}|\\d{3,4}[ |-]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4}).*");
//        pattern = Pattern.compile("([\\u4e00-\\u9fa5]{2,8})(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(?=[商家|退货地址：])");
//        pattern = Pattern.compile("(.{2,8})(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(.*?)(?=[退件方式|填写快递单号|自己联系快递寄出])");
        pattern = Pattern.compile("(1[3-9]\\d{9}|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*?([\\u4e00-\\u9fa5]{2,8})(1[3-9]\\d{9}|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*?(?=(退件方式|上门取件|去快递柜|去服务点))");

        // 提取联系电话
//        pattern = Pattern.compile(".*退货地址：.*?(1[3-9]\\d{9}|\\d{3,4}[ |-]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4}).*");
//        pattern = Pattern.compile("(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(?=退货地址：)");
//        pattern = Pattern.compile("(1[3-9]\\d{9}|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(.*)(?=自己联系快递寄出)");
//        pattern = Pattern.compile("(1[3-9]\\d{9}|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*?(1[3-9]\\d{9}|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*?(?=(退件方式|上门取件|去快递柜|去服务点))");

        // 提取地址
//        pattern = Pattern.compile(".*[商家|退货地址：](.*?)(?=[退件方式|填写快递单号|自己联系快递寄出])");
//        pattern = Pattern.compile(".*(退货地址|收件人)：?(.*?)(1[3-9]\\d{9}|\\d{3,4}[ -]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4}).*");
//        pattern = Pattern.compile(".*(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})([\\u4e00-\\u9fa5]{15,25})");
        pattern = Pattern.compile("(1[3-9]\\d{9}|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*?(1[3-9]\\d{9}|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(.*?)(?=(退件方式|上门取件|去快递柜|去服务点))");
        pattern = Pattern.compile(".*退货地址：.*?(1[3-9]\\d{9}|\\d{3,4}[ -]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4})?(.*?)(填写快递单号|自己联系快递).*");
        Matcher matcher = pattern.matcher(content);
        System.out.println("-----------");
        if (matcher.find()) {
            System.out.println("++++++++++++++++" + matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            System.out.println(matcher.group(3));
        }
    }

    @Test
    public void pddExchangeTest() {
        String content = "";
//        content = "讲多多11:5984G换货详情寄回商品确认收货商家处理?商家发货请寄回商品请在6天06小时内填写快递单号官方补贴退货A免填单号2小时上门丢损赔偿先寄后付0元退回，" +
//                "需填写快递单号自己联系快递寄出，请不要邮寄到付退货地址：EFOLONG旗舰店鑫荣轻奢二栋13413934747广东省揭阳市揭东区锡场镇东围鑫荣轻奢T栋）填写快递单号撤销售";

        Pattern pattern = null;
        // 姓名
        pattern = Pattern.compile(".*退货地址：(.*?)(1[3-9]\\d{9}|\\d{3,4}[ |-]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4}).*");

        // 提取联系电话
//        pattern = Pattern.compile(".*退货地址：.*?(1[3-9]\\d{9}|\\d{3,4}[ |-]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4}).*");

        // 提取地址
        pattern = Pattern.compile(".*退货地址：.*?(1[3-9]\\d{9}|\\d{3,4}[ -]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4})(.*?)(填写快递单号|自己联系快递寄出).*");
        Matcher matcher = pattern.matcher(content);
        System.out.println("-----------");
        if (matcher.find()) {
            System.out.println("++++++++++++++++" + matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            System.out.println(matcher.group(3));
        }
    }

    @Test
    public void dyRefundTest() {
        String content = "OQ*0:10:02周翁三列3>退货详情我的订单1待退货OA提交申请商家审核退回商品售后完成退款完成因您的信用良好，已享受闪电退货，请确保商品不影响二并在7天 " +
                "内填写物流信息。次销售后将其退回指定地址，寄件详情麻素店 13074020118云C安徽省安庆市岳西县莲云乡连云开发区珠屋路8好肖梦服饰有限公司二楼免填地址·丢损必赔我已寄出我要寄件填写物流单号支持上门取件/快递柜寄件物流客服春季新款纯棉衬衫女圆领设计感复古长袖棉麻衬衣亚麻职业时尚上衣白色/M（105-120)数量：1申请金额?39.00合计退款￥39.00撤销申请";
        // 平台识别
//        Pattern pattern = Pattern.compile(".*退货详情.*我的订单.*");
        // 提取地址
//        Pattern pattern = Pattern.compile("([\\u4e00-\\u9fa5]{3})(?=1[3-9]\\d{9}|\\d{3,4}[ -]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4})");
        // 提取联系电话
//        Pattern pattern = Pattern.compile("(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})");
        // 提取地址
        Pattern pattern = Pattern.compile("(1[3-9]\\d{9}|\\d{3,4}[ |-]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4})(.*?)(上门取件|我要寄件|自行寄回|我已寄出)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            System.out.println("++++++++++++++++" + matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        System.out.println("-----------");
    }

    @Test
    public void dyExchangeTest() {
        String content = "周翁兰列OQ*0:9:03日甸A换货详情请寄回商品①还剩6天23时59分商家已同意换货，请填写真实快递单号或使用上门取件、" +
                "快递柜寄件服务退回商品商家审核寄回商品换货完成商家发货1牛科锋021-60336477商家地址口上海市上海市松江区洞泾镇洞薛路885号普洛斯洞泾物流园P2号库华塘大昌上门取件/去快递柜寄件" +
                "首重免费寄I我要寄件免填地址·丢损必赔我已寄出填写单号填写物流单号保障服务运费险保障中退换货自动理赔协商记录换货信息联系商家【官方正品】贝德玛控油卸妆水500ml21享4D500ml平台介入取消换货修改换货";
        content = "周翁兰列OQ*0:9:03日甸A换货详情请寄回商品①还剩6天23时59分商家已同意换货，请填写真实快递单号或使用上门取件、快递柜寄件服务退回商品商家审核寄回商品换货完成商家发货1牛科锋021-60336477商家地址口上海市上海市松江区洞泾镇洞薛路885号普洛斯洞泾物流园P2号库华塘大昌上门取件/去快递柜寄件首重免费寄I我要寄件免填地址·丢损必赔我已寄出填写单号填写物流单号保障服务运费险保障中退换货自动理赔协商记录换货信息联系商家【官方正品】贝德玛控油卸妆水500ml21享4D500ml平台介入取消换货修改换货";
        // 平台识别
//        Pattern pattern = Pattern.compile(".*换货详情.*取消换货.*修改换货.*");
        // 提取地址
//        Pattern pattern = Pattern.compile("([\\u4e00-\\u9fa5]{3})(?=1[3-9]\\d{9}|\\d{3,4}[ -]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4})");
        // 提取联系电话
//        Pattern pattern = Pattern.compile("(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})");
        // 提取地址
        Pattern pattern = Pattern.compile("(1[3-9]\\d{9}|\\d{3,4}[ |-]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4})(.*?)(上门取件|我要寄件)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            System.out.println("++++++++++++++++" + matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        System.out.println("-----------");
    }

    /**
     * 快手退货截图识别
     * 图片位于 td-biz-ticket 桶下的 other_refund_test 文件夹
     *
     * 自行寄回 = other_refund_test/ks_self.png
     * 上门取件 = other_refund_test/ks_got.png
     */
    @Test
    public void ksRefundTest() {
        // 自行寄回
        String content = "X2919:09司川HDO专售后详情6处理申请寄回商品卖家收货退款成功请寄回商品请您在9天23小时59分内寄回商品并上传快递单号，" +
                "逾期未处理退货申请将自动关闭自行寄回上门取件官方推荐自行去附近服务店寄件完美日记19874269832收件地址广东省广州市从化区鳌头镇城鳌大道口万维物流园二层快递单号CC" +
                "请填写快递单号快递公司请选择快递公司2确认提交尊享服务生效中>退货补运费预计补贴￥10协商历史1取消申请";
        // 上门取件
        content = "X2919:08司川HDO专售后详情6处理申请寄回商品卖家收货退款成功请寄回商品请您在9天23小时59分内寄回商品并上传快递单号，逾期未处理退货申请将自动关闭上门取件官方推荐" +
                "自行寄回官方运费补贴，比自行寄回更便宜郑琪琪13554605497寄件地址浙江省杭州市余杭区仓前街道万通中心1幢1007完美日记19874269832收件地址广东省广州市从化区鳌头镇城鳌大道口万维物流园二层上门时间请选择" +
                "上门取件时间?O起?7.8预估费用①按1kg内物品预估，1kg内运费7.8元，续重3.6元/S已阅读并同意《上门取件服务协议》确认提交取消申请";
        // 平台识别
        Pattern pattern = null;
//        pattern = Pattern.compile(".*售后详情.*取消申请.*");

        // 提取姓名（自行寄回）
        pattern = Pattern.compile(".*自行去附近服务店寄件(.*?)(1[3-9]\\d{9}|\\d{3,4}[ -]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4})");
        // 提取姓名（上门取件）
        pattern = Pattern.compile("(.{2,8})(1[3-9]\\d{9}|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(?=收件地址)");

        // 提取联系电话（上门、自行寄回）
//        pattern = Pattern.compile("(1[3-9]\\d{9}|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(?=收件地址)");

        // 提取地址（上门、自行寄回）
//        pattern = Pattern.compile(".*收件地址(.*?)(快递单号|上门时间)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            System.out.println("++++++++++++++++" + matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        System.out.println("-----------");
    }

    /**
     * 京东退货截图识别
     * 图片位于 td-biz-ticket 桶下的 other_refund_test 文件夹
     *
     * 自营 = other_refund_test/jd_offcial.png
     * 第三方店铺 = other_refund_test/ks_got.png
     */
    @Test
    public void jdRefundTest() {
        // 自营
        String content = "中日318:40创固4.l8服务单详情本次售后服务将由京东为您提供请您发货Acn完成提交申请京东审核京东收货京东处理请您尽快发货并填写运单号，" +
                "如未及时填写查看更多服务将在6天23小时59分38秒后关闭请您在7天内寄回全套商品，并在服务单中填写快递信息商品回寄信息收起寄回地址江苏苏州市昆山市花桥经济开发区花集路632号（C仓逆向处置中心）收件人汤佳东联系电话0512-83662107猜你想问我" +
                "能取消服务单吗？商品退回运费谁承担？怎么没有人上门取件商品需要寄回到哪里？退货可以改成换货吗？退货可以改成维修吗理肤泉B5修复霜40ml*2支套装补水保湿舒缓泛红维稳乳液面霜护肤品n0填写发货单取消申请";
        // 平台识别
        Pattern pattern = null;
        pattern = Pattern.compile(".*服务单详情.*|.*填写发货单.*");
        // 提取姓名
        // 自营/第三方
//        pattern = Pattern.compile(".*收件人(.*?)联系电话.*");
        // 提取联系电话
        pattern = Pattern.compile(".*联系电话(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*");
        // 提取地址
//        pattern = Pattern.compile(".*寄回地址(.*?)收件人");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            System.out.println("++++++++++++++++" + matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        System.out.println("-----------");
    }

    /**
     * 小红书退货截图识别
     * 图片位于 td-biz-ticket 桶下的 other_refund_test 文件夹
     *
     * 自行寄回 = other_refund_test/xhs_self.png
     * 上门取件 = other_refund_test/xhs_got.png
     */
    @Test
    public void xhsRefundTest() {
        // 自行寄回
        String content = "OQ*0510:20周蛤列恒支退货详情入等待退货商品寄回请将商品退回商家指定退货地址，并在6天23小时内填写退回物流信息，超时未寄回将自动关闭此次" +
                "申请。。汐阿姨工作室的店联系卖家去咨询寄回方式自主寄回物流公司请选择物流单号请填写快递单号杨思萍15355332671浙江省金华市义乌市江东街道塔下洲A3区35栋2楼退货商品网红撞色方领美背打底吊带背心女外，" +
                "￥29.9K退货售后类型申请金额￥29.90退货原因其他创建时间2023-03-1722:19:031取消售后平台介入提交";
        // 上门取件
        content = "OQ*0510:20周喆列恒支退货详情入等待退货商品寄回请将商品退回商家指定退货地址，并在6天23小时内填写退回物流信息，超时未寄回将自动关闭此次申请。》" +
                "汐阿姨工作室的店联系卖家去咨询寄回方式上门取件取件时间预估运费￥7首重1kg内运费7元，续重2元/kg曾敏13554605497浙江省杭州市余杭区仓前街道仓前街道景兴路" +
                "万通中心1幢1007蛮极杨思萍15355332671收浙江省金华市义乌市江东街道塔下洲A3区35栋2楼退货商品网红撞色方领美背打底吊带背心女外，以￥29.9退货售后类型申请金额¥29.90已阅读并同意《上门取取消售后提交件用户服务协议》";
        // 平台识别
        Pattern pattern = null;
        pattern = Pattern.compile(".*退货详情.*|.*取消售后.*");
        // 提取姓名
        // 自行寄回
        pattern = Pattern.compile(".*请填写快递单号([\\u4e00-\\u9fa5]*)(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})");
        // 上门取件
//        pattern = Pattern.compile("([\\u4e00-\\u9fa5]*)(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(?=收)");
        pattern = Pattern.compile(".*([\\u4e00-\\u9fa5]{2,5})(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*(?=退货商品)");

        // 提取联系电话
        // 自行寄回
//        pattern = Pattern.compile(".*请填写快递单号.*?(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})");
        // 上门取件
        pattern = Pattern.compile(".*([\\u4e00-\\u9fa5]{2,5})(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*(?=退货商品)");

        // 提取地址
        // 自行寄回
//        pattern = Pattern.compile(".*请填写快递单号.*?(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(.*?)退货商品");
        // 上门取件
        pattern = Pattern.compile(".*(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(.*?)(?=退货商品)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            System.out.println("++++++++++++++++" + matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        System.out.println("-----------");
    }

    /**
     * 小红书退货截图识别
     * 图片位于 td-biz-ticket 桶下的 other_refund_test 文件夹
     *
     * 自行寄回 = other_refund_test/xhs_self.png
     * 上门取件 = other_refund_test/xhs_got.png
     */
    @Test
    public void xhsExchangeTest() {
        // 自行寄回
        String content = "回贝洛319:10周金国口一>换货详情等待换货商品寄回请将商品退回商家指定退货地址，并在6天23小时内填写退回物流信息，超时未寄回将自动关闭此次申请。卖家留言亲，您好，在退换前请保证衣服没有影响二次销售温馨提示：" +
                "（即保证吊牌完好，衣服无损坏）；个个订单多个商品的请个包裹退针距服饰的店联系卖家去咨询金寄回方式自主寄回物流公司请选择物流单号请填写快递单号吴伟18814112918浙江省金华市义乌市后宅街道城北路K39科拉工业园1号楼2楼退货处（拒收任何快递到付件）保障中退货包运费?换货商品other高街运动卫裤女春夏垂感拖地。以￥69.9平台介入取消售后提交";
        // 上门取件
        content = "回食洛319:11周蛤兰国口一>换货详情等待换货商品寄回请将商品退回商家指定退货地址，并在6天23小时内填写退回物流信息，超时未寄回将自动关闭此次申请。卖家留言亲，您好，在退换前请保证衣服没有影响二次销售温馨提示：（即保证吊牌完好，衣服无损坏）；个个订单多个商品的请个包裹退针距服饰的店联系卖家去咨询剧寄回方式上门取件取件时间预估运费¥0退货包运费已补贴7元曾敏13554605497浙江省杭州市余杭区仓前街道仓前街道景兴路万通中心1幢1007吴伟18814112918浙江省金华市义乌市后宅街道城北路K39科拉工业园1号楼2楼退货处（拒收任何快递到付件）退货包运费保障中?换货商品已阅读并同意《上门取取消售后提交件用户服务协议》";
        // 平台识别
        Pattern pattern = null;
        pattern = Pattern.compile(".*退货详情.*取消售后.*");
        // 提取姓名
        // 自行寄回
        pattern = Pattern.compile(".*请填写快递单号([\\u4e00-\\u9fa5]*)(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})");
        // 上门取件
        pattern = Pattern.compile(".*([\\u4e00-\\u9fa5]{2,5})(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*(?=(换货商品|退货包运费))");

        // 提取联系电话
        // 自行寄回
//        pattern = Pattern.compile(".*请填写快递单号.*?(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})");
        // 上门取件
        pattern = Pattern.compile(".*([\\u4e00-\\u9fa5]{2,5})(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*(?=(换货商品|退货包运费))");

        // 提取地址
        // 自行寄回
//        pattern = Pattern.compile(".*请填写快递单号.*?(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(.*?)(换货商品|退货包运费)");
        // 上门取件
        pattern = Pattern.compile(".*(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(.*?)(?=(换货商品|退货包运费))");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            System.out.println("++++++++++++++++" + matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        System.out.println("-----------");
    }
    /**
     * 小红书退货截图识别
     * 图片位于 td-biz-ticket 桶下的 other_refund_test 文件夹
     *
     * 自行寄回 = other_refund_test/xhs_self.png
     * 上门取件 = other_refund_test/xhs_got.png
     */
    @Test
    public void refund1688Test() {
        // 自行寄回
        String content = "上午11:42无服务D?退款详情您必须在6天23小时2分秒内完成退货，在此页面写运单号，边期系统自动关闭退款。退款协议达成，" +
                "等待买家退货您还可以主动联系卖家b测试账号001，说明退货情况并保留退货资料，请勿私自到付/平邮，否则卖家有权拒收！如双方未能协商一致，" +
                "您可以在2天23小时28分44秒后投诉请寄回商品官方退货丢损必自动回传运单免填地址四④A立即预约寄件1688上门取件需手动回填单号，" +
                "无法享受丢损赔付的官方保障退货收件人：菜鸟退货收件地址联系电话：13955555555退货地址：浙江省杭州市西湖区蒋村街道中节能西溪首座填写快递单号留言/凭证撤销退款";

        // 平台识别
        Pattern pattern = null;
        pattern = Pattern.compile(".*退货详情.*取消售后.*");
        // 提取姓名
        // 自行寄回
        pattern = Pattern.compile(".*请填写快递单号([\\u4e00-\\u9fa5]*)(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})");
        // 上门取件
        pattern = Pattern.compile(".*([\\u4e00-\\u9fa5]{2,5})(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*(?=(换货商品|退货包运费))");

        // 提取联系电话
        // 自行寄回
//        pattern = Pattern.compile(".*请填写快递单号.*?(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})");
        // 上门取件
        pattern = Pattern.compile(".*([\\u4e00-\\u9fa5]{2,5})(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8}).*(?=(换货商品|退货包运费))");

        // 提取地址
        // 自行寄回
//        pattern = Pattern.compile(".*请填写快递单号.*?(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(.*?)(换货商品|退货包运费)");
        // 上门取件
        pattern = Pattern.compile(".*(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(.*?)(?=(换货商品|退货包运费))");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            System.out.println("++++++++++++++++" + matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        System.out.println("-----------");
    }

    @Test
    public void otherRefundTest() {
        // 自行寄回
        String content = "同器气网19:06の阳b退款详情售后申请已通过，请及时退货倒计时：06天23小时57分1填写物流单号协商历史待买家退货商家已同意申请，2023-05-0419:02商家退货信息华生收件人19802739900浙江省杭州市临平区乔司街道鑫业路2号高明收货地址中心B座10楼电梯左侧1001（寄回后务必上传退货快递单号和留个纸条下单的手机号码）售后信息【墨笙歌高定】羿林|60姆米八?1499.00年老料红云纱直筒阔腿裤-00507以L红色;L【115-125】?1499.00退款金额撤销申请";

        // 平台识别
        Pattern pattern = null;
        // 提取姓名
        // 自行寄回
        pattern = Pattern.compile(".*(退货地址|收货地址|收件人|收货人|寄回地址)：?(.*?)(1[3-9]\\d{9}|\\d{3,4}[ -]?\\d{7,8}|\\d{3}-\\d{3}-\\d{4}).*");

        // 提取联系电话
        // 自行寄回
//        pattern = Pattern.compile(".*请填写快递单号.*?(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})");

        // 提取地址
        // 自行寄回
//        pattern = Pattern.compile(".*请填写快递单号.*?(1[3-9]\\d{9}$|\\d{3}-\\d{3}-\\d{4}|\\d{3,4}[ -]?\\d{7,8})(.*?)(换货商品|退货包运费)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            System.out.println("++++++++++++++++" + matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            System.out.println(matcher.group(3));
        }
        System.out.println("-----------");
    }


    /**
     * 匹配固话
     * 010 12345678
     * 05711234567
     * 0571-1234567
     * 0571 1234567
     * <p>
     * 400-221-5383
     */
    @Test
    public void phoneRegTest() {
        String phone = "05711234567";
//        phone = "0571-1234567";
//        phone = "0571 1234567";
//        Pattern pattern = Pattern.compile("\\d{3,4}( |-)?\\d{7,8}");
        Pattern pattern = Pattern.compile("\\d{3,4}[ |-]?\\d{7,8}");
        Matcher matcher = pattern.matcher(phone);
        System.out.println(matcher.matches());

    }

    /**
     * 400-221-5383
     */
    @Test
    public void phoneRegTest2() {
        String phone = "500-221-5383";
        Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
        Matcher matcher = pattern.matcher(phone);
        System.out.println(matcher.matches());
    }

    /**
     * iPv4的ip地址都是（1~255）.（0~255）.（0~255）.（0~255）的格式
     * 112.124.33.28
     * 127.0.0.1
     * 255.255.255.255
     * 192.168.1.1
     * 192.168.123.222
     */
    @Test
    public void ipRegTest() {
        String ip = "127.0.0.1";
        String reg = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(ip);
        System.out.println(matcher.matches());
    }

    /**************************字符串分组及获取*************************/

    /**
     * 每行以字母 j 开头，不区分大小写
     * Pattern 知识扩展
     * 注意:只有当匹配操作成功,才可以使用start(),end(),group()三个方法,否则会抛出java.lang.IllegalStateException,
     * 也就是当matches(),lookingAt(),find()其中任意一个方法返回true时,才可以使用.
     */
    @Test
    public void groupTest() {
        String a = "java has regex\nJava has regex\n" +
                "JAVA has pretty good regular expressions\n" +
                "Regular expressions are in Java";
        Pattern p = Pattern.compile("^java", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher m = p.matcher(a);
        while (m.find()) {
            System.out.println(m.group()); // // 分组总是表示符合pattern的字符串本身
        }
    }

    /**
     * 从一个给定的字符串中找到数字串
     * group 获得字符串
     */
    @Test
    public void groupTest1() {
        // 按指定模式在字符串查找
        String line = "This order was placed for QT3000! OK?";
        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile("(\\D*)(\\d+)(.*)");
        // 现在创建 matcher 对象
        Matcher matcher = pattern.matcher(line);
        System.out.println("groupCount: " + matcher.groupCount());
        if (matcher.find()) {
            System.out.println("group(0) value: " + matcher.group(0)); // 分组0总是表示符合pattern的字符串本身
            System.out.println("group(1) value: " + matcher.group(1)); // 第一个分组 (\D*) 匹配到的内容
            System.out.println("group(2) value: " + matcher.group(2)); // 第二个分组 (\d+) 匹配到的内容
            System.out.println("group(3) value: " + matcher.group(3)); // 第三个分组 (.*) 匹配到的内容
        } else {
            System.out.println("NO MATCH");
        }
    }

    /**
     * 给定的字符串中截取自己需要的字符串
     * 通过 group 获得字符串 oBHgGLHYvnMLxOChZeYYCENgPrZcCvxH
     */
    @Test
    public void groupTest2() {
        // 按指定模式在字符串查找
        String line = "NTES_SESS=VVEWO8vLULj7glrgYSnLplkBeEp1r_wzVi.PB7mWIFP2pyFSNmCkhA1yQ2SQCaUFZzvs4KXm_JJ3yB4aVSdJ7n" +
                "yB01SiIgMgN.9mkZ822T5B496xpvGvhIuMYZ5E24XXjQzRDRk1b3uQh2ZhXyG7HGEnf8flQVtYM61eg70EHL3oU77oRz3r1jfAW; " +
                "S_INFO=1502966524|0|2&90##|youngqiankun#kunkun0123456789#kunkun18739933735; P_INFO=youngqiankun@163.com" +
                "|1502966524|1|mail163|00&13|zhj&1502958141&mail163#zhj&330100#10#0|158155&0|mail163|youngqiankun@163.com; " +
                "NTES_PASSPORT=9Sxem5c_YQV5IpIqFp_nd8D9WBKtsX5raEUe8GljJZ6oVUXIMd7AqEwUifIi7.hXozh45181MS80NZSbm8VM." +
                "xZ8LUc3h7QVmudfLvBP36b.yzHMOjJtSjZhKixslRUQL; starttime=; Coremail.sid=vBQpwSDUicfESZzdJKUUYuzQkgtdDdPU; " +
                "mail_style=js6; mail_uid=youngqiankun@163.com; mail_host=mail.163.com; JSESSIONID=93784AEC8E5C43771ACD2EAB2E298A6B; " +
                "Province=0571; City=0571; mail_upx=t5hz.mail.163.com|t6hz.mail.163.com|t7hz.mail.163.com|t8hz.mail.163.com|" +
                "t10hz.mail.163.com|t11hz.mail.163.com|t12hz.mail.163.com|t13hz.mail.163.com|t1hz.mail.163.com|t2hz.mail.163.com|" +
                "t3hz.mail.163.com|t4hz.mail.163.com|t1bj.mail.163.com|t2bj.mail.163.com|t3bj.mail.163.com|t4bj.mail.163.com; " +
                "mail_upx_nf=; mail_idc=; Coremail=1502972936415%oBHgGLHYvnMLxOChZeYYCENgPrZcCvxH%g7a72.mail.163.com; " +
                "MAIL_MISC=youngqiankun#kunkun0123456789#kunkun18739933735; cm_last_info=dT15b3VuZ3FpYW5rdW4lNDAxNjMuY2" +
                "9tJmQ9aHR0cCUzQSUyRiUyRm1haWwuMTYzLmNvbSUyRm0lMkZtYWluLmpzcCUzRnNpZCUzRG9CSGdHTEhZdm5NTHhPQ2haZVlZQ0VOZ1By" +
                "WmNDdnhIJnM9b0JIZ0dMSFl2bk1MeE9DaFplWVlDRU5nUHJaY0N2eEgmaD1odHRwJTNBJTJGJTJGbWFpbC4xNjMuY29tJTJGbSUyRm1haW4" +
                "uanNwJTNGc2lkJTNEb0JIZ0dMSFl2bk1MeE9DaFplWVlDRU5nUHJaY0N2eEgmdz1tYWlsLjE2My5jb20mbD0wJnQ9MTE=; MAIL_SESS=" +
                "VVEWO8vLULj7glrgYSnLplkBeEp1r_wzVi.PB7mWIFP2pyFSNmCkhA1yQ2SQCaUFZzvs4KXm_JJ3yB4aVSdJ7nyB01SiIgMgN.9mkZ8" +
                "22T5B496xpvGvhIuMYZ5E24XXjQzRDRk1b3uQh2ZhXyG7HGEnf8flQVtYM61eg70EHL3oU77oRz3r1jfAW; MAIL_SINFO=1502966524" +
                "|0|2&90##|youngqiankun#kunkun0123456789#kunkun18739933735; MAIL_PINFO=youngqiankun@163.com|1502966524|1" +
                "|mail163|00&13|zhj&1502958141&mail163#zhj&330100#10#0|158155&0|mail163|youngqiankun@163.com; secu_info=1;" +
                " mail_entry_sess=99e3f971ba81b90cb605f6cdacf70461c67a4f04d6b0caf8d9389be487e9591ef648e37f524d31422cf3" +
                "dcfc33f30ad159fc7d9c68e4bffa2a2d0284f5c841c33adc68e56eab6f7b5a9366cbe86bf0185e9626fad202198ab355c8a95b09" +
                "ee93166472e6b00dadc5562ba2d7c2dae25e68c6aea266839521d2d7767b63adb143c1eec199bc96d77661b8f9f013bb57fab9e2" +
                "3b647c711141e034f39569d857925796eda7d1215f97684216ebfb5e47da58b7e82f1508efcb9519373f0411f6a6; locale=";
        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(".*%(.*)%.*");
        // 现在创建 matcher 对象
        Matcher matcher = pattern.matcher(line);
        System.out.println("groupCount: " + matcher.groupCount());
        if (matcher.find()) {
            // System.out.println("group(0) value: " + matcher.group(0)); //分组0总是表示符合pattern的字符串本身
            System.out.println("group(1) value: " + matcher.group(1)); // 第一个分组 (\D*) 匹配到的内容
        } else {
            System.out.println("NO MATCH");
        }

        // 贪婪模式的知识点扩展
        // 默认为贪婪模式
        pattern = Pattern.compile(".*JSESSIONID=(.*?);");
        matcher = pattern.matcher(line);
        if (matcher.find()) {
            System.out.println("groupCount: " + matcher.groupCount());
            System.out.println("group(1) value: " + matcher.group(1));
        }
    }

    /*******************扩展***********************/

    /**
     * 字符串替换
     */
    @Test
    public void replaceTest() {
        String s = "/*! Here's a block of text to use as input to\n" +
                "    the regular expression matcher. Note that we'll\n" +
                "    first extract the block of text by looking for\n" +
                "    the special delimiters, then process the\n" +
                "    extracted block. !*/";
        // Match the specially-commented block of text above:
        Matcher matcher = Pattern.compile("/\\*!(.*)!\\*/", Pattern.DOTALL).matcher(s);
        if (matcher.find()) {
            System.out.println("====================");
            s = matcher.group(1); // Captured by parentheses
        }
        System.out.println("====s: " + s);
        // Replace two or more spaces with a single space:
        s = s.replaceAll(" {2,}", " ");
        System.out.println("----s1: " + s);

        // Replace one or more spaces at the beginning of each line with no spaces. Must enable MULTILINE mode:
        // (?m)是打开多行模式的开关，^是匹配一行的开头
        s = s.replaceAll("(?m)^ +", "");
        System.out.println("----s2: " + s);

    }

}
