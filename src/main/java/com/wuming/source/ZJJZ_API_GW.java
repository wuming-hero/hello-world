package com.wuming.source;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * 见证宝API V1.12 20180108
 *
 *
 * */

public class ZJJZ_API_GW {
    /*生成请求银行的完整报文*/
    public String getTranMessage(HashMap parmaKeyDict) {

        byte[] byteMessageBody;
        String tranMessage = "";
        String tranMessageBody = "";

        /*组业务报文体*/
        tranMessageBody = getTranMessageBody(parmaKeyDict);

        try {
            byteMessageBody = tranMessageBody.getBytes("gbk");//编码
        } catch (UnsupportedEncodingException ex) {
            return ex.toString();
        }

        /*组公网业务报文头*/
        int iLength = byteMessageBody.length;
        String hLength = String.format("%08d", iLength);
        String tranMessageHead = getTranMessageHead(hLength, parmaKeyDict);

        /*组公网通讯报文头*/
        int iNetLength = iLength + 122;
        String hNetLength = String.format("%010d", iNetLength);
        String tranMessageNetHead = getTranMessageNetHead(hNetLength, parmaKeyDict);

        /*组完整请求报文*/
        tranMessage = tranMessageNetHead + tranMessageHead + tranMessageBody;

        return tranMessage;
    }

    public String getTranMessage_ZX(HashMap parmaKeyDict) {
        //  String netType=(String)parmaKeyDict.get("NetType");//通讯类型：ZX：专线；GW：公网
        //  String hServType=(String)parmaKeyDict.get("ServType");//服务类型：01:请求02:应答

        byte[] byteMessageBody;
        String tranMessage = "";
        String tranMessageBody = "";

        /*组业务报文体*/
        tranMessageBody = getTranMessageBody(parmaKeyDict);

        try {
            byteMessageBody = tranMessageBody.getBytes("gbk");//编码
        } catch (UnsupportedEncodingException ex) {
            return ex.toString();
        }

        /*组公网业务报文头*/
        int iLength = byteMessageBody.length;
        String hLength = String.format("%08d", iLength);
        String tranMessageHead = getTranMessageHead(hLength, parmaKeyDict);


        /*组完整请求报文*/
        tranMessage = tranMessageHead + tranMessageBody;

        return tranMessage;
    }

    /*发送报文，并接收银行返回*/
    public void SendTranMessage_OLD(String tranMessage, String ServerIPAddress, int ServerPort, HashMap retKeyDict) {
        try {


            Socket s = new Socket(ServerIPAddress, ServerPort);
            s.setSendBufferSize(4096);
            s.setTcpNoDelay(true);
            s.setSoTimeout(60000);
            s.setKeepAlive(true);
            //Socket s = new Socket(InetAddress.getByName(null),port);
            OutputStream os = s.getOutputStream();
            InputStream is = s.getInputStream();

            os.write(tranMessage.getBytes("gbk"));
            os.flush();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int len = -1;
            while ((len = is.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            byte[] byteContent = bos.toByteArray();
            String recvMessage = new String(byteContent, "gbk");
            recvMessage = recvMessage + '\0';//修复使用spilt方法时的数组越界错误
            retKeyDict.put("RecvMessage", recvMessage);

            os.close();
            is.close();
            s.close();


        } catch (IOException ex) {
            System.out.println(ex.toString());
        }


    }

    /*发送报文，并接收银行返回*/
    public void SendTranMessage(String tranMessage, String ServerIPAddress, int ServerPort, HashMap retKeyDict) {
        Socket s = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            s = new Socket(ServerIPAddress, ServerPort);
            s.setSendBufferSize(4096);
            s.setTcpNoDelay(true);
            s.setSoTimeout(60000);
            s.setKeepAlive(true);

            os = s.getOutputStream();
            is = s.getInputStream();

            os.write(tranMessage.getBytes("gbk"));
            os.flush();

            //先读取通讯头报文222字节
            int netHeader = 222;
            byte[] netHeaderBytes = new byte[netHeader];
            int netHeaderReadCount = 0;
            while (netHeaderReadCount < netHeader) {
                netHeaderReadCount += is.read(netHeaderBytes, netHeaderReadCount, netHeader - netHeaderReadCount);
            }

            //分析通讯报文头，得到业务报文头+业务报文体的长度 30-40字节
            byte[] bizBytes = Arrays.copyOfRange(netHeaderBytes, 30, 40);
            int bizLen = Integer.parseInt(new String(bizBytes, "gbk"));
            System.out.println("-------------------------------");
            System.out.println("bizLen的长度:" + bizLen);

            byte[] bizBodyByte = new byte[bizLen];
            int bizBodyByteReadCount = 0;
            while (bizBodyByteReadCount < bizLen) {
                bizBodyByteReadCount += is.read(bizBodyByte, bizBodyByteReadCount, bizLen - bizBodyByteReadCount);
            }

            //拼装接收到的银行返回报文
            String recvMessage = new String(netHeaderBytes, "gbk") + new String(bizBodyByte, "gbk");

            recvMessage = recvMessage + '\0';//修复使用spilt方法时的数组越界错误

            retKeyDict.put("RecvMessage", recvMessage);


            os.close();
            is.close();
            s.close();


        } catch (IOException ex) {
            System.out.println(ex.toString());
        }


    }

    /*解析接收银行的报文，入参String类型*/
    public HashMap parsingTranMessageString(String TranMessage) {
        HashMap retKeyDict = new HashMap();
        int i;
        byte[] bNetHead = new byte[222];
        byte[] bTranFunc = new byte[226];
        byte[] bRspCode = new byte[93];
        byte[] bRspMsg = new byte[193];
        byte[] bHeadMsg = new byte[344];

       /*转换为GBK格式
      try{
          byte[]   byteRetMessage = TranMessage.getBytes("gbk");//编码
          String sRevMsg= new String(byteRetMessage,"gbk");
          retKeyDict.put("RevMsg_GBK",sRevMsg);
      }catch(UnsupportedEncodingException ex)
      {
              System.out.println(ex.toString());
      }

      String revMsg_GBK=(String)retKeyDict.get("RevMsg_GBK");
      */

        /*获取返回码*/
        try {
            byte[] byteRetMessage = TranMessage.getBytes("gbk");//编码
            for (i = 0; i < 93; i++) {
                bRspCode[i] = byteRetMessage[i];
            }
            String sRspCode = new String(bRspCode, "gbk");
            sRspCode = sRspCode.substring(87);
            retKeyDict.put("RspCode", sRspCode);
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex.toString());
        }

        /*获取返回信息*/
        try {
            byte[] byteRetMessage = TranMessage.getBytes("gbk");//编码
            for (i = 0; i < 193; i++) {
                bRspMsg[i] = byteRetMessage[i];
            }
            String sRspMsg = new String(bRspMsg, "gbk");
            sRspMsg = sRspMsg.substring(93);
            retKeyDict.put("RspMsgBak", sRspMsg);
            sRspMsg = sRspMsg.trim();
            retKeyDict.put("RspMsg", sRspMsg);
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex.toString());
        }

        String strCode = (String) retKeyDict.get("RspCode");

        if ("000000".equals(strCode)) {
            /*获取交易码 */
            try {
                byte[] byteRetMessage = TranMessage.getBytes("gbk");//编码
                for (i = 0; i < 226; i++) {
                    bTranFunc[i] = byteRetMessage[i];
                }
                String sTranFunc = new String(bTranFunc, "gbk");

                for (i = 0; i < 222; i++) {
                    bNetHead[i] = byteRetMessage[i];
                }
                String sNetHead = new String(bNetHead, "gbk");

                //String strRspMsg=(String)retKeyDict.get("RspMsg");
                int iTranLength = sNetHead.length();
                sTranFunc = sTranFunc.substring(iTranLength);
                // System.out.println(sTranFunc);
                retKeyDict.put("TranFunc", sTranFunc);
            } catch (UnsupportedEncodingException ex) {
                System.out.println(ex.toString());
            }

            // String strFunc=(String)retKeyDict.get("TranFunc");
            //System.out.println(strFunc);
            /*获取返回报文体*/
            try {
                byte[] byteRetMessage = TranMessage.getBytes("gbk");//编码
                String sBodyMsg = new String(byteRetMessage, "gbk");//解码
                for (i = 0; i < 344; i++) {
                    bHeadMsg[i] = byteRetMessage[i];
                }
                String sHeadMsg = new String(bHeadMsg, "gbk");
                //String strRspMsg=(String)retKeyDict.get("RspMsg");
                int iLength = sHeadMsg.length();
                sBodyMsg = sBodyMsg.substring(iLength);
                retKeyDict.put("BodyMsg", sBodyMsg);
            } catch (UnsupportedEncodingException ex) {
                System.out.println(ex.toString());
            }

            /*解析报文体*/
            spiltMessage(retKeyDict);

        }


        return retKeyDict;


    }

    public String getSignMessage(HashMap parmaKeyDict) {
        String signMessageBody = "";
        int hTranFunc = Integer.parseInt((String) parmaKeyDict.get("TranFunc"));

        switch (hTranFunc) {
            case 6005:
                signMessageBody = getSignMessageBody_6005(parmaKeyDict);
                break;
            case 6006:
                signMessageBody = getSignMessageBody_6006(parmaKeyDict);
                break;
            case 6111:
                signMessageBody = getSignMessageBody_6111(parmaKeyDict);
                break;
            case 6120:
                signMessageBody = getSignMessageBody_6120(parmaKeyDict);
                break;
            case 6134:
                signMessageBody = getSignMessageBody_6134(parmaKeyDict);
                break;
            case 6136:
                signMessageBody = getSignMessageBody_6136(parmaKeyDict);
                break;
            case 6165:
                signMessageBody = getSignMessageBody_6165(parmaKeyDict);
                break;

            default:
                signMessageBody = "交易码未配置，请自行根据接口文档及demo封装接口";
        }

        return signMessageBody;
    }

    /*生成请求银行报文的报文头*/
    private String getTranMessageHead(String hLength, HashMap parmaKeyDict) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式

        String hServType = "01";
        String hMacCode = "                ";
        String hTrandateTime = df.format(new Date());
        String hRspCode = "999999";
        String hRspMsg = "                                          ";
        String hConFlag = "0";
        String hCounterId = "PA001";

        String hTranFunc = (String) parmaKeyDict.get("TranFunc");
        String hThirdLogNo = (String) parmaKeyDict.get("ThirdLogNo");
        String hQydm = (String) parmaKeyDict.get("Qydm");


        String tranMessageHead = hTranFunc + hServType + hMacCode + hTrandateTime + hRspCode + hRspMsg +
                hConFlag + hLength + hCounterId + hThirdLogNo + hQydm;

        return tranMessageHead;
    }

    private String getTranMessageNetHead(String hLength, HashMap parmaKeyDict) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式

        String netHeadPart1 = "A001260101";
        String netHeadPart2 = "                ";//16个空格
        String hTradeCode = "000000";
        String hServType = "01";
        //String hMacCode="                ";
        String hTrandateTime = df.format(new Date());
        String hRspCode = "999999";
        String hRspMsg = "                                                                                                    ";//100个空格
        String hConFlag = "0";
        String hCounterId = "PA001";
        String hTimes = "000";
        String hSignFlag = "0";
        String hSignPacketType = "0";
        String netHeadPart3 = "            ";//12个空格
        String netHeadPart4 = "00000000000";


        // String hTranFunc=(String)parmaKeyDict.get("TranFunc");
        String hThirdLogNo = (String) parmaKeyDict.get("ThirdLogNo");
        String hQydm = (String) parmaKeyDict.get("Qydm");

        String tranMessageNetHead = netHeadPart1 + hQydm + netHeadPart2 + hLength + hTradeCode + hCounterId + hServType + hTrandateTime + hThirdLogNo +
                hRspCode + hRspMsg + hConFlag + hTimes + hSignFlag + hSignPacketType + netHeadPart3 + netHeadPart4;

        return tranMessageNetHead;
    }


    /*根据交易码调用不同的交易请求报文生成方法*/
    private String getTranMessageBody(HashMap parmaKeyDict) {
        String tranMessageBody = "";
        int hTranFunc = Integer.parseInt((String) parmaKeyDict.get("TranFunc"));

        switch (hTranFunc) {
            case 6000:
                tranMessageBody = getTranMessageBody_6000(parmaKeyDict);
                break;
            case 6005:
                tranMessageBody = getTranMessageBody_6033(parmaKeyDict);
                break;
            case 6006:
                tranMessageBody = getTranMessageBody_6034(parmaKeyDict);
                break;
            case 6007:
                tranMessageBody = getTranMessageBody_6007(parmaKeyDict);
                break;
            case 6008:
                tranMessageBody = getTranMessageBody_6008(parmaKeyDict);
                break;
            case 6010:
                tranMessageBody = getTranMessageBody_6010(parmaKeyDict);
                break;
            case 6011:
                tranMessageBody = getTranMessageBody_6011(parmaKeyDict);
                break;
            case 6014:
                tranMessageBody = getTranMessageBody_6014(parmaKeyDict);
                break;
            case 6027:
                tranMessageBody = getTranMessageBody_6027(parmaKeyDict);
                break;
            case 6031:
                tranMessageBody = getTranMessageBody_6031(parmaKeyDict);
                break;
            case 6033:
                tranMessageBody = getTranMessageBody_6033(parmaKeyDict);
                break;
            case 6034:
                tranMessageBody = getTranMessageBody_6034(parmaKeyDict);
                break;
            case 6040:
                tranMessageBody = getTranMessageBody_6048(parmaKeyDict);
                break;
            case 6041:
                tranMessageBody = getTranMessageBody_6048(parmaKeyDict);
                break;
            case 6042:
                tranMessageBody = getTranMessageBody_6048(parmaKeyDict);
                break;
            case 6043:
                tranMessageBody = getTranMessageBody_6048(parmaKeyDict);
                break;
            case 6044:
                tranMessageBody = getTranMessageBody_6048(parmaKeyDict);
                break;
            case 6048:
                tranMessageBody = getTranMessageBody_6048(parmaKeyDict);
                break;
            case 6050:
                tranMessageBody = getTranMessageBody_6050(parmaKeyDict);
                break;
            case 6052:
                tranMessageBody = getTranMessageBody_6052(parmaKeyDict);
                break;
            case 6053:
                tranMessageBody = getTranMessageBody_6053(parmaKeyDict);
                break;
            case 6055:
                tranMessageBody = getTranMessageBody_6055(parmaKeyDict);
                break;
            case 6056:
                tranMessageBody = getTranMessageBody_6056(parmaKeyDict);
                break;
            case 6061:
                tranMessageBody = getTranMessageBody_6061(parmaKeyDict);
                break;
            case 6063:
                tranMessageBody = getTranMessageBody_6063(parmaKeyDict);
                break;
            case 6064:
                tranMessageBody = getTranMessageBody_6064(parmaKeyDict);
                break;
            case 6065:
                tranMessageBody = getTranMessageBody_6065(parmaKeyDict);
                break;
            case 6066:
                tranMessageBody = getTranMessageBody_6066(parmaKeyDict);
                break;
            case 6067:
                tranMessageBody = getTranMessageBody_6067(parmaKeyDict);
                break;
            case 6068:
                tranMessageBody = getTranMessageBody_6068(parmaKeyDict);
                break;
            case 6069:
                tranMessageBody = getTranMessageBody_6069(parmaKeyDict);
                break;
            case 6070:
                tranMessageBody = getTranMessageBody_6070(parmaKeyDict);
                break;
            case 6071:
                tranMessageBody = getTranMessageBody_6071(parmaKeyDict);
                break;
            case 6072:
                tranMessageBody = getTranMessageBody_6072(parmaKeyDict);
                break;
            case 6073:
                tranMessageBody = getTranMessageBody_6073(parmaKeyDict);
                break;
            case 6037:
                tranMessageBody = getTranMessageBody_6037(parmaKeyDict);
                break;
            case 6077:
                tranMessageBody = getTranMessageBody_6077(parmaKeyDict);
                break;
            case 6079:
                tranMessageBody = getTranMessageBody_6079(parmaKeyDict);
                break;
            case 6080:
                tranMessageBody = getTranMessageBody_6080(parmaKeyDict);
                break;
            case 6082:
                tranMessageBody = getTranMessageBody_6082(parmaKeyDict);
                break;
            case 6083:
                tranMessageBody = getTranMessageBody_6083(parmaKeyDict);
                break;
            case 6084:
                tranMessageBody = getTranMessageBody_6084(parmaKeyDict);
                break;
            case 6085:
                tranMessageBody = getTranMessageBody_6085(parmaKeyDict);
                break;
            case 6087:
                tranMessageBody = getTranMessageBody_6087(parmaKeyDict);
                break;
            case 6088:
                tranMessageBody = getTranMessageBody_6088(parmaKeyDict);
                break;
            case 6090:
                tranMessageBody = getTranMessageBody_6090(parmaKeyDict);
                break;
            case 6091:
                tranMessageBody = getTranMessageBody_6091(parmaKeyDict);
                break;
            case 6092:
                tranMessageBody = getTranMessageBody_6092(parmaKeyDict);
                break;
            case 6093:
                tranMessageBody = getTranMessageBody_6093(parmaKeyDict);
                break;
            case 6097:
                tranMessageBody = getTranMessageBody_6097(parmaKeyDict);
                break;
            case 6098:
                tranMessageBody = getTranMessageBody_6098(parmaKeyDict);
                break;
            case 6099:
                tranMessageBody = getTranMessageBody_6099(parmaKeyDict);
                break;
            case 6100:
                tranMessageBody = getTranMessageBody_6100(parmaKeyDict);
                break;
            case 6101:
                tranMessageBody = getTranMessageBody_6101(parmaKeyDict);
                break;
            case 6102:
                tranMessageBody = getTranMessageBody_6102(parmaKeyDict);
                break;
            case 6103:
                tranMessageBody = getTranMessageBody_6103(parmaKeyDict);
                break;
            case 6108:
                tranMessageBody = getTranMessageBody_6108(parmaKeyDict);
                break;
            case 6109:
                tranMessageBody = getTranMessageBody_6109(parmaKeyDict);
                break;
            case 6110:
                tranMessageBody = getTranMessageBody_6110(parmaKeyDict);
                break;
            case 6111:
                tranMessageBody = getTranMessageBody_6111(parmaKeyDict);
                break;
            case 6114:
                tranMessageBody = getTranMessageBody_6114(parmaKeyDict);
                break;
            case 6118:
                tranMessageBody = getTranMessageBody_6118(parmaKeyDict);
                break;
            case 6119:
                tranMessageBody = getTranMessageBody_6119(parmaKeyDict);
                break;
            case 6120:
                tranMessageBody = getTranMessageBody_6120(parmaKeyDict);
                break;
            case 6121:
                tranMessageBody = getTranMessageBody_6121(parmaKeyDict);
                break;
            case 6122:
                tranMessageBody = getTranMessageBody_6122(parmaKeyDict);
                break;
            case 6123:
                tranMessageBody = getTranMessageBody_6123(parmaKeyDict);
                break;
            case 6124:
                tranMessageBody = getTranMessageBody_6124(parmaKeyDict);
                break;
            case 6126:
                tranMessageBody = getTranMessageBody_6126(parmaKeyDict);
                break;
            case 6127:
                tranMessageBody = getTranMessageBody_6127(parmaKeyDict);
                break;
            case 6128:
                tranMessageBody = getTranMessageBody_6128(parmaKeyDict);
                break;
            case 6133:
                tranMessageBody = getTranMessageBody_6133(parmaKeyDict);
                break;
            case 6134:
                tranMessageBody = getTranMessageBody_6134(parmaKeyDict);
                break;
            case 6135:
                tranMessageBody = getTranMessageBody_6135(parmaKeyDict);
                break;
            case 6136:
                tranMessageBody = getTranMessageBody_6136(parmaKeyDict);
                break;
            case 6137:
                tranMessageBody = getTranMessageBody_6137(parmaKeyDict);
                break;
            case 6138:
                tranMessageBody = getTranMessageBody_6138(parmaKeyDict);
                break;
            case 6142:
                tranMessageBody = getTranMessageBody_6142(parmaKeyDict);
                break;
            case 6145:
                tranMessageBody = getTranMessageBody_6145(parmaKeyDict);
                break;
            case 6146:
                tranMessageBody = getTranMessageBody_6146(parmaKeyDict);
                break;
            case 6147:
                tranMessageBody = getTranMessageBody_6147(parmaKeyDict);
                break;
            case 6148:
                tranMessageBody = getTranMessageBody_6148(parmaKeyDict);
                break;
            case 6149:
                tranMessageBody = getTranMessageBody_6149(parmaKeyDict);
                break;
            case 6150:
                tranMessageBody = getTranMessageBody_6150(parmaKeyDict);
                break;
            case 6151:
                tranMessageBody = getTranMessageBody_6151(parmaKeyDict);
                break;
            case 6152:
                tranMessageBody = getTranMessageBody_6152(parmaKeyDict);
                break;
            case 6153:
                tranMessageBody = getTranMessageBody_6153(parmaKeyDict);
                break;
            case 6154:
                tranMessageBody = getTranMessageBody_6154(parmaKeyDict);
                break;
            case 6155:
                tranMessageBody = getTranMessageBody_6155(parmaKeyDict);
                break;
            case 6156:
                tranMessageBody = getTranMessageBody_6156(parmaKeyDict);
                break;
            case 6157:
                tranMessageBody = getTranMessageBody_6157(parmaKeyDict);
                break;
            case 6158:
                tranMessageBody = getTranMessageBody_6158(parmaKeyDict);
                break;
            case 6159:
                tranMessageBody = getTranMessageBody_6159(parmaKeyDict);
                break;
            case 6160:
                tranMessageBody = getTranMessageBody_6160(parmaKeyDict);
                break;
            case 6161:
                tranMessageBody = getTranMessageBody_6161(parmaKeyDict);
                break;
            case 6162:
                tranMessageBody = getTranMessageBody_6162(parmaKeyDict);
                break;
            case 6163:
                tranMessageBody = getTranMessageBody_6163(parmaKeyDict);
                break;
            case 6164:
                tranMessageBody = getTranMessageBody_6164(parmaKeyDict);
                break;
            case 6165:
                tranMessageBody = getTranMessageBody_6165(parmaKeyDict);
                break;
            case 6166:
                tranMessageBody = getTranMessageBody_6166(parmaKeyDict);
                break;
            case 6201:
                tranMessageBody = getTranMessageBody_6201(parmaKeyDict);
                break;
            case 6202:
                tranMessageBody = getTranMessageBody_6202(parmaKeyDict);
                break;
            default:
                tranMessageBody = "交易码未配置，请自行根据接口文档及demo封装接口";
        }

        return tranMessageBody;
    }

    /*根据交易码调用不同的交易返回报文生成方法*/
/*
  private String getRetMessageBody(HashMap parmaKeyDict)
  {
      String tranMessageBody="";
      String hTranFunc=(String)parmaKeyDict.get("TranFunc");

      switch(hTranFunc)
      {
          case "6000":tranMessageBody=getRetMessageBody_6000(parmaKeyDict);break;
      }

      return tranMessageBody;
  }
  */
    /*生成6000交易的报文体:会员注册*/
    private String getTranMessageBody_6000(HashMap parmaKeyDict) {

        String bFuncFlag = "";           //功能标志
        String bSupAcctId = "";          //资金汇总账号
        String bThirdCustId = "";        //交易网会员代码
        String bCustProperty = "";       //会员属性
        String bNickName = "";           //用户昵称
        String bMobilePhone = "";        //手机号码
        String bEmail = "";              //Email
        String bReserve = "";            //保留域


        if (parmaKeyDict.containsKey("FuncFlag")) {
            bFuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("SupAcctId")) {
            bSupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            bThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustProperty")) {
            bCustProperty = (String) parmaKeyDict.get("CustProperty");
        }

        if (parmaKeyDict.containsKey("NickName")) {
            bNickName = (String) parmaKeyDict.get("NickName");
        }

        if (parmaKeyDict.containsKey("MobilePhone")) {
            bMobilePhone = (String) parmaKeyDict.get("MobilePhone");
        }

        if (parmaKeyDict.containsKey("Email")) {
            bEmail = (String) parmaKeyDict.get("Email");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            bReserve = (String) parmaKeyDict.get("Reserve");
        }


        String tranMessageBody = bFuncFlag + "&" + bSupAcctId + "&" + bThirdCustId + "&" + bCustProperty + "&" + bNickName + "&" + bMobilePhone + "&" + bEmail + "&" + bReserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6055(HashMap parmaKeyDict) {

        String SupAcctId = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String CustName = "";
        String IdType = "";
        String IdCode = "";
        String AcctId = "";
        String BankType = "";
        String BankName = "";
        String BankCode = "";
        String SBankCode = "";
        String MobilePhone = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }

        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }

        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }

        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }

        if (parmaKeyDict.containsKey("BankType")) {
            BankType = (String) parmaKeyDict.get("BankType");
        }

        if (parmaKeyDict.containsKey("BankName")) {
            BankName = (String) parmaKeyDict.get("BankName");
        }

        if (parmaKeyDict.containsKey("BankCode")) {
            BankCode = (String) parmaKeyDict.get("BankCode");
        }

        if (parmaKeyDict.containsKey("SBankCode")) {
            SBankCode = (String) parmaKeyDict.get("SBankCode");
        }

        if (parmaKeyDict.containsKey("MobilePhone")) {
            MobilePhone = (String) parmaKeyDict.get("MobilePhone");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }


        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ThirdCustId + "&" + CustName + "&" + IdType + "&" + IdCode + "&" + AcctId + "&" + BankType + "&" + BankName + "&" + BankCode + "&" + SBankCode + "&" + MobilePhone + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6063(HashMap parmaKeyDict) {

        String FuncFlag = "";
        String SupAcctId = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String CustName = "";
        String IdType = "";
        String IdCode = "";
        String AcctId = "";
        String BankName = "";
        String BankCode = "";
        String SBankCode = "";
        String MobilePhone = "";
        String Reserve = "";
        String TelePhone = "";
        String Email = "";
        String Address = "";
        String CPFlag = "";
        String BankFlag = "";
        String NickName = "";
        String AcctName = "";

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }

        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }

        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }

        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }

        if (parmaKeyDict.containsKey("BankFlag")) {
            BankFlag = (String) parmaKeyDict.get("BankFlag");
        }

        if (parmaKeyDict.containsKey("BankName")) {
            BankName = (String) parmaKeyDict.get("BankName");
        }

        if (parmaKeyDict.containsKey("BankCode")) {
            BankCode = (String) parmaKeyDict.get("BankCode");
        }

        if (parmaKeyDict.containsKey("SBankCode")) {
            SBankCode = (String) parmaKeyDict.get("SBankCode");
        }

        if (parmaKeyDict.containsKey("MobilePhone")) {
            MobilePhone = (String) parmaKeyDict.get("MobilePhone");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        if (parmaKeyDict.containsKey("TelePhone")) {
            TelePhone = (String) parmaKeyDict.get("TelePhone");
        }

        if (parmaKeyDict.containsKey("Email")) {
            Email = (String) parmaKeyDict.get("Email");
        }

        if (parmaKeyDict.containsKey("CPFlag")) {
            CPFlag = (String) parmaKeyDict.get("CPFlag");
        }

        if (parmaKeyDict.containsKey("NickName")) {
            NickName = (String) parmaKeyDict.get("NickName");
        }

        if (parmaKeyDict.containsKey("AcctName")) {
            AcctName = (String) parmaKeyDict.get("AcctName");
        }

        if (parmaKeyDict.containsKey("Address")) {
            Address = (String) parmaKeyDict.get("Address");
        }


        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + CustAcctId + "&" + CustName + "&" + NickName + "&" + IdType + "&" + IdCode + "&" + ThirdCustId + "&" + MobilePhone + "&" + TelePhone + "&" + Email + "&" + Address + "&" + CPFlag + "&" + BankFlag + "&" + AcctName + "&" + AcctId + "&" + BankName + "&" + BankCode + "&" + SBankCode + "&" + Reserve + "&";

        return tranMessageBody;
    }


    private String getTranMessageBody_6064(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String CustAcctId = "";
        String AcctId = "";
        String TranAmount = "";
        String CcyCode = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + ThirdCustId + "&" + CustAcctId + "&" + AcctId + "&" + TranAmount + "&" + CcyCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6066(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String CustName = "";
        String IdType = "";
        String IdCode = "";
        String AcctId = "";
        String BankType = "";
        String BankName = "";
        String BankCode = "";
        String SBankCode = "";
        String MobilePhone = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }

        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }

        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }

        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }

        if (parmaKeyDict.containsKey("BankType")) {
            BankType = (String) parmaKeyDict.get("BankType");
        }

        if (parmaKeyDict.containsKey("BankName")) {
            BankName = (String) parmaKeyDict.get("BankName");
        }

        if (parmaKeyDict.containsKey("BankCode")) {
            BankCode = (String) parmaKeyDict.get("BankCode");
        }

        if (parmaKeyDict.containsKey("SBankCode")) {
            SBankCode = (String) parmaKeyDict.get("SBankCode");
        }

        if (parmaKeyDict.containsKey("MobilePhone")) {
            MobilePhone = (String) parmaKeyDict.get("MobilePhone");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ThirdCustId + "&" + CustName + "&" + IdType + "&" + IdCode + "&" + AcctId + "&" + BankType + "&" + BankName + "&" + BankCode + "&" + SBankCode + "&" + MobilePhone + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6067(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String CustAcctId = "";
        String TranAmount = "";
        String AcctId = "";
        String MessageCode = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }


        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }

        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + ThirdCustId + "&" + CustAcctId + "&" + AcctId + "&" + MessageCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6056(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String CustAcctId = "";
        String TranAmount = "";
        String CcyCode = "";
        String Note = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ThirdCustId + "&" + TranAmount + "&" + CcyCode + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6121(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String TranAmount = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String ThirdHtMsg = "";
        String Note = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String) parmaKeyDict.get("ThirdHtMsg");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ThirdCustId + "&" + TranAmount + "&" + CcyCode + "&" + ThirdHtId + "&" + ThirdHtMsg + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6122(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String OrigThirdLogNo = "";
        String ThirdHtId = "";
        String TranAmount = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("OrigThirdLogNo")) {
            OrigThirdLogNo = (String) parmaKeyDict.get("OrigThirdLogNo");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }


        String tranMessageBody = SupAcctId + "&" + FuncFlag + "&" + OrigThirdLogNo + "&" + ThirdHtId + "&" + TranAmount + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6123(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String File1_Name = "";
        String File1_Password = "";
        String File2_Name = "";
        String File2_Password = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("File1_Name")) {
            File1_Name = (String) parmaKeyDict.get("File1_Name");
        }

        if (parmaKeyDict.containsKey("File1_Password")) {
            File1_Password = (String) parmaKeyDict.get("File1_Password");
        }

        if (parmaKeyDict.containsKey("File2_Name")) {
            File2_Name = (String) parmaKeyDict.get("File2_Name");
        }

        if (parmaKeyDict.containsKey("File2_Password")) {
            File2_Password = (String) parmaKeyDict.get("File2_Password");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }


        String tranMessageBody = SupAcctId + "&" + File1_Name + "&" + File1_Password + "&" + File2_Name + "&" + File2_Password + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6124(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String File1_Name = "";
        String File2_Name = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("File1_Name")) {
            File1_Name = (String) parmaKeyDict.get("File1_Name");
        }

        if (parmaKeyDict.containsKey("File2_Name")) {
            File2_Name = (String) parmaKeyDict.get("File2_Name");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + File1_Name + "&" + File2_Name + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6061(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String OrigThirdLogNo = "";
        String TranDate = "";
        String Reserve = "";
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("OrigThirdLogNo")) {
            OrigThirdLogNo = (String) parmaKeyDict.get("OrigThirdLogNo");
        }
        if (parmaKeyDict.containsKey("TranDate")) {
            TranDate = (String) parmaKeyDict.get("TranDate");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + OrigThirdLogNo + "&" + TranDate + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6033(HashMap parmaKeyDict) {
        String TranWebName = "";
        String CustAcctId = "";
        String IdType = "";
        String IdCode = "";
        String ThirdCustId = "";
        String CustName = "";
        String SupAcctId = "";
        String OutAcctId = "";
        String OutAcctIdName = "";
        String CcyCode = "";
        String TranAmount = "";
        String Note = "";
        String Reserve = "";
        String WebSign = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        if (parmaKeyDict.containsKey("TranWebName")) {
            TranWebName = (String) parmaKeyDict.get("TranWebName");
        }

        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }

        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }

        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }
        if (parmaKeyDict.containsKey("OutAcctIdName")) {
            OutAcctIdName = (String) parmaKeyDict.get("OutAcctIdName");
        }
        if (parmaKeyDict.containsKey("OutAcctId")) {
            OutAcctId = (String) parmaKeyDict.get("OutAcctId");
        }
        if (parmaKeyDict.containsKey("WebSign")) {
            WebSign = (String) parmaKeyDict.get("WebSign");
        }

        String tranMessageBody = TranWebName + "&" + CustAcctId + "&" + IdType + "&" + IdCode + "&" + ThirdCustId + "&" + CustName + "&" + SupAcctId + "&" + OutAcctId + "&" + OutAcctIdName + "&" + CcyCode + "&" + TranAmount + "&" + Note + "&" + Reserve + "&" + WebSign + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6007(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String CustAcctId = "";
        String FuncFlag = "";
        String TranFee = "";
        String TranAmount = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String ThirdHtMsg = "";
        String Note = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String) parmaKeyDict.get("TranFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String) parmaKeyDict.get("ThirdHtMsg");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + CustAcctId + "&" + ThirdCustId + "&" + TranAmount + "&" + TranFee + "&" + CcyCode + "&" + ThirdHtId + "&" + ThirdHtMsg + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6134(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String FuncFlag = "";
        String ThirdCustId = "";
        String TranAmount = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String ThirdHtMsg = "";
        String Note = "";
        String Reserve = "";
        String WebSign = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String) parmaKeyDict.get("ThirdHtMsg");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        if (parmaKeyDict.containsKey("WebSign")) {
            WebSign = (String) parmaKeyDict.get("WebSign");
        }
        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + FuncFlag + "&" + ThirdCustId + "&" + TranAmount + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + ThirdHtMsg + "&" + Note + "&" + Reserve + "&" + WebSign + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6135(HashMap parmaKeyDict) {
        String FuncFlag = "";
        String SupAcctId = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String TranAmount = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String ThirdHtMsg = "";
        String Note = "";
        String SerialNo = "";
        String MessageCode = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String) parmaKeyDict.get("ThirdHtMsg");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("SerialNo")) {
            SerialNo = (String) parmaKeyDict.get("SerialNo");
        }

        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + CustAcctId + "&" + ThirdCustId + "&" + TranAmount + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + ThirdHtMsg + "&" + Note + "&" + SerialNo + "&" + MessageCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6008(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String CustAcctId = "";
        String CustName = "";
        String TranAmount = "";
        String CcyCode = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ThirdCustId + "&" + CustName + "&" + TranAmount + "&" + CcyCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6031(HashMap parmaKeyDict) {
        String FuncFlag = "";
        String OutCustAcctId = "";
        String SupAcctId = "";
        String OutThirdCustId = "";
        String OutCustName = "";
        String InCustAcctId = "";
        String InThirdCustId = "";
        String InCustName = "";
        String TranAmount = "";
        String TranFee = "";
        String TranType = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String ThirdHtMsg = "";
        String Note = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustName")) {
            OutCustName = (String) parmaKeyDict.get("OutCustName");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String) parmaKeyDict.get("InThirdCustId");
        }

        if (parmaKeyDict.containsKey("InCustName")) {
            InCustName = (String) parmaKeyDict.get("InCustName");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String) parmaKeyDict.get("TranFee");
        }
        if (parmaKeyDict.containsKey("TranType")) {
            TranType = (String) parmaKeyDict.get("TranType");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }
        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String) parmaKeyDict.get("ThirdHtMsg");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }


        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + OutCustName + "&" + InCustAcctId + "&" + InThirdCustId + "&" + InCustName + "&" + TranAmount + "&" + TranFee + "&" + TranType + "&" + CcyCode + "&" + ThirdHtId + "&" + ThirdHtMsg + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6034(HashMap parmaKeyDict) {
        String FuncFlag = "";
        String OutCustAcctId = "";
        String SupAcctId = "";
        String OutThirdCustId = "";
        String OutCustName = "";
        String InCustAcctId = "";
        String InThirdCustId = "";
        String InCustName = "";
        String TranAmount = "";
        String TranFee = "";
        String TranType = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String ThirdHtMsg = "";
        String Note = "";
        String Reserve = "";
        String WebSign = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustName")) {
            OutCustName = (String) parmaKeyDict.get("OutCustName");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String) parmaKeyDict.get("InThirdCustId");
        }

        if (parmaKeyDict.containsKey("InCustName")) {
            InCustName = (String) parmaKeyDict.get("InCustName");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String) parmaKeyDict.get("TranFee");
        }
        if (parmaKeyDict.containsKey("TranType")) {
            TranType = (String) parmaKeyDict.get("TranType");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }
        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String) parmaKeyDict.get("ThirdHtMsg");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        if (parmaKeyDict.containsKey("WebSign")) {
            WebSign = (String) parmaKeyDict.get("WebSign");
        }

        String tranMessageBody = FuncFlag + "&" + OutCustAcctId + "&" + SupAcctId + "&" + OutThirdCustId + "&" + OutCustName + "&" + InCustAcctId + "&" + InThirdCustId + "&" + InCustName + "&" + TranAmount + "&" + TranFee + "&" + TranType + "&" + CcyCode + "&" + ThirdHtId + "&" + ThirdHtMsg + "&" + Note + "&" + Reserve + "&" + WebSign + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6010(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String SelectFlag = "";
        String PageNum = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("SelectFlag")) {
            SelectFlag = (String) parmaKeyDict.get("SelectFlag");
        }

        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + SelectFlag + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6014(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String OrigThirdLogNo = "";
        String TranDate = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("OrigThirdLogNo")) {
            OrigThirdLogNo = (String) parmaKeyDict.get("OrigThirdLogNo");
        }

        if (parmaKeyDict.containsKey("TranDate")) {
            TranDate = (String) parmaKeyDict.get("TranDate");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + FuncFlag + "&" + OrigThirdLogNo + "&" + TranDate + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6048(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String BeginDate = "";
        String EndDate = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + BeginDate + "&" + EndDate + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6050(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }
        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6052(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();
        String FuncFlag = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String SupAcctId = "";
        String ThirdHtCount = "";
        String Reserve = "";
        String WebSign = "";
        String array = "";
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;
        String[] TranFee = null;
        String[] CcyCode = null;
        String[] ThirdHtId = null;
        String[] ThirdHtMsg = null;
        String[] Note = null;
        String[] MarketLogNo = null;
        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }
        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }
        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("ThirdHtCount")) {
            ThirdHtCount = (String) parmaKeyDict.get("ThirdHtCount");
        }
        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }
        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String[]) parmaKeyDict.get("TranFee");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String[]) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String[]) parmaKeyDict.get("ThirdHtId");
        }
        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String[]) parmaKeyDict.get("ThirdHtMsg");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String[]) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("MarketLogNo")) {
            MarketLogNo = (String[]) parmaKeyDict.get("MarketLogNo");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        if (parmaKeyDict.containsKey("WebSign")) {
            WebSign = (String) parmaKeyDict.get("WebSign");
        }
        int iCount = Integer.parseInt(ThirdHtCount);

        for (int i = 0; i < iCount; i++) {
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
            list.add(TranFee[i]);
            list.add(CcyCode[i]);
            list.add(ThirdHtId[i]);
            list.add(ThirdHtMsg[i]);
            list.add(Note[i]);
            list.add(MarketLogNo[i]);
        }
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }
        String tranMessageBody = FuncFlag + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + SupAcctId + "&" + ThirdHtCount + "&" + array + "&" + Reserve + "&" + WebSign + "&";

        return tranMessageBody;

    }

    private String getTranMessageBody_6133(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();
        String FuncFlag = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String SupAcctId = "";
        String ThirdHtCount = "";
        String SerialNo = "";
        String MessageCode = "";
        String Reserve = "";
        String array = "";
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;
        String[] TranFee = null;
        String[] CcyCode = null;
        String[] ThirdHtId = null;
        String[] ThirdHtMsg = null;
        String[] Note = null;
        String[] MarketLogNo = null;
        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }
        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }
        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("ThirdHtCount")) {
            ThirdHtCount = (String) parmaKeyDict.get("ThirdHtCount");
        }
        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }
        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String[]) parmaKeyDict.get("TranFee");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String[]) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String[]) parmaKeyDict.get("ThirdHtId");
        }
        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String[]) parmaKeyDict.get("ThirdHtMsg");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String[]) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("MarketLogNo")) {
            MarketLogNo = (String[]) parmaKeyDict.get("MarketLogNo");
        }
        if (parmaKeyDict.containsKey("SerialNo")) {
            SerialNo = (String) parmaKeyDict.get("SerialNo");
        }
        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        int iCount = Integer.parseInt(ThirdHtCount);

        for (int i = 0; i < iCount; i++) {
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
            list.add(TranFee[i]);
            list.add(CcyCode[i]);
            list.add(ThirdHtId[i]);
            list.add(ThirdHtMsg[i]);
            list.add(Note[i]);
            list.add(MarketLogNo[i]);
        }
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }
        String tranMessageBody = FuncFlag + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + SupAcctId + "&" + ThirdHtCount + "&" + array + "&" + SerialNo + "&" + MessageCode + "&" + Reserve + "&";

        return tranMessageBody;

    }


    private String getTranMessageBody_6120(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();
        String FuncFlag = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String SupAcctId = "";
        String ThirdHtCount = "";
        String Reserve = "";
        String WebSign = "";
        String array = "";
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;
        String[] TranFee = null;
        String[] CcyCode = null;
        String[] ThirdHtId = null;
        String[] ThirdHtMsg = null;
        String[] Note = null;
        String[] MarketLogNo = null;
        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }
        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }
        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("ThirdHtCount")) {
            ThirdHtCount = (String) parmaKeyDict.get("ThirdHtCount");
        }
        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }
        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String[]) parmaKeyDict.get("TranFee");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String[]) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String[]) parmaKeyDict.get("ThirdHtId");
        }
        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String[]) parmaKeyDict.get("ThirdHtMsg");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String[]) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("MarketLogNo")) {
            MarketLogNo = (String[]) parmaKeyDict.get("MarketLogNo");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        if (parmaKeyDict.containsKey("WebSign")) {
            WebSign = (String) parmaKeyDict.get("WebSign");
        }
        int iCount = Integer.parseInt(ThirdHtCount);

        for (int i = 0; i < iCount; i++) {
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
            list.add(TranFee[i]);
            list.add(CcyCode[i]);
            list.add(ThirdHtId[i]);
            list.add(ThirdHtMsg[i]);
            list.add(Note[i]);
            list.add(MarketLogNo[i]);
        }
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }
        String tranMessageBody = FuncFlag + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + SupAcctId + "&" + ThirdHtCount + "&" + array + "&" + Reserve + "&" + WebSign + "&";

        return tranMessageBody;

    }

    private String getTranMessageBody_6053(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();
        String SupAcctId = "";
        String TotalCount = "";
        String Reserve = "";
        String array = "";
        String[] CustAcctId = null;
        String[] ThirdCustId = null;
        String[] TranAmount = null;
        String[] Note = null;
        String[] MarketLogNo = null;
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("TotalCount")) {
            TotalCount = (String) parmaKeyDict.get("TotalCount");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String[]) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String[]) parmaKeyDict.get("ThirdCustId");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String[]) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("MarketLogNo")) {
            MarketLogNo = (String[]) parmaKeyDict.get("MarketLogNo");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        int iCount = Integer.parseInt(TotalCount);
        for (int i = 0; i < iCount; i++) {
            list.add(CustAcctId[i]);
            list.add(ThirdCustId[i]);
            list.add(TranAmount[i]);
            list.add(Note[i]);
            list.add(MarketLogNo[i]);
        }
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }
        String tranMessageBody = SupAcctId + "&" + TotalCount + "&" + array + "&" + Reserve + "&";

        return tranMessageBody;

    }

    private String getTranMessageBody_6027(HashMap parmaKeyDict) {
        String BankNo = "";
        String KeyWord = "";
        String BankName = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("BankNo")) {
            BankNo = (String) parmaKeyDict.get("BankNo");
        }

        if (parmaKeyDict.containsKey("KeyWord")) {
            KeyWord = (String) parmaKeyDict.get("KeyWord");
        }

        if (parmaKeyDict.containsKey("BankName")) {
            BankName = (String) parmaKeyDict.get("BankName");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = BankNo + "&" + KeyWord + "&" + BankName + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6072(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String SelectFlag = "";
        String FuncFlag = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("SelectFlag")) {
            SelectFlag = (String) parmaKeyDict.get("SelectFlag");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }
        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + CustAcctId + "&" + SelectFlag + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6037(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }


        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }


        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + ThirdCustId + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6073(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String SelectFlag = "";
        String FuncFlag = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("SelectFlag")) {
            SelectFlag = (String) parmaKeyDict.get("SelectFlag");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }
        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + CustAcctId + "&" + SelectFlag + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6126(HashMap parmaKeyDict) {
        String FuncFlag = "";
        String Status = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("Status")) {
            Status = (String) parmaKeyDict.get("Status");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }

        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + Status + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6127(HashMap parmaKeyDict) {
        String OldFrontLogNo = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String CustName = "";
        String TranAmount = "";
        String CcyCode = "";
        String Note = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("OldFrontLogNo")) {
            OldFrontLogNo = (String) parmaKeyDict.get("OldFrontLogNo");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = OldFrontLogNo + "&" + CustAcctId + "&" + ThirdCustId + "&" + CustName + "&" + TranAmount + "&" + CcyCode + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6128(HashMap parmaKeyDict) {
        String OldFrontLogNo = "";
        String TranAmount = "";
        String Note = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("OldFrontLogNo")) {
            OldFrontLogNo = (String) parmaKeyDict.get("OldFrontLogNo");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }


        String tranMessageBody = OldFrontLogNo + "&" + TranAmount + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }


    private String getTranMessageBody_6077(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String OrigThirdLogNo = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("OrigThirdLogNo")) {
            OrigThirdLogNo = (String) parmaKeyDict.get("OrigThirdLogNo");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + FuncFlag + "&" + OrigThirdLogNo + "&" + Reserve + "&";

        return tranMessageBody;
    }


    private String getTranMessageBody_6071(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String AcctId = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("SelectFlag")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + ThirdCustId + "&" + CustAcctId + "&" + AcctId + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6011(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6065(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String CustAcctId = "";
        String FuncFlag = "";
        String AcctId = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + ThirdCustId + "&" + CustAcctId + "&" + AcctId + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6087(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String CustName = "";
        String IdType = "";
        String IdCode = "";
        String MobilePhone = "";
        String ProductCode = "";
        String Reserve = "";
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }
        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }
        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }
        if (parmaKeyDict.containsKey("MobilePhone")) {
            MobilePhone = (String) parmaKeyDict.get("MobilePhone");
        }
        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + CustName + "&" + IdType + "&" + IdCode + "&" + MobilePhone + "&" + ProductCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6155(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String CustName = "";
        String IdType = "";
        String IdCode = "";
        String DisableDate = "";
        String MobilePhone = "";
        String ProductCode = "";
        String TaNo = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }
        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }
        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }
        if (parmaKeyDict.containsKey("DisableDate")) {
            DisableDate = (String) parmaKeyDict.get("DisableDate");
        }
        if (parmaKeyDict.containsKey("MobilePhone")) {
            MobilePhone = (String) parmaKeyDict.get("MobilePhone");
        }
        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }
        if (parmaKeyDict.containsKey("TaNo")) {
            TaNo = (String) parmaKeyDict.get("TaNo");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + CustName + "&" + IdType + "&" + IdCode + "&" + DisableDate + "&" + MobilePhone + "&" + ProductCode + "&" + TaNo + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6156(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ClientNo = "";
        String CustName = "";
        String IdType = "";
        String IdCode = "";
        String TranAmount = "";
        String ProductCode = "";
        String AcctNo = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }
        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }
        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }
        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }
        if (parmaKeyDict.containsKey("AcctNo")) {
            AcctNo = (String) parmaKeyDict.get("AcctNo");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ClientNo + "&" + CustName + "&" + IdType + "&" + IdCode + "&" + TranAmount + "&" + ProductCode + "&" + AcctNo + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6157(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ClientNo = "";
        String TranAmount = "";
        String ProductCode = "";
        String TradeType = "";
        String RevCustAcctId = "";
        String HkAcctNo = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }
        if (parmaKeyDict.containsKey("TradeType")) {
            TradeType = (String) parmaKeyDict.get("TradeType");
        }
        if (parmaKeyDict.containsKey("RevCustAcctId")) {
            RevCustAcctId = (String) parmaKeyDict.get("RevCustAcctId");
        }
        if (parmaKeyDict.containsKey("HkAcctNo")) {
            HkAcctNo = (String) parmaKeyDict.get("HkAcctNo");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ClientNo + "&" + TranAmount + "&" + ProductCode + "&" + TradeType + "&" + RevCustAcctId + "&" + HkAcctNo + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6158(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ProductCode = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }
        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }
        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }
        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + ProductCode + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6159(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ClientNo = "";
        String IdType = "";
        String IdCode = "";
        String ProductCode = "";
        String AcctNo = "";
        String BecifNo = "";
        String SubBecifNo = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }
        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }
        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }
        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }
        if (parmaKeyDict.containsKey("AcctNo")) {
            AcctNo = (String) parmaKeyDict.get("AcctNo");
        }
        if (parmaKeyDict.containsKey("BecifNo")) {
            BecifNo = (String) parmaKeyDict.get("BecifNo");
        }
        if (parmaKeyDict.containsKey("SubBecifNo")) {
            SubBecifNo = (String) parmaKeyDict.get("SubBecifNo");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ClientNo + "&" + IdType + "&" + IdCode + "&" + ProductCode + "&" + AcctNo + "&" + BecifNo + "&" + SubBecifNo + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6160(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ClientNo = "";
        String ProductCode = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }
        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }
        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }
        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }
        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ClientNo + "&" + ProductCode + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6161(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String OrigThirdLogNo = "";
        String CustAcctId = "";
        String TranDate = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("OrigThirdLogNo")) {
            OrigThirdLogNo = (String) parmaKeyDict.get("OrigThirdLogNo");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("TranDate")) {
            TranDate = (String) parmaKeyDict.get("TranDate");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + OrigThirdLogNo + "&" + CustAcctId + "&" + TranDate + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6148(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ClientNo = "";
        String Answers = "";
        String IdType = "";
        String IdCode = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }
        if (parmaKeyDict.containsKey("Answers")) {
            Answers = (String) parmaKeyDict.get("Answers");
        }
        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }
        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }

        String tranMessageBody = SupAcctId + "&" + ClientNo + "&" + Answers + "&" + IdType + "&" + IdCode + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6149(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ClientNo = "";
        String IdType = "";
        String IdCode = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }
        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }
        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }

        String tranMessageBody = SupAcctId + "&" + ClientNo + "&" + IdType + "&" + IdCode + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6150(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CompanyName = "";
        String IdType = "";
        String IdCode = "";
        String DisableDate = "";
        String LicenceNo = "";
        String LicenceEnableDate = "";
        String LicenceDisableDate = "";
        String InstReprName = "";
        String InstReprCertType = "";
        String InstReprCertNo = "";
        String TransactorName = "";
        String TransactorCertType = "";
        String TransactorCertNo = "";
        String TransactorMobileNo = "";
        String TransactorEmail = "";
        String CorpAddress = "";
        String FileName = "";
        String AcctName = "";
        String CustAcctId = "";
        String AcctNo = "";
        String OpenBank = "";
        String OpenBankName = "";
        String ArchivePath = "";
        String ProductCode = "";
        String TaNo = "";
        String BecifNo = "";
        String Remark = "";
        String Desc = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("CompanyName")) {
            CompanyName = (String) parmaKeyDict.get("CompanyName");
        }
        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }
        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }
        if (parmaKeyDict.containsKey("DisableDate")) {
            DisableDate = (String) parmaKeyDict.get("DisableDate");
        }
        if (parmaKeyDict.containsKey("LicenceNo")) {
            LicenceNo = (String) parmaKeyDict.get("LicenceNo");
        }
        if (parmaKeyDict.containsKey("LicenceEnableDate")) {
            LicenceEnableDate = (String) parmaKeyDict.get("LicenceEnableDate");
        }
        if (parmaKeyDict.containsKey("LicenceDisableDate")) {
            LicenceDisableDate = (String) parmaKeyDict.get("LicenceDisableDate");
        }
        if (parmaKeyDict.containsKey("InstReprName")) {
            InstReprName = (String) parmaKeyDict.get("InstReprName");
        }
        if (parmaKeyDict.containsKey("InstReprCertType")) {
            InstReprCertType = (String) parmaKeyDict.get("InstReprCertType");
        }
        if (parmaKeyDict.containsKey("InstReprCertNo")) {
            InstReprCertNo = (String) parmaKeyDict.get("InstReprCertNo");
        }
        if (parmaKeyDict.containsKey("TransactorName")) {
            TransactorName = (String) parmaKeyDict.get("TransactorName");
        }
        if (parmaKeyDict.containsKey("TransactorCertType")) {
            TransactorCertType = (String) parmaKeyDict.get("TransactorCertType");
        }
        if (parmaKeyDict.containsKey("TransactorCertNo")) {
            TransactorCertNo = (String) parmaKeyDict.get("TransactorCertNo");
        }
        if (parmaKeyDict.containsKey("TransactorMobileNo")) {
            TransactorMobileNo = (String) parmaKeyDict.get("TransactorMobileNo");
        }
        if (parmaKeyDict.containsKey("TransactorEmail")) {
            TransactorEmail = (String) parmaKeyDict.get("TransactorEmail");
        }
        if (parmaKeyDict.containsKey("CorpAddress")) {
            CorpAddress = (String) parmaKeyDict.get("CorpAddress");
        }
        if (parmaKeyDict.containsKey("FileName")) {
            FileName = (String) parmaKeyDict.get("FileName");
        }
        if (parmaKeyDict.containsKey("AcctName")) {
            AcctName = (String) parmaKeyDict.get("AcctName");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("AcctNo")) {
            AcctNo = (String) parmaKeyDict.get("AcctNo");
        }
        if (parmaKeyDict.containsKey("OpenBank")) {
            OpenBank = (String) parmaKeyDict.get("OpenBank");
        }
        if (parmaKeyDict.containsKey("OpenBankName")) {
            OpenBankName = (String) parmaKeyDict.get("OpenBankName");
        }
        if (parmaKeyDict.containsKey("ArchivePath")) {
            ArchivePath = (String) parmaKeyDict.get("ArchivePath");
        }
        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }
        if (parmaKeyDict.containsKey("TaNo")) {
            TaNo = (String) parmaKeyDict.get("TaNo");
        }
        if (parmaKeyDict.containsKey("BecifNo")) {
            BecifNo = (String) parmaKeyDict.get("BecifNo");
        }
        if (parmaKeyDict.containsKey("Remark")) {
            Remark = (String) parmaKeyDict.get("Remark");
        }
        if (parmaKeyDict.containsKey("Desc")) {
            Desc = (String) parmaKeyDict.get("Desc");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CompanyName + "&" + IdType + "&" + IdCode + "&" + DisableDate + "&" + LicenceNo + "&" + LicenceEnableDate + "&" + LicenceDisableDate + "&" + InstReprName + "&" + InstReprCertType + "&" + InstReprCertNo + "&"
                + TransactorName + "&" + TransactorCertType + "&" + TransactorCertNo + "&" + TransactorMobileNo + "&" + TransactorEmail + "&" + CorpAddress + "&" + FileName + "&" + AcctName + "&" + CustAcctId + "&" + AcctNo + "&"
                + OpenBank + "&" + OpenBankName + "&" + ArchivePath + "&" + ProductCode + "&" + TaNo + "&" + BecifNo + "&" + Remark + "&" + Desc + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6151(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustName = "";
        String IdType = "";
        String IdCode = "";
        String TaNo = "";
        String AcctNo = "";
        String BecifNo = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }
        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }
        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }
        if (parmaKeyDict.containsKey("TaNo")) {
            TaNo = (String) parmaKeyDict.get("TaNo");
        }
        if (parmaKeyDict.containsKey("AcctNo")) {
            AcctNo = (String) parmaKeyDict.get("AcctNo");
        }
        if (parmaKeyDict.containsKey("BecifNo")) {
            BecifNo = (String) parmaKeyDict.get("BecifNo");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustName + "&" + IdType + "&" + IdCode + "&" + TaNo + "&" + AcctNo + "&" + BecifNo + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6152(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ClientNo = "";
        String TranAmount = "";
        String ProductCode = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + ClientNo + "&" + TranAmount + "&" + ProductCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6153(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String TranDate = "";
        String BecifNo = "";
        String SubBecifNo = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("TranDate")) {
            TranDate = (String) parmaKeyDict.get("TranDate");
        }
        if (parmaKeyDict.containsKey("BecifNo")) {
            BecifNo = (String) parmaKeyDict.get("BecifNo");
        }
        if (parmaKeyDict.containsKey("SubBecifNo")) {
            SubBecifNo = (String) parmaKeyDict.get("SubBecifNo");
        }
        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + TranDate + "&" + BecifNo + "&" + SubBecifNo + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6154(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String TranDate = "";
        String BecifNo = "";
        String SubBecifNo = "";
        String BatchNo = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("TranDate")) {
            TranDate = (String) parmaKeyDict.get("TranDate");
        }
        if (parmaKeyDict.containsKey("BecifNo")) {
            BecifNo = (String) parmaKeyDict.get("BecifNo");
        }
        if (parmaKeyDict.containsKey("SubBecifNo")) {
            SubBecifNo = (String) parmaKeyDict.get("SubBecifNo");
        }
        if (parmaKeyDict.containsKey("BatchNo")) {
            BatchNo = (String) parmaKeyDict.get("BatchNo");
        }
        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + TranDate + "&" + BecifNo + "&" + SubBecifNo + "&" + BatchNo + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }


    private String getTranMessageBody_6088(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String CustName = "";
        String IdType = "";
        String IdCode = "";
        String AcctId = "";
        String BankType = "";
        String BankName = "";
        String BankCode = "";
        String SBankCode = "";
        String MobilePhone = "";
        String CustProperty = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }
        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }
        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }
        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }
        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }
        if (parmaKeyDict.containsKey("BankType")) {
            BankType = (String) parmaKeyDict.get("BankType");
        }
        if (parmaKeyDict.containsKey("BankName")) {
            BankName = (String) parmaKeyDict.get("BankName");
        }
        if (parmaKeyDict.containsKey("BankCode")) {
            BankCode = (String) parmaKeyDict.get("BankCode");
        }
        if (parmaKeyDict.containsKey("SBankCode")) {
            SBankCode = (String) parmaKeyDict.get("SBankCode");
        }
        if (parmaKeyDict.containsKey("MobilePhone")) {
            MobilePhone = (String) parmaKeyDict.get("MobilePhone");
        }
        if (parmaKeyDict.containsKey("CustProperty")) {
            CustProperty = (String) parmaKeyDict.get("CustProperty");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }


        String tranMessageBody = SupAcctId + "&" + ThirdCustId + "&" + CustName + "&" + IdType + "&" + IdCode + "&" + AcctId + "&" + BankType + "&" + BankName + "&" + BankCode + "&" + SBankCode + "&" + MobilePhone + "&" + CustProperty + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6097(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String AcctId = "";
        String MessageCode = "";
        String Reserve = "";
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }
        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }
        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + ThirdCustId + "&" + AcctId + "&" + MessageCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6100(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ClientNo = "";
        String TranAmount = "";
        String ProductCode = "";
        String TradeType = "";
        String RevCustAcctId = "";
        String Reserve = "";
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }
        if (parmaKeyDict.containsKey("TradeType")) {
            TradeType = (String) parmaKeyDict.get("TradeType");
        }
        if (parmaKeyDict.containsKey("RevCustAcctId")) {
            RevCustAcctId = (String) parmaKeyDict.get("RevCustAcctId");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ClientNo + "&" + TranAmount + "&" + ProductCode + "&" + TradeType + "&" + RevCustAcctId + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6068(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String CustName = "";
        String IdType = "";
        String IdCode = "";
        String TranAmount = "";
        String ProductCode = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }

        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }

        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + CustName + "&" + IdType + "&" + IdCode + "&" + TranAmount + "&" + ProductCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6069(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ClientNo = "";
        String TranAmount = "";
        String ProductCode = "";
        String TradeType = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("TradeType")) {
            TradeType = (String) parmaKeyDict.get("TradeType");
        }

        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ClientNo + "&" + TranAmount + "&" + ProductCode + "&" + TradeType + "&" + Reserve + "&";

        return tranMessageBody;
    }


    private String getTranMessageBody_6040(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ProductCode = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }

        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }

        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + ProductCode + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6043(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ClientNo = "";
        String ProductCode = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }

        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ClientNo + "&" + ProductCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6044(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ClientNo = "";
        String ProductCode = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }

        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }

        if (parmaKeyDict.containsKey("ProductCode")) {
            ProductCode = (String) parmaKeyDict.get("ProductCode");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("ClientNo")) {
            ClientNo = (String) parmaKeyDict.get("ClientNo");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ClientNo + "&" + ProductCode + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6041(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String FuncFlag = "";
        String SelectFlag = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }

        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("SelectFlag")) {
            SelectFlag = (String) parmaKeyDict.get("SelectFlag");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + CustAcctId + "&" + SelectFlag + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6042(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String OrigThirdLogNo = "";
        String TranDate = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OrigThirdLogNo")) {
            OrigThirdLogNo = (String) parmaKeyDict.get("OrigThirdLogNo");
        }

        if (parmaKeyDict.containsKey("TranDate")) {
            TranDate = (String) parmaKeyDict.get("TranDate");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + OrigThirdLogNo + "&" + TranDate + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6079(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String CustAcctId = "";
        String SelectFlag = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String RecordMax = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("SelectFlag")) {
            SelectFlag = (String) parmaKeyDict.get("SelectFlag");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }
        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("RecordMax")) {
            RecordMax = (String) parmaKeyDict.get("RecordMax");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + SelectFlag + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + RecordMax + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6080(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String CustAcctId = "";
        String SelectFlag = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String RecordMax = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("SelectFlag")) {
            SelectFlag = (String) parmaKeyDict.get("SelectFlag");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }
        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("RecordMax")) {
            RecordMax = (String) parmaKeyDict.get("RecordMax");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + SelectFlag + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + RecordMax + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6082(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String CustAcctId = "";
        String TranType = "";
        String TranAmount = "";
        String AcctId = "";
        String ThirdHtId = "";
        String TranNote = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("TranType")) {
            TranType = (String) parmaKeyDict.get("TranType");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TranNote")) {
            TranNote = (String) parmaKeyDict.get("TranNote");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + ThirdCustId + "&" + CustAcctId + "&" + TranType + "&" + TranAmount + "&" + AcctId + "&" + ThirdHtId + "&" + TranNote + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6083(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String CustAcctId = "";
        String ModifiedType = "";
        String NewMobilePhone = "";
        String AcctId = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ModifiedType")) {
            ModifiedType = (String) parmaKeyDict.get("ModifiedType");
        }

        if (parmaKeyDict.containsKey("NewMobilePhone")) {
            NewMobilePhone = (String) parmaKeyDict.get("NewMobilePhone");
        }

        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + ThirdCustId + "&" + CustAcctId + "&" + ModifiedType + "&" + NewMobilePhone + "&" + AcctId + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6084(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String CustAcctId = "";
        String ModifiedType = "";
        String SerialNo = "";
        String MessageCode = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ModifiedType")) {
            ModifiedType = (String) parmaKeyDict.get("ModifiedType");
        }

        if (parmaKeyDict.containsKey("SerialNo")) {
            SerialNo = (String) parmaKeyDict.get("SerialNo");
        }

        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + ThirdCustId + "&" + CustAcctId + "&" + ModifiedType + "&" + SerialNo + "&" + MessageCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6085(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String CustName = "";
        String OutAcctId = "";
        String OutAcctIdName = "";
        String CcyCode = "RMB";
        String TranAmount = "";
        String HandFee = "";
        String SerialNo = "";
        String MessageCode = "";
        String Note = "";
        String Reserve = "";
        String WebSign = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }
        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }

        if (parmaKeyDict.containsKey("OutAcctId")) {
            OutAcctId = (String) parmaKeyDict.get("OutAcctId");
        }
        if (parmaKeyDict.containsKey("OutAcctIdName")) {
            OutAcctIdName = (String) parmaKeyDict.get("OutAcctIdName");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("SerialNo")) {
            SerialNo = (String) parmaKeyDict.get("SerialNo");
        }

        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        if (parmaKeyDict.containsKey("WebSign")) {
            WebSign = (String) parmaKeyDict.get("WebSign");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ThirdCustId + "&" + CustName + "&" + OutAcctId + "&" + OutAcctIdName + "&" + CcyCode + "&" + TranAmount + "&"
                + HandFee + "&" + SerialNo + "&" + MessageCode + "&" + Note + "&" + Reserve + "&" + WebSign + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6098(HashMap parmaKeyDict) {
        String SelectFlag = "";
        String SupAcctId = "";
        String CustAcctId = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("SelectFlag")) {
            SelectFlag = (String) parmaKeyDict.get("SelectFlag");
        }
        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SelectFlag + "&" + SupAcctId + "&" + CustAcctId + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6092(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String ThirdCustId = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + ThirdCustId + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6093(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + Reserve + "&";

        return tranMessageBody;
    }


    private String getTranMessageBody_6099(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6118(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }

        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6119(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();
        String FuncFlag = "";
        String SupAcctId = "";
        String OutCustAcctId = "";
        String OutCustName = "";
        String InCustAcctId = "";
        String InCustName = "";
        String MidCustAcctId = "";
        String MidCustName = "";
        String Amount = "";
        String TranFee = "";
        String TranType = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String ThirdHtMsg = "";
        String Note = "";
        String Reserve = "";
        String WebSign = "";

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }
        if (parmaKeyDict.containsKey("OutCustName")) {
            OutCustName = (String) parmaKeyDict.get("OutCustName");
        }
        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String) parmaKeyDict.get("InCustAcctId");
        }
        if (parmaKeyDict.containsKey("InCustName")) {
            InCustName = (String) parmaKeyDict.get("InCustName");
        }
        if (parmaKeyDict.containsKey("MidCustAcctId")) {
            MidCustAcctId = (String) parmaKeyDict.get("MidCustAcctId");
        }
        if (parmaKeyDict.containsKey("MidCustName")) {
            MidCustName = (String) parmaKeyDict.get("MidCustName");
        }
        if (parmaKeyDict.containsKey("Amount")) {
            Amount = (String) parmaKeyDict.get("Amount");
        }
        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String) parmaKeyDict.get("TranFee");
        }
        if (parmaKeyDict.containsKey("TranType")) {
            TranType = (String) parmaKeyDict.get("TranType");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }
        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String) parmaKeyDict.get("ThirdHtMsg");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        if (parmaKeyDict.containsKey("WebSign")) {
            WebSign = (String) parmaKeyDict.get("WebSign");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + OutCustAcctId + "&" + OutCustName + "&" + InCustAcctId + "&" + InCustName + "&" + MidCustAcctId + "&" + MidCustName + "&" + Amount + "&" + TranFee + "&" + TranType + "&" + CcyCode + "&" + ThirdHtId + "&" + ThirdHtMsg + "&" + Note + "&" + Reserve + "&" + WebSign + "&";

        return tranMessageBody;

    }

    private String getTranMessageBody_6103(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FileType = "";
        String TranDate = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FileType")) {
            FileType = (String) parmaKeyDict.get("FileType");
        }

        if (parmaKeyDict.containsKey("TranDate")) {
            TranDate = (String) parmaKeyDict.get("TranDate");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + FileType + "&" + TranDate + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6108(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }

        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6109(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }

        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6114(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String BeginDate = "";
        String EndDate = "";
        String PageNum = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("BeginDate")) {
            BeginDate = (String) parmaKeyDict.get("BeginDate");
        }

        if (parmaKeyDict.containsKey("EndDate")) {
            EndDate = (String) parmaKeyDict.get("EndDate");
        }

        if (parmaKeyDict.containsKey("PageNum")) {
            PageNum = (String) parmaKeyDict.get("PageNum");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + BeginDate + "&" + EndDate + "&" + PageNum + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6110(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String OrigThirdLogNo = "";
        String TranDate = "";
        String FuncFlag = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("OrigThirdLogNo")) {
            OrigThirdLogNo = (String) parmaKeyDict.get("OrigThirdLogNo");
        }

        if (parmaKeyDict.containsKey("TranDate")) {
            TranDate = (String) parmaKeyDict.get("TranDate");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + FuncFlag + "&" + OrigThirdLogNo + "&" + CustAcctId + "&" + TranDate + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6090(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String FileName = "";
        String Reserve = "";
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }
        if (parmaKeyDict.containsKey("FileName")) {
            FileName = (String) parmaKeyDict.get("FileName");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + FuncFlag + "&" + FileName + "&" + Reserve + "&";

        return tranMessageBody;

    }

    private String getTranMessageBody_6091(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String FuncFlag = "";
        String FileName = "";
        String TranDate = "";
        String Reserve = "";
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }
        if (parmaKeyDict.containsKey("FileName")) {
            FileName = (String) parmaKeyDict.get("FileName");
        }
        if (parmaKeyDict.containsKey("TranDate")) {
            TranDate = (String) parmaKeyDict.get("TranDate");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        String tranMessageBody = SupAcctId + "&" + FuncFlag + "&" + FileName + "&" + TranDate + "&" + Reserve + "&";

        return tranMessageBody;

    }

    private String getTranMessageBody_6070(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();

        String SupAcctId = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String TotalCount = "";
        String Note = "";
        String Reserve = "";
        String array = "";
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TotalCount")) {
            TotalCount = (String) parmaKeyDict.get("TotalCount");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        int iCount = Integer.parseInt(TotalCount);

        for (int i = 0; i < iCount; i++) {
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
        }

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }

        String tranMessageBody = SupAcctId + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + TotalCount + "&" + array + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6136(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();

        String SupAcctId = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String TotalCount = "";
        String Note = "";
        String Reserve = "";
        String WebSign = "";
        String array = "";
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TotalCount")) {
            TotalCount = (String) parmaKeyDict.get("TotalCount");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        if (parmaKeyDict.containsKey("WebSign")) {
            WebSign = (String) parmaKeyDict.get("WebSign");
        }
        int iCount = Integer.parseInt(TotalCount);

        for (int i = 0; i < iCount; i++) {
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
        }

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }

        String tranMessageBody = SupAcctId + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + TotalCount + "&" + array + "&" + Note + "&" + Reserve + "&" + WebSign + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6165(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();

        String SupAcctId = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String TotalCount = "";
        String Note = "";
        String Reserve = "";
        String WebSign = "";
        String array = "";
        String[] MarketLogNo = null;
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TotalCount")) {
            TotalCount = (String) parmaKeyDict.get("TotalCount");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("MarketLogNo")) {
            MarketLogNo = (String[]) parmaKeyDict.get("MarketLogNo");
        }
        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }
        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        if (parmaKeyDict.containsKey("WebSign")) {
            WebSign = (String) parmaKeyDict.get("WebSign");
        }
        int iCount = Integer.parseInt(TotalCount);

        for (int i = 0; i < iCount; i++) {
            list.add(MarketLogNo[i]);
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
        }

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }

        String tranMessageBody = SupAcctId + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + TotalCount + "&" + array + "&" + Note + "&" + Reserve + "&" + WebSign + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6137(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();

        String SupAcctId = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String TotalCount = "";
        String Note = "";
        String SerialNo = "";
        String MessageCode = "";
        String Reserve = "";
        String array = "";
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TotalCount")) {
            TotalCount = (String) parmaKeyDict.get("TotalCount");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("SerialNo")) {
            SerialNo = (String) parmaKeyDict.get("SerialNo");
        }

        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        int iCount = Integer.parseInt(TotalCount);

        for (int i = 0; i < iCount; i++) {
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
        }

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }

        String tranMessageBody = SupAcctId + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + TotalCount + "&" + array + "&" + Note + "&" + SerialNo + "&" + MessageCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6138(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();

        String SupAcctId = "";
        String CustAcctId = "";
        String AcctId = "";
        String BankName = "";
        String BankCode = "";
        String SBankCode = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("AcctId")) {
            AcctId = (String) parmaKeyDict.get("AcctId");
        }

        if (parmaKeyDict.containsKey("BankName")) {
            BankName = (String) parmaKeyDict.get("BankName");
        }

        if (parmaKeyDict.containsKey("BankCode")) {
            BankCode = (String) parmaKeyDict.get("BankCode");
        }

        if (parmaKeyDict.containsKey("SBankCode")) {
            SBankCode = (String) parmaKeyDict.get("SBankCode");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + AcctId + "&" + BankName + "&" + BankCode + "&" + SBankCode + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6142(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String OldFrontlogno = "";
        String OldTranType = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OldFrontlogno")) {
            OldFrontlogno = (String) parmaKeyDict.get("OldFrontlogno");
        }

        if (parmaKeyDict.containsKey("OldTranType")) {
            OldTranType = (String) parmaKeyDict.get("OldTranType");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + OldFrontlogno + "&" + OldTranType + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6145(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String AcqChannelType = "";
        String ThirdHtId = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String CustName = "";
        String TranAmount = "";
        String CcyCode = "";
        String Note = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("AcqChannelType")) {
            AcqChannelType = (String) parmaKeyDict.get("AcqChannelType");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }
        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + AcqChannelType + "&" + ThirdHtId + "&" + CustAcctId + "&" + ThirdCustId + "&" + CustName + "&" + TranAmount + "&" + CcyCode + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6146(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String AcqChannelType = "";
        String ThirdHtId = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("AcqChannelType")) {
            AcqChannelType = (String) parmaKeyDict.get("AcqChannelType");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + AcqChannelType + "&" + ThirdHtId + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6162(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String CustProperty = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("CustProperty")) {
            CustProperty = (String) parmaKeyDict.get("CustProperty");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + CustProperty + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6147(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String AcqChannelType = "";
        String ThirdHtId = "";
        String TranAmount = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("AcqChannelType")) {
            AcqChannelType = (String) parmaKeyDict.get("AcqChannelType");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + AcqChannelType + "&" + ThirdHtId + "&" + TranAmount + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6164(HashMap parmaKeyDict) {
        String FuncFlag = "";
        String SupAcctId = "";
        String OldThirdLogNo = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String InCustAcctId = "";
        String InThirdCustId = "";
        String ThirdHtId = "";
        String TranAmount = "";
        String TranFee = "";
        String Note = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OldThirdLogNo")) {
            OldThirdLogNo = (String) parmaKeyDict.get("OldThirdLogNo");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }
        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }
        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String) parmaKeyDict.get("InCustAcctId");
        }
        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String) parmaKeyDict.get("InThirdCustId");
        }
        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String) parmaKeyDict.get("TranFee");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + OldThirdLogNo + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + InCustAcctId + "&" + InThirdCustId + "&" + ThirdHtId + "&" + TranAmount + "&" + TranFee + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6163(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();

        String SupAcctId = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String TotalCount = "";
        String Note = "";
        String Reserve = "";
        String array = "";
        String[] MarketLogNo = null;
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TotalCount")) {
            TotalCount = (String) parmaKeyDict.get("TotalCount");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("MarketLogNo")) {
            MarketLogNo = (String[]) parmaKeyDict.get("MarketLogNo");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        int iCount = Integer.parseInt(TotalCount);

        for (int i = 0; i < iCount; i++) {
            list.add(MarketLogNo[i]);
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
        }

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }

        String tranMessageBody = SupAcctId + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + TotalCount + "&" + array + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6166(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();

        String SupAcctId = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String TotalCount = "";
        String Note = "";
        String Reserve = "";
        String SerialNo = "";
        String MessageCode = "";
        String array = "";
        String[] MarketLogNo = null;
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TotalCount")) {
            TotalCount = (String) parmaKeyDict.get("TotalCount");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("MarketLogNo")) {
            MarketLogNo = (String[]) parmaKeyDict.get("MarketLogNo");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        if (parmaKeyDict.containsKey("SerialNo")) {
            SerialNo = (String) parmaKeyDict.get("SerialNo");
        }
        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }

        int iCount = Integer.parseInt(TotalCount);

        for (int i = 0; i < iCount; i++) {
            list.add(MarketLogNo[i]);
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
        }

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }

        String tranMessageBody = SupAcctId + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + TotalCount + "&" + array + "&" + Note + "&" + Reserve + "&" + SerialNo + "&" + MessageCode + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6201(HashMap parmaKeyDict) {
        String FuncFlag = "";
        String SupAcctId = "";
        String BatFileName = "";
        String Password = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("BatFileName")) {
            BatFileName = (String) parmaKeyDict.get("BatFileName");
        }

        if (parmaKeyDict.containsKey("Password")) {
            Password = (String) parmaKeyDict.get("Password");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + SupAcctId + "&" + BatFileName + "&" + Password + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6202(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String BatFileName = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("BatFileName")) {
            BatFileName = (String) parmaKeyDict.get("BatFileName");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + BatFileName + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getSignMessageBody_6005(HashMap parmaKeyDict) {
        String TranWebName = "";
        String CustAcctId = "";
        String IdType = "";
        String IdCode = "";
        String ThirdCustId = "";
        String CustName = "";
        String SupAcctId = "";
        String OutAcctId = "";
        String OutAcctIdName = "";
        String CcyCode = "";
        String TranAmount = "";
        String Note = "";
        String Reserve = "";


        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }

        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        if (parmaKeyDict.containsKey("TranWebName")) {
            TranWebName = (String) parmaKeyDict.get("TranWebName");
        }

        if (parmaKeyDict.containsKey("IdType")) {
            IdType = (String) parmaKeyDict.get("IdType");
        }

        if (parmaKeyDict.containsKey("IdCode")) {
            IdCode = (String) parmaKeyDict.get("IdCode");
        }

        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }
        if (parmaKeyDict.containsKey("OutAcctIdName")) {
            OutAcctIdName = (String) parmaKeyDict.get("OutAcctIdName");
        }
        if (parmaKeyDict.containsKey("OutAcctId")) {
            OutAcctId = (String) parmaKeyDict.get("OutAcctId");
        }

        String tranMessageBody = TranWebName + "&" + CustAcctId + "&" + IdType + "&" + IdCode + "&" + ThirdCustId + "&" + CustName + "&" + SupAcctId + "&" + OutAcctId + "&" + OutAcctIdName + "&" + CcyCode + "&" + TranAmount + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getSignMessageBody_6006(HashMap parmaKeyDict) {
        String FuncFlag = "";
        String OutCustAcctId = "";
        String SupAcctId = "";
        String OutThirdCustId = "";
        String OutCustName = "";
        String InCustAcctId = "";
        String InThirdCustId = "";
        String InCustName = "";
        String TranAmount = "";
        String TranFee = "";
        String TranType = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String ThirdHtMsg = "";
        String Note = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustName")) {
            OutCustName = (String) parmaKeyDict.get("OutCustName");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String) parmaKeyDict.get("InThirdCustId");
        }

        if (parmaKeyDict.containsKey("InCustName")) {
            InCustName = (String) parmaKeyDict.get("InCustName");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String) parmaKeyDict.get("TranFee");
        }
        if (parmaKeyDict.containsKey("TranType")) {
            TranType = (String) parmaKeyDict.get("TranType");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }
        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String) parmaKeyDict.get("ThirdHtMsg");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }


        String tranMessageBody = FuncFlag + "&" + OutCustAcctId + "&" + SupAcctId + "&" + OutThirdCustId + "&" + OutCustName + "&" + InCustAcctId + "&" + InThirdCustId + "&" + InCustName + "&" + TranAmount + "&" + TranFee + "&" + TranType + "&" + CcyCode + "&" + ThirdHtId + "&" + ThirdHtMsg + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6101(HashMap parmaKeyDict) {
        String FuncFlag = "";
        String OutCustAcctId = "";
        String SupAcctId = "";
        String OutThirdCustId = "";
        String OutCustName = "";
        String InCustAcctId = "";
        String InThirdCustId = "";
        String InCustName = "";
        String TranAmount = "";
        String TranFee = "";
        String TranType = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String ThirdHtMsg = "";
        String Note = "";
        String SerialNo = "";
        String MessageCode = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }
        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }
        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }
        if (parmaKeyDict.containsKey("OutCustName")) {
            OutCustName = (String) parmaKeyDict.get("OutCustName");
        }
        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String) parmaKeyDict.get("InCustAcctId");
        }
        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String) parmaKeyDict.get("InThirdCustId");
        }
        if (parmaKeyDict.containsKey("InCustName")) {
            InCustName = (String) parmaKeyDict.get("InCustName");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String) parmaKeyDict.get("TranFee");
        }
        if (parmaKeyDict.containsKey("TranType")) {
            TranType = (String) parmaKeyDict.get("TranType");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }
        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String) parmaKeyDict.get("ThirdHtMsg");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("SerialNo")) {
            SerialNo = (String) parmaKeyDict.get("SerialNo");
        }
        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = FuncFlag + "&" + OutCustAcctId + "&" + SupAcctId + "&" + OutThirdCustId + "&" + OutCustName + "&" + InCustAcctId + "&" + InThirdCustId + "&" + InCustName + "&" + TranAmount + "&" + TranFee + "&" + TranType + "&" + CcyCode + "&" + ThirdHtId + "&" + ThirdHtMsg + "&" + Note + "&" + SerialNo + "&" + MessageCode + "&" + Reserve + "&";

        return tranMessageBody;

    }

    private String getTranMessageBody_6102(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }
        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getSignMessageBody_6111(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String CustName = "";
        String OutAcctId = "";
        String OutAcctIdName = "";
        String TranAmount = "";
        String HandFee = "";
        String CcyCode = "";
        String Note = "";
        String Reserve = "";
        String SerialNo = "";
        String MessageCode = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }

        if (parmaKeyDict.containsKey("OutAcctId")) {
            OutAcctId = (String) parmaKeyDict.get("OutAcctId");
        }

        if (parmaKeyDict.containsKey("OutAcctIdName")) {
            OutAcctIdName = (String) parmaKeyDict.get("OutAcctIdName");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("SerialNo")) {
            SerialNo = (String) parmaKeyDict.get("SerialNo");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }


        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ThirdCustId + "&" + CustName + "&" + OutAcctId + "&" + OutAcctIdName + "&" + CcyCode + "&" + TranAmount + "&" + HandFee + "&" + SerialNo + "&" + MessageCode + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getSignMessageBody_6134(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String FuncFlag = "";
        String ThirdCustId = "";
        String TranAmount = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String ThirdHtMsg = "";
        String Note = "";
        String Reserve = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String) parmaKeyDict.get("ThirdHtMsg");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + FuncFlag + "&" + ThirdCustId + "&" + TranAmount + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + ThirdHtMsg + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getSignMessageBody_6136(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();
        String SupAcctId = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String TotalCount = "";
        String Note = "";
        String Reserve = "";
        String array = "";
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TotalCount")) {
            TotalCount = (String) parmaKeyDict.get("TotalCount");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        int iCount = Integer.parseInt(TotalCount);

        for (int i = 0; i < iCount; i++) {
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
        }

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }

        String tranMessageBody = SupAcctId + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + TotalCount + "&" + array + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getSignMessageBody_6165(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();
        String SupAcctId = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String HandFee = "";
        String CcyCode = "";
        String ThirdHtId = "";
        String TotalCount = "";
        String Note = "";
        String Reserve = "";
        String array = "";
        String[] MarketLogNo = null;
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }

        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String) parmaKeyDict.get("ThirdHtId");
        }

        if (parmaKeyDict.containsKey("TotalCount")) {
            TotalCount = (String) parmaKeyDict.get("TotalCount");
        }

        if (parmaKeyDict.containsKey("MarketLogNo")) {
            MarketLogNo = (String[]) parmaKeyDict.get("MarketLogNo");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }
        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        int iCount = Integer.parseInt(TotalCount);

        for (int i = 0; i < iCount; i++) {
            list.add(MarketLogNo[i]);
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
        }

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }

        String tranMessageBody = SupAcctId + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + HandFee + "&" + CcyCode + "&" + ThirdHtId + "&" + TotalCount + "&" + array + "&" + Note + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getSignMessageBody_6120(HashMap parmaKeyDict) {
        LinkedList<String> list = new LinkedList<String>();
        String FuncFlag = "";
        String OutCustAcctId = "";
        String OutThirdCustId = "";
        String SupAcctId = "";
        String ThirdHtCount = "";
        String Reserve = "";
        String array = "";
        String[] InCustAcctId = null;
        String[] InThirdCustId = null;
        String[] TranAmount = null;
        String[] TranFee = null;
        String[] CcyCode = null;
        String[] ThirdHtId = null;
        String[] ThirdHtMsg = null;
        String[] Note = null;
        String[] MarketLogNo = null;

        if (parmaKeyDict.containsKey("FuncFlag")) {
            FuncFlag = (String) parmaKeyDict.get("FuncFlag");
        }

        if (parmaKeyDict.containsKey("OutCustAcctId")) {
            OutCustAcctId = (String) parmaKeyDict.get("OutCustAcctId");
        }

        if (parmaKeyDict.containsKey("OutThirdCustId")) {
            OutThirdCustId = (String) parmaKeyDict.get("OutThirdCustId");
        }

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdHtCount")) {
            ThirdHtCount = (String) parmaKeyDict.get("ThirdHtCount");
        }

        if (parmaKeyDict.containsKey("InCustAcctId")) {
            InCustAcctId = (String[]) parmaKeyDict.get("InCustAcctId");
        }

        if (parmaKeyDict.containsKey("InThirdCustId")) {
            InThirdCustId = (String[]) parmaKeyDict.get("InThirdCustId");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String[]) parmaKeyDict.get("TranAmount");
        }
        if (parmaKeyDict.containsKey("TranFee")) {
            TranFee = (String[]) parmaKeyDict.get("TranFee");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String[]) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("ThirdHtId")) {
            ThirdHtId = (String[]) parmaKeyDict.get("ThirdHtId");
        }
        if (parmaKeyDict.containsKey("ThirdHtMsg")) {
            ThirdHtMsg = (String[]) parmaKeyDict.get("ThirdHtMsg");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String[]) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("MarketLogNo")) {
            MarketLogNo = (String[]) parmaKeyDict.get("MarketLogNo");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }

        int iCount = Integer.parseInt(ThirdHtCount);

        for (int i = 0; i < iCount; i++) {
            list.add(InCustAcctId[i]);
            list.add(InThirdCustId[i]);
            list.add(TranAmount[i]);
            list.add(TranFee[i]);
            list.add(CcyCode[i]);
            list.add(ThirdHtId[i]);
            list.add(ThirdHtMsg[i]);
            list.add(Note[i]);
            list.add(MarketLogNo[i]);
        }
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            array = array + it.next() + "&";
        }

        String tranMessageBody = FuncFlag + "&" + OutCustAcctId + "&" + OutThirdCustId + "&" + SupAcctId + "&" + ThirdHtCount + "&" + array + "&" + Reserve + "&";

        return tranMessageBody;
    }

    private String getTranMessageBody_6111(HashMap parmaKeyDict) {
        String SupAcctId = "";
        String CustAcctId = "";
        String ThirdCustId = "";
        String CustName = "";
        String OutAcctId = "";
        String OutAcctIdName = "";
        String TranAmount = "";
        String HandFee = "";
        String CcyCode = "";
        String Note = "";
        String Reserve = "";
        String WebSign = "";
        String SerialNo = "";
        String MessageCode = "";

        if (parmaKeyDict.containsKey("SupAcctId")) {
            SupAcctId = (String) parmaKeyDict.get("SupAcctId");
        }

        if (parmaKeyDict.containsKey("CustAcctId")) {
            CustAcctId = (String) parmaKeyDict.get("CustAcctId");
        }

        if (parmaKeyDict.containsKey("ThirdCustId")) {
            ThirdCustId = (String) parmaKeyDict.get("ThirdCustId");
        }

        if (parmaKeyDict.containsKey("CustName")) {
            CustName = (String) parmaKeyDict.get("CustName");
        }

        if (parmaKeyDict.containsKey("OutAcctId")) {
            OutAcctId = (String) parmaKeyDict.get("OutAcctId");
        }

        if (parmaKeyDict.containsKey("OutAcctIdName")) {
            OutAcctIdName = (String) parmaKeyDict.get("OutAcctIdName");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }

        if (parmaKeyDict.containsKey("TranAmount")) {
            TranAmount = (String) parmaKeyDict.get("TranAmount");
        }

        if (parmaKeyDict.containsKey("HandFee")) {
            HandFee = (String) parmaKeyDict.get("HandFee");
        }
        if (parmaKeyDict.containsKey("SerialNo")) {
            SerialNo = (String) parmaKeyDict.get("SerialNo");
        }
        if (parmaKeyDict.containsKey("CcyCode")) {
            CcyCode = (String) parmaKeyDict.get("CcyCode");
        }
        if (parmaKeyDict.containsKey("MessageCode")) {
            MessageCode = (String) parmaKeyDict.get("MessageCode");
        }
        if (parmaKeyDict.containsKey("Note")) {
            Note = (String) parmaKeyDict.get("Note");
        }
        if (parmaKeyDict.containsKey("Reserve")) {
            Reserve = (String) parmaKeyDict.get("Reserve");
        }
        if (parmaKeyDict.containsKey("WebSign")) {
            WebSign = (String) parmaKeyDict.get("WebSign");
        }


        String tranMessageBody = SupAcctId + "&" + CustAcctId + "&" + ThirdCustId + "&" + CustName + "&" + OutAcctId + "&" + OutAcctIdName + "&" + CcyCode + "&" + TranAmount + "&" + HandFee + "&" + SerialNo + "&" + MessageCode + "&" + Note + "&" + Reserve + "&" + WebSign + "&";

        return tranMessageBody;
    }

    private void spiltMessage(HashMap retKeyDict) {
        int tranFunc = Integer.parseInt((String) retKeyDict.get("TranFunc"));
        switch (tranFunc) {
            case 6000:
                spiltMessage_6000(retKeyDict);
                break;
            case 6005:
                spiltMessage_HandFee(retKeyDict);
                break;
            case 6006:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6007:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6008:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6010:
                spiltMessage_6010(retKeyDict);
                break;
            case 6011:
                spiltMessage_6011(retKeyDict);
                break;
            case 6014:
                spiltMessage_6014(retKeyDict);
                break;
            case 6027:
                spiltMessage_6027(retKeyDict);
                break;
            case 6031:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6033:
                spiltMessage_HandFee(retKeyDict);
                break;
            case 6034:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6040:
                spiltMessage_6040(retKeyDict);
                break;
            case 6041:
                spiltMessage_6041(retKeyDict);
                break;
            case 6042:
                spiltMessage_6042(retKeyDict);
                break;
            case 6043:
                spiltMessage_6043(retKeyDict);
                break;
            case 6044:
                spiltMessage_6044(retKeyDict);
                break;
            case 6048:
                spiltMessage_6048(retKeyDict);
                break;
            case 6050:
                spiltMessage_6050(retKeyDict);
                break;
            case 6052:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6053:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6055:
                spiltMessage_Reserve(retKeyDict);
                break;
            case 6056:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6061:
                spiltMessage_6061(retKeyDict);
                break;
            case 6064:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6065:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6066:
                spiltMessage_Reserve(retKeyDict);
                break;
            case 6067:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6068:
                spiltMessage_6068(retKeyDict);
                break;
            case 6069:
                spiltMessage_6069(retKeyDict);
                break;
            case 6070:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6072:
                spiltMessage_6072(retKeyDict);
                break;
            case 6073:
                spiltMessage_6073(retKeyDict);
                break;
            case 6037:
                spiltMessage_6037(retKeyDict);
                break;
            case 6077:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6079:
                spiltMessage_6079(retKeyDict);
                break;
            case 6080:
                spiltMessage_6080(retKeyDict);
                break;
            case 6082:
                spiltMessage_Phone(retKeyDict);
                break;
            case 6083:
                spiltMessage_Phone(retKeyDict);
                break;
            case 6084:
                spiltMessage_Reserve(retKeyDict);
                break;
            case 6085:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6087:
                spiltMessage_6087(retKeyDict);
                break;
            case 6088:
                spiltMessage_6088(retKeyDict);
                break;
            case 6090:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6091:
                spiltMessage_6091(retKeyDict);
                break;
            case 6092:
                spiltMessage_6092(retKeyDict);
                break;
            case 6093:
                spiltMessage_6093(retKeyDict);
                break;
            case 6097:
                spiltMessage_6097(retKeyDict);
                break;
            case 6098:
                spiltMessage_6098(retKeyDict);
                break;
            case 6099:
                spiltMessage_6099(retKeyDict);
                break;
            case 6100:
                spiltMessage_6100(retKeyDict);
                break;
            case 6101:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6102:
                spiltMessage_6102(retKeyDict);
                break;
            case 6103:
                spiltMessage_6103(retKeyDict);
                break;
            case 6108:
                spiltMessage_6108(retKeyDict);
                break;
            case 6109:
                spiltMessage_6109(retKeyDict);
                break;
            case 6110:
                spiltMessage_6014(retKeyDict);
                break;
            case 6114:
                spiltMessage_6114(retKeyDict);
                break;
            case 6118:
                spiltMessage_6118(retKeyDict);
                break;
            case 6119:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6120:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6121:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6122:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6123:
                spiltMessage_6123(retKeyDict);
                break;
            case 6124:
                spiltMessage_6124(retKeyDict);
                break;
            case 6126:
                spiltMessage_6126(retKeyDict);
                break;
            case 6127:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6128:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6133:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6134:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6135:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6136:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6137:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6138:
                spiltMessage_Reserve(retKeyDict);
                break;
            case 6142:
                spiltMessage_6142(retKeyDict);
                break;
            case 6145:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6146:
                spiltMessage_6146(retKeyDict);
                break;
            case 6147:
                spiltMessage_6147(retKeyDict);
                break;
            case 6148:
                spiltMessage_6148(retKeyDict);
                break;
            case 6149:
                spiltMessage_6149(retKeyDict);
                break;
            case 6150:
                spiltMessage_6150(retKeyDict);
                break;
            case 6151:
                spiltMessage_6151(retKeyDict);
                break;
            case 6152:
                spiltMessage_6152(retKeyDict);
                break;
            case 6153:
                spiltMessage_6153(retKeyDict);
                break;
            case 6154:
                spiltMessage_6154(retKeyDict);
                break;
            case 6155:
                spiltMessage_6155(retKeyDict);
                break;
            case 6156:
                spiltMessage_6156(retKeyDict);
                break;
            case 6157:
                spiltMessage_6157(retKeyDict);
                break;
            case 6158:
                spiltMessage_6158(retKeyDict);
                break;
            case 6159:
                spiltMessage_6159(retKeyDict);
                break;
            case 6160:
                spiltMessage_6160(retKeyDict);
                break;
            case 6161:
                spiltMessage_6161(retKeyDict);
                break;
            case 6162:
                spiltMessage_Reserve(retKeyDict);
                break;
            case 6163:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6164:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6165:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6166:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6201:
                spiltMessage_FrontLogNo(retKeyDict);
                break;
            case 6202:
                spiltMessage_6202(retKeyDict);
                break;

        }
    }

    private void spiltMessage_6000(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String custAcctId = arryMessage[0];
            String reserve = arryMessage[1];

            retKeyDict.put("CustAcctId", custAcctId);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_Reserve(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String reserve = arryMessage[0];

            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_FrontLogNo(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String FrontLogNo = arryMessage[0];
            String reserve = arryMessage[1];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_HandFee(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String FrontLogNo = arryMessage[0];
            String HandFee = arryMessage[1];
            String reserve = arryMessage[2];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("HandFee", HandFee);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6014(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TranFlag = arryMessage[0];
            String TranStatus = arryMessage[1];
            String TranAmount = arryMessage[2];
            String TranDate = arryMessage[3];
            String TranTime = arryMessage[4];
            String InCustAcctId = arryMessage[5];
            String OutCustAcctId = arryMessage[6];
            String reserve = arryMessage[7];

            retKeyDict.put("TranFlag", TranFlag);
            retKeyDict.put("TranStatus", TranStatus);
            retKeyDict.put("TranAmount", TranAmount);
            retKeyDict.put("TranDate", TranDate);
            retKeyDict.put("TranTime", TranTime);
            retKeyDict.put("InCustAcctId", InCustAcctId);
            retKeyDict.put("OutCustAcctId", OutCustAcctId);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6202(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String ResultFlag = arryMessage[0];
            String Password = arryMessage[1];
            String Reserve = arryMessage[2];

            retKeyDict.put("ResultFlag", ResultFlag);
            retKeyDict.put("Password", Password);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6142(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String OldFrontlogno = arryMessage[0];
            String CheckCode = arryMessage[1];
            String Reserve = arryMessage[2];

            retKeyDict.put("OldFrontlogno", OldFrontlogno);
            retKeyDict.put("CheckCode", CheckCode);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6146(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TranStatus = arryMessage[0];
            String TranAmount = arryMessage[1];
            String TranFee = arryMessage[2];
            String PayModel = arryMessage[3];
            String TranDate = arryMessage[4];
            String TranTime = arryMessage[5];
            String InCustAcctId = arryMessage[6];
            String InCustName = arryMessage[7];
            String RealInCustAcctId = arryMessage[8];
            String RealInCustName = arryMessage[9];
            String FrontLogNo = arryMessage[10];
            String Reserve = arryMessage[11];

            retKeyDict.put("TranStatus", TranStatus);
            retKeyDict.put("TranAmount", TranAmount);
            retKeyDict.put("TranFee", TranFee);
            retKeyDict.put("PayModel", PayModel);
            retKeyDict.put("TranDate", TranDate);
            retKeyDict.put("TranTime", TranTime);
            retKeyDict.put("InCustAcctId", InCustAcctId);
            retKeyDict.put("InCustName", InCustName);
            retKeyDict.put("RealInCustAcctId", RealInCustAcctId);
            retKeyDict.put("RealInCustName", RealInCustName);
            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6147(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String FrontLogNo = arryMessage[0];
            String CustAcctId = arryMessage[1];
            String TranAmount = arryMessage[2];
            String Note = arryMessage[3];
            String Reserve = arryMessage[4];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("CustAcctId", CustAcctId);
            retKeyDict.put("TranAmount", TranAmount);
            retKeyDict.put("Note", Note);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6148(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String ClientNo = arryMessage[0];
            String IdType = arryMessage[1];
            String IdCode = arryMessage[2];
            String CustRiskLevel = arryMessage[3];
            String AssessDate = arryMessage[4];
            String InvalidDate = arryMessage[5];

            retKeyDict.put("ClientNo", ClientNo);
            retKeyDict.put("IdType", IdType);
            retKeyDict.put("IdCode", IdCode);
            retKeyDict.put("CustRiskLevel", CustRiskLevel);
            retKeyDict.put("AssessDate", AssessDate);
            retKeyDict.put("InvalidDate", InvalidDate);
        }
    }

    private void spiltMessage_6149(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String ClientNo = arryMessage[0];
            String IdType = arryMessage[1];
            String IdCode = arryMessage[2];
            String CustRiskLevel = arryMessage[3];
            String AssessDate = arryMessage[4];
            String InvalidDate = arryMessage[5];

            retKeyDict.put("ClientNo", ClientNo);
            retKeyDict.put("IdType", IdType);
            retKeyDict.put("IdCode", IdCode);
            retKeyDict.put("CustRiskLevel", CustRiskLevel);
            retKeyDict.put("AssessDate", AssessDate);
            retKeyDict.put("InvalidDate", InvalidDate);
        }
    }

    private void spiltMessage_6150(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String FrontLogNo = arryMessage[0];
            String ClientNo = arryMessage[1];
            String CustName = arryMessage[2];
            String IdType = arryMessage[3];
            String IdCode = arryMessage[4];
            String AcctName = arryMessage[5];
            String AcctNo = arryMessage[6];
            String OpenBank = arryMessage[7];
            String OpenBankName = arryMessage[8];
            String Reserve = arryMessage[9];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("ClientNo", ClientNo);
            retKeyDict.put("CustName", CustName);
            retKeyDict.put("IdType", IdType);
            retKeyDict.put("IdCode", IdCode);
            retKeyDict.put("AcctName", AcctName);
            retKeyDict.put("AcctNo", AcctNo);
            retKeyDict.put("OpenBank", OpenBank);
            retKeyDict.put("OpenBankName", OpenBankName);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6151(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            int iCount = Integer.parseInt(TotalCount);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 8 + 1];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6152(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TradeType = arryMessage[0];
            String RemainRealTranAmt = arryMessage[1];

            retKeyDict.put("TranStatus", TradeType);
            retKeyDict.put("TranAmount", RemainRealTranAmt);
        }
    }

    private void spiltMessage_6153(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            String TotalAmount = arryMessage[3];
            String PayOrgClientNo = arryMessage[3];

            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iBegin5 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin5 + 1);
            int iBegin6 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin6 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 11 + 6];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("TotalAmount", TotalAmount);
            retKeyDict.put("PayOrgClientNo", PayOrgClientNo);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6154(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 17 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6155(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String FrontLogNo = arryMessage[0];
            String ClientNo = arryMessage[1];
            String CustName = arryMessage[2];
            String IdType = arryMessage[3];
            String IdCode = arryMessage[4];
            String CustAcctId = arryMessage[5];
            String Reserve = arryMessage[6];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("ClientNo", ClientNo);
            retKeyDict.put("CustName", CustName);
            retKeyDict.put("IdType", IdType);
            retKeyDict.put("IdCode", IdCode);
            retKeyDict.put("CustAcctId", CustAcctId);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6156(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String FrontLogNo = arryMessage[0];
            String ClientNo = arryMessage[1];
            String ProductCode = arryMessage[2];
            String AcctNo = arryMessage[3];
            String DealStatus = arryMessage[4];
            String Reserve = arryMessage[5];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("ClientNo", ClientNo);
            retKeyDict.put("ProductCode", ProductCode);
            retKeyDict.put("AcctNo", AcctNo);
            retKeyDict.put("DealStatus", DealStatus);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6157(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String FrontLogNo = arryMessage[0];
            String DealStatus = arryMessage[1];
            String Reserve = arryMessage[2];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("DealStatus", DealStatus);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6158(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 5 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6159(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String Balance = arryMessage[0];
            String TotalIncome = arryMessage[1];
            String WeekIncome = arryMessage[2];
            String LastIncome = arryMessage[3];
            String ClientNo = arryMessage[4];
            String CustName = arryMessage[5];
            String IdCode = arryMessage[6];
            String TransAcctId = arryMessage[7];
            String FundAcctId = arryMessage[8];
            String AcctNo = arryMessage[9];
            String Reserve = arryMessage[10];

            retKeyDict.put("Balance", Balance);
            retKeyDict.put("TotalIncome", TotalIncome);
            retKeyDict.put("WeekIncome", WeekIncome);
            retKeyDict.put("LastIncome", LastIncome);
            retKeyDict.put("ClientNo", ClientNo);
            retKeyDict.put("CustName", CustName);
            retKeyDict.put("IdCode", IdCode);
            retKeyDict.put("TransAcctId", TransAcctId);
            retKeyDict.put("FundAcctId", FundAcctId);
            retKeyDict.put("AcctNo", AcctNo);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6160(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 9 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6161(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TranType = arryMessage[0];
            String TranStatus = arryMessage[1];
            String TranAmount = arryMessage[2];
            String TranDate = arryMessage[3];
            String TranTime = arryMessage[4];
            String Reserve = arryMessage[5];

            retKeyDict.put("TranType", TranType);
            retKeyDict.put("TranStatus", TranStatus);
            retKeyDict.put("TranAmount", TranAmount);
            retKeyDict.put("TranDate", TranDate);
            retKeyDict.put("TranTime", TranTime);
            retKeyDict.put("Reserve", Reserve);
        }
    }


    private void spiltMessage_6027(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            int iCount = Integer.parseInt(TotalCount);
            int iBegin = bodyMessage.indexOf("&");
            //System.out.println(iBegin);
            int iEnd1 = bodyMessage.lastIndexOf("&");
            //System.out.println(iEnd1);
            String ArrayContent = bodyMessage.substring(iBegin + 1, iEnd1);
            //System.out.println(ArrayContent);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            //System.out.println(iEnd2);
            ArrayContent = ArrayContent.substring(0, iEnd2);
            int iEnd3 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd3);
            //System.out.println(ArrayContent);
            String reserve = arryMessage[iCount * 2 + 1];
            String reserve2 = arryMessage[iCount * 2 + 2];


            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
            retKeyDict.put("Reserve2", reserve2);
        }
    }

    private void spiltMessage_6048(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            int iCount = Integer.parseInt(TotalCount);
            int iBegin = bodyMessage.indexOf("&");
            int iEnd1 = bodyMessage.lastIndexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin + 1, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 2 + 1];


            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6126(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 10 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6103(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            int iCount = Integer.parseInt(TotalCount);
            int iBegin = bodyMessage.indexOf("&");
            int iEnd1 = bodyMessage.lastIndexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin + 1, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 2 + 1];


            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }


    private void spiltMessage_6123(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String ReturnStatus = arryMessage[0];
            String ReturnMsg = arryMessage[1];
            String Reserve = arryMessage[2];

            retKeyDict.put("ReturnStatus", ReturnStatus);
            retKeyDict.put("ReturnMsg", ReturnMsg);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6124(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String ReturnStatus = arryMessage[0];
            String ReturnMsg = arryMessage[1];
            String Password = arryMessage[2];
            String Reserve = arryMessage[3];

            retKeyDict.put("ReturnStatus", ReturnStatus);
            retKeyDict.put("ReturnMsg", ReturnMsg);
            retKeyDict.put("Password", Password);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6040(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 5 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6041(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 9 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6042(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TranType = arryMessage[0];
            String TranStatus = arryMessage[1];
            String TranAmount = arryMessage[2];
            String TranDate = arryMessage[3];
            String TranTime = arryMessage[4];
            String reserve = arryMessage[5];

            retKeyDict.put("TranType", TranType);
            retKeyDict.put("TranStatus", TranStatus);
            retKeyDict.put("TranAmount", TranAmount);
            retKeyDict.put("TranDate", TranDate);
            retKeyDict.put("TranTime", TranTime);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6043(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String Balance = arryMessage[0];
            String TotalIncome = arryMessage[1];
            String WeekIncome = arryMessage[2];
            String LastIncome = arryMessage[3];
            String reserve = arryMessage[4];

            retKeyDict.put("Balance", Balance);
            retKeyDict.put("TotalIncome", TotalIncome);
            retKeyDict.put("WeekIncome", WeekIncome);
            retKeyDict.put("LastIncome", LastIncome);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6044(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 6 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6050(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 2 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6072(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 10 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6073(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 12 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6061(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arrayMessage = bodyMessage.split("&");
            String ReturnStatus = arrayMessage[0];
            String ReturnMsg = arrayMessage[1];
            String Reserve = arrayMessage[2];

            retKeyDict.put("ReturnStatus", ReturnStatus);
            retKeyDict.put("ReturnMsg", ReturnMsg);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6068(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arrayMessage = bodyMessage.split("&");
            String FrontLogNo = arrayMessage[0];
            String ClientNo = arrayMessage[1];
            String DealStatus = arrayMessage[2];
            String Reserve = arrayMessage[3];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("ClientNo", ClientNo);
            retKeyDict.put("DealStatus", DealStatus);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6087(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arrayMessage = bodyMessage.split("&");
            String FrontLogNo = arrayMessage[0];
            String ClientNo = arrayMessage[1];
            String CustAcctId = arrayMessage[2];
            String Reserve = arrayMessage[3];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("ClientNo", ClientNo);
            retKeyDict.put("CustAcctId", CustAcctId);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6088(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arrayMessage = bodyMessage.split("&");
            String CustAcctId = arrayMessage[0];
            String ThirdCustId = arrayMessage[1];
            String Reserve = arrayMessage[2];

            retKeyDict.put("CustAcctId", CustAcctId);
            retKeyDict.put("ThirdCustId", ThirdCustId);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6097(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arrayMessage = bodyMessage.split("&");
            String CustAcctId = arrayMessage[0];
            String ThirdCustId = arrayMessage[1];
            String Reserve = arrayMessage[2];

            retKeyDict.put("CustAcctId", CustAcctId);
            retKeyDict.put("ThirdCustId", ThirdCustId);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6091(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arrayMessage = bodyMessage.split("&");
            String ResultFlag = arrayMessage[0];
            String ReturnFlag = arrayMessage[1];
            String Remark = arrayMessage[2];
            String Reserve = arrayMessage[3];

            retKeyDict.put("ResultFlag", ResultFlag);
            retKeyDict.put("ReturnFlag", ReturnFlag);
            retKeyDict.put("Remark", Remark);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6069(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arrayMessage = bodyMessage.split("&");
            String FrontLogNo = arrayMessage[0];
            String DealStatus = arrayMessage[1];
            String Reserve = arrayMessage[2];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("DealStatus", DealStatus);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6100(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arrayMessage = bodyMessage.split("&");
            String FrontLogNo = arrayMessage[0];
            String DealStatus = arrayMessage[1];
            String Reserve = arrayMessage[2];

            retKeyDict.put("FrontLogNo", FrontLogNo);
            retKeyDict.put("DealStatus", DealStatus);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6102(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arrayMessage = bodyMessage.split("&");
            String ThirdCustId = arrayMessage[0];
            String OrangeDoorStatus = arrayMessage[1];
            String OrangeDoorDesc = arrayMessage[2];
            String SCFPID = arrayMessage[3];
            String OrangePayStatus = arrayMessage[4];
            String OrangePayDesc = arrayMessage[5];
            String WpAccNo = arrayMessage[6];
            String Reserve = arrayMessage[7];

            retKeyDict.put("ThirdCustId", ThirdCustId);
            retKeyDict.put("OrangeDoorStatus", OrangeDoorStatus);
            retKeyDict.put("OrangeDoorDesc", OrangeDoorDesc);
            retKeyDict.put("SCFPID", SCFPID);
            retKeyDict.put("OrangePayStatus", OrangePayStatus);
            retKeyDict.put("OrangePayDesc", OrangePayDesc);
            retKeyDict.put("WpAccNo", WpAccNo);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6079(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 12 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6080(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 17 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6098(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 10 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6108(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 7 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6109(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 10 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6114(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 10 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6010(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 7 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6011(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String LastBalance = arryMessage[0];
            String CurBalance = arryMessage[1];
            String reserve = arryMessage[2];

            retKeyDict.put("LastBalance", LastBalance);
            retKeyDict.put("CurBalance", CurBalance);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6037(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalAmount = arryMessage[1];
            String TotalBalance = arryMessage[2];
            String TotalFreezeAmount = arryMessage[3];
            String custAcctId = arryMessage[0];
            String reserve = arryMessage[4];

            retKeyDict.put("CustAcctId", custAcctId);
            retKeyDict.put("TotalAmount", TotalAmount);
            retKeyDict.put("TotalBalance", TotalBalance);
            retKeyDict.put("TotalFreezeAmount", TotalFreezeAmount);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6092(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String custAcctId = arryMessage[0];
            String reserve = arryMessage[1];

            retKeyDict.put("CustAcctId", custAcctId);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_6093(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String ThirdCustId = arryMessage[0];
            String TotalBalance = arryMessage[1];
            String TotalFreezeAmount = arryMessage[2];
            String Reserve = arryMessage[3];

            retKeyDict.put("ThirdCustId", ThirdCustId);
            retKeyDict.put("TotalBalance", TotalBalance);
            retKeyDict.put("TotalFreezeAmount", TotalFreezeAmount);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6099(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String ThirdCustId = arryMessage[0];
            String TotalBalance = arryMessage[1];
            String TotalFreezeAmount = arryMessage[2];
            String Reserve = arryMessage[3];

            retKeyDict.put("ThirdCustId", ThirdCustId);
            retKeyDict.put("TotalBalance", TotalBalance);
            retKeyDict.put("TotalFreezeAmount", TotalFreezeAmount);
            retKeyDict.put("Reserve", Reserve);
        }
    }

    private void spiltMessage_6118(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String TotalCount = arryMessage[0];
            String BeginNum = arryMessage[1];
            String LastPage = arryMessage[2];
            String RecordNum = arryMessage[3];
            int iCount = Integer.parseInt(RecordNum);
            int iBegin1 = bodyMessage.indexOf("&");
            String ArrayContent = bodyMessage.substring(iBegin1 + 1);
            int iBegin2 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin2 + 1);
            int iBegin3 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin3 + 1);
            int iBegin4 = ArrayContent.indexOf("&");
            ArrayContent = ArrayContent.substring(iBegin4 + 1);
            int iEnd1 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd1);
            int iEnd2 = ArrayContent.lastIndexOf("&");
            ArrayContent = ArrayContent.substring(0, iEnd2);
            String reserve = arryMessage[iCount * 10 + 4];
            retKeyDict.put("TotalCount", TotalCount);
            retKeyDict.put("BeginNum", BeginNum);
            retKeyDict.put("LastPage", LastPage);
            retKeyDict.put("RecordNum", RecordNum);
            retKeyDict.put("ArrayContent", ArrayContent);
            retKeyDict.put("Reserve", reserve);
        }
    }

    private void spiltMessage_Phone(HashMap retKeyDict) {
        if (retKeyDict.containsKey("BodyMsg")) {
            String bodyMessage = (String) retKeyDict.get("BodyMsg");
            String[] arryMessage = bodyMessage.split("&");
            String SerialNo = arryMessage[1];
            String reserve = arryMessage[2];
            String RevMobilePhone = arryMessage[0];

            retKeyDict.put("SerialNo", SerialNo);
            retKeyDict.put("RevMobilePhone", RevMobilePhone);
            retKeyDict.put("Reserve", reserve);
        }
    }


}
