package com.shijivk.todokireader.config;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.time.Duration;
import java.util.Map;


//需要更改地址变量，将其提出
public class WebDriverFactory extends BasePooledObjectFactory<WebDriver> {

    @Override
    public WebDriver create() throws Exception {
        //获取driver地址
        Yaml yaml = new Yaml();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("application.yaml");

        Map<String, Object> ret = yaml.load(stream);
        String driverPath = (String)ret.get("driverPath");

        //设置本地chromedriver地址
        System.setProperty("webdriver.chrome.driver", driverPath);

        //创建无Chrome无头参数
        ChromeOptions chromeOptions=new ChromeOptions();
        //chromeOptions.addArguments("-headless");
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

        //创建Driver实例
        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
        //增加隐式等待时间
        chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        return chromeDriver;
    }

    @Override
    public PooledObject<WebDriver> wrap(WebDriver webDriver) {
        return new DefaultPooledObject<WebDriver>(webDriver);
    }

    @Override
    public void destroyObject(PooledObject<WebDriver> p, DestroyMode destroyMode) throws Exception {
        p.getObject().quit();
    }
}
