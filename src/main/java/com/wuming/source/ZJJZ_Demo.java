package com.wuming.source;

import zjjz_api_gw.ZJJZ_API_GW;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class ZJJZ_Demo {
    public static void main(String[] args) {

        String ServerIPAddress="127.0.0.1";//客户端所在机器地址，请根据实际情况修改
        int ServerPort=7072;

        /*定义报文参数字典*/
        HashMap parmaKeyDict = new HashMap();//用于存放生成向银行请求报文的参数
        HashMap retKeyDict = new HashMap();//用于存放银行发送报文的参数

        int testType=1;//1:演示一般联机接口示例 2：演示6070循环数据输入接口 3：演示支付密码的接口示例

        if(testType==1)
        {

            /**
             * 第一部分：生成发送银行的请求的报文的实例
             *
             */

            /*生成随机数:当前精确到秒的时间再加6位的数字随机序列,用于生成请求流水号*/
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
            String rdNum=df.format(new Date());
            SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String trandate=df2.format(new Date());
            Random random = new Random();
            int ird = random.nextInt(999999);
            String srd= String.format("%06d", ird);
            String thirdLogNo = rdNum + srd;//赋值请求流水号

            /*报文头参数赋值*/
            parmaKeyDict.put("TranFunc", "6000");   //交易码，此处以【6000】接口为例子
            parmaKeyDict.put("Qydm", "3050");       //企业代码
            parmaKeyDict.put("ThirdLogNo", thirdLogNo); //请求流水号，必须20位，平台应自行生成，demo仅作示范

            /*报文体*/
            parmaKeyDict.put("SupAcctId", "11014892686007"); //资金汇总账号
            parmaKeyDict.put("FuncFlag", "1"); //功能标志1：开户
            parmaKeyDict.put("ThirdCustId", "pingan"+srd);      //电商平台会员ID，请自定义
            parmaKeyDict.put("CustProperty", "00");           //会员属性
            parmaKeyDict.put("NickName", "测试君");            //会员昵称
            parmaKeyDict.put("MobilePhone", "18617166125");  //手机号码
            parmaKeyDict.put("Email", "");                   //邮箱
            parmaKeyDict.put("Reserve", "");                 //保留域

            /*获取请求报文*/
            ZJJZ_API_GW msg=new ZJJZ_API_GW();
            String tranMessage=msg.getTranMessage(parmaKeyDict);//调用函数生成报文


            /*输出报文结果*/
            System.out.println("第一部分：生成发送银行的请求的报文的实例");
            System.out.println(tranMessage);
            System.out.println("-------------------------------");

            /**
             * 第二部分：获取银行返回的报文的实例
             *
             */
            /*发送请求报文*/
            msg.SendTranMessage(tranMessage,ServerIPAddress,ServerPort,retKeyDict);

            /*获取银行返回报文*/
            String recvMessage=(String)retKeyDict.get("RecvMessage");//银行返回的报文
            /*输出报文结果*/
            System.out.println("第二部分：获取银行返回的报文");
            System.out.println(recvMessage);
            System.out.println("-------------------------------");

            /**
             * 第三部分：解析银行返回的报文的实例
             *
             */
            retKeyDict= msg.parsingTranMessageString(recvMessage);
            String rspCode=(String)retKeyDict.get("RspCode");//银行返回的应答码
            String rspMsg=(String)retKeyDict.get("RspMsg");//银行返回的应答描述
            String bodyMsg=(String)retKeyDict.get("BodyMsg");
            String custAcctId=(String)retKeyDict.get("CustAcctId");//银行返回的钱包ID

            /*输出报文结果*/
            System.out.println("第三部分：解析银行返回的报文");
            System.out.println("返回应答码：");
            System.out.println(rspCode);
            System.out.println("返回应答码描述：");
            System.out.println(rspMsg);
            System.out.println("返回报文体：");
            System.out.println(bodyMsg);
            System.out.println("返回钱包ID：");
            System.out.println(custAcctId);
            System.out.println("-------------------------------");

        }
        else if(testType==2)
        {

            /**
             * 其它-循环数组：6070循环数组接口输入的实例
             *
             */
            /*定义报文参数字典*/
            HashMap parmaKeyDict2 = new HashMap();//用于存放生成向银行请求报文的参数
            HashMap retKeyDict2 = new HashMap();//用于存放银行发送报文的参数

            /*生成流水号*/
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
            String rdNum=df.format(new Date());
            SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String trandate=df2.format(new Date());
            Random random = new Random();
            int ird2 = random.nextInt(999999);
            String srd2= String.format("%06d", ird2);
            String thirdLogNo2 = rdNum + srd2;//赋值请求流水号

            /*报文头固定参数*/
            parmaKeyDict2.put("TranFunc", "6070");   //交易码，此处以【6070】接口为例子
            parmaKeyDict2.put("Qydm", "3045");       //企业代码
            parmaKeyDict2.put("ThirdLogNo", thirdLogNo2); //请求流水号，

            /*报文体参数*/
            parmaKeyDict2.put("SupAcctId", "11014892664003");
            parmaKeyDict2.put("OutCustAcctId", "3045000000002458");
            parmaKeyDict2.put("OutThirdCustId", "110000001566");
            parmaKeyDict2.put("HandFee", "0.00");
            parmaKeyDict2.put("CcyCode", "RMB");
            parmaKeyDict2.put("ThirdHtId", "2016100006");
            parmaKeyDict2.put("TotalCount", "3");//总条数，即循环次数，这里假定3条

            /*组Array数据内容*/
            String[] InCustAcctId={"3045000000002449","3045000000002468","3045000000002478"};
            String[] InThirdCustId={"110000001509","201606240000000626","201606270000000627"};
            String[] TranAmount={"0.01","0.02","0.03"};

            parmaKeyDict2.put("InCustAcctId", InCustAcctId);
            parmaKeyDict2.put("InThirdCustId", InThirdCustId);
            parmaKeyDict2.put("TranAmount", TranAmount);

            /*获取请求报文*/
            ZJJZ_API_GW msg2=new ZJJZ_API_GW();
            String tranMessage2=msg2.getTranMessage(parmaKeyDict2);//调用函数生成报文

            /*输出报文结果*/
            System.out.println("6070报文请求报文：");
            System.out.println(tranMessage2);
            System.out.println("-------------------------------");

            /*发送请求报文*/
            msg2.SendTranMessage(tranMessage2,ServerIPAddress,ServerPort,retKeyDict2);

            /*获取银行返回报文*/
            String recvMessage2=(String)retKeyDict2.get("RecvMessage");//银行返回的报文

            /*输出报文结果*/
            System.out.println("6070报文返回报文：");
            System.out.println(recvMessage2);
            System.out.println("-------------------------------");

        }
        else
        {
            /**
             * 其它-支付密码：6006验证支付密码接口输入的实例
             *
             */

            /*定义报文参数字典*/
            HashMap parmaKeyDict3 = new HashMap();//用于存放生成向银行请求报文的参数
            HashMap retKeyDict3 = new HashMap();//用于存放银行发送报文的参数

            /*生成流水号*/
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
            String rdNum=df.format(new Date());
            SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String trandate=df2.format(new Date());
            Random random = new Random();
            int ird3 = random.nextInt(999999);
            String srd3= String.format("%06d", ird3);
            String thirdLogNo3 = rdNum + srd3;//赋值请求流水号

            /*报文头固定参数*/
            parmaKeyDict3.put("TranFunc", "6111");   //交易码，此处以【6111】接口为例子
            parmaKeyDict3.put("Qydm", "3113");       //企业代码
            parmaKeyDict3.put("ThirdLogNo", thirdLogNo3); //请求流水号，

            /*报文体参数*/
            parmaKeyDict3.put("SupAcctId", "11014705060007");  //资金汇总账户
            parmaKeyDict3.put("CustAcctId", "3133000000001089");  //钱包ID
            parmaKeyDict3.put("ThirdCustId", "redstar008");       //电商平台会员ID
            parmaKeyDict3.put("CustName", "董路");                //会员名称
            parmaKeyDict3.put("OutAcctId", "621877788999026");    //提现账号
            parmaKeyDict3.put("OutAcctIdName", "董路");           //提现账户户名
            parmaKeyDict3.put("TranAmount", "100");              //提现金额
            parmaKeyDict3.put("HandFee", "0");                //电商平台收取的费用
            parmaKeyDict3.put("CcyCode", "RMB");                 //币种
            parmaKeyDict3.put("SerialNo", "");                   //短信指令号，此处为空
            parmaKeyDict3.put("MessageCode", "");                //短信验证码，此处为空
            parmaKeyDict3.put("Note", "测试交易");                       //提现转账备注
            //parmaKeyDict3.put("Reserve", trandate+thirdLogNo3);    //保留域，填写日期+流水号（需与报文头相同），防止签名被重复利用。
            parmaKeyDict3.put("Reserve", "测试专用");    //保留域，正确应填写日期+流水号，但为方便demo演示，这里送固定值

            /*获取签名源串报文*/
            ZJJZ_API_GW msg3=new ZJJZ_API_GW();
            String orig=msg3.getSignMessage(parmaKeyDict3);//调用函数签名源串

            System.out.println("签名源串：");
            System.out.println(orig);
            System.out.println("-------------------------------");

            //生成签名源串之后，按文档说明发到银行SDK或页面验证密码，获取银行签名，下面为演示假设已获取到签名仅需后续步骤。
            String WebSign="4ca6d3aaa480e4432d244e58e7a05bdba7dd1b442ab769c2078aec57bf10b327053d789da4e8b8e27e59e5878d4efa2f368afa45863b5ed4151e99ba45f1315cc5f096f4e0b2333c789aea2996a74f6320e6e7392bce94d5fd31ba17de159120be7ae8d1695b8cbe109397607a816a540a5c7de20d2598e8941e9b73653b6a30";
            parmaKeyDict3.put("WebSign", WebSign);


            /*获取请求报文*/
            String tranMessage3=msg3.getTranMessage(parmaKeyDict3);//调用函数生成报文

            /*输出报文结果*/
            System.out.println("6111报文请求报文：");
            System.out.println(tranMessage3);
            System.out.println("-------------------------------");

            /*发送请求报文*/
            msg3.SendTranMessage(tranMessage3,ServerIPAddress,ServerPort,retKeyDict3);

            /*获取银行返回报文*/
            String recvMessage3=(String)retKeyDict3.get("RecvMessage");//银行返回的报文

            /*输出报文结果*/
            System.out.println("6111报文返回报文：");
            System.out.println(recvMessage3);
            System.out.println("-------------------------------");

        }

    }

}
