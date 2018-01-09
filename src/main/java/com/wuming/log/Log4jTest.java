package com.wuming.log;

import com.wuming.xml.jsoup.JsoupTest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * Created by wuming on 2017/7/19.
 */
public class Log4jTest {

    static Logger logger = LogManager.getLogger(JsoupTest.class);

    /**
     * 因为是
     * 没有Log4j配置文件,以下代码会以Log4j默认的配置运行，level = error
     */
    @Test
    public void runWithoutLog4jXmlSetting() {
        logger.entry();   //trace级别的信息，单独列出来是希望你在某个方法或者程序逻辑开始的时候调用，和logger.trace("entry")基本一个意思
        logger.error("Did it again!");   //error级别的信息，参数就是你输出的信息
        logger.error("{}", "我是日志信息，Log4j 2.x以上版本可以使用{}占位符来输出日志");
        logger.info("我是info信息");    //info级别的信息
        logger.debug("我是debug信息");
        logger.warn("我是warn信息");
        logger.fatal("我是fatal信息");
        logger.log(Level.DEBUG, "我是debug信息");   //这个就是制定Level类型的调用：谁闲着没事调用这个，也不一定哦！
        logger.exit();    //和entry()对应的结束方法，和logger.trace("exit");一个意思
    }


}
