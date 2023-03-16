package com.home.mall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.home.mall.order.vo.PayVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: lyq
 * @createDate: 15/3/2023
 * @version: 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "alipay")
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTemplate {
    public  String app_id = "2021000119682116";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public  String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC36FEoeelG5p60MeSR5cZHM4ry2G+ldtZ2EbXsOTK+luGMu2r5Mmmw54y+dU8lf1UFCrDjDHM6yoIBgGIQScEj207I8WdcH138O6Mt39vcZETuDe7C8Peo7LvMVo576XFyjUAX+3jOfX06f/ewlOM0YAcT+oMXG/dNOZYjI3P+wdW/9f22+kW4b00EwPWRGvWUnnvrlkxHygtP74dSy8ngWowgptVx09jt1SZAPk2PewWid6E5JD5mFXaigxBgr3ENYHmWFO3gYbpe8U30TeUaDaohtnL0D1JTL7+xwCWm4ecV0v7Hdz1JYVKGsV+nlzSYfzBNMxvoFdBfwl0qIaZHAgMBAAECggEAS5Y4hSJWNsXixL0c7Lo73Ffa7CT4/lDxpGVzGDwKFzt8SJNfTDzkI8mHl2Avv4GuN/xE54zeb3N8K5HQvFEUCODCaOSPTtW1k04Pq3Musk6j5hPj5kuT/uIN7dZFibhQ4Hwds72v0W7SYZDnBLStDYHKZvO3nW/pHNxmUNrpefMOju8seeXKxGqyPgzbGuRMXxkAES4xivJq1OGedDKUEzvU8FbTxl1qJk1KPSKhdkwADUFxk1ftTG9bJqAzxZQrcVp57WdUehOEozlRBZIRZSBIIiYT1kHbz8NC2fRUGVmAkhr6AnSXqyVsVswlwEaAtJUE9lgSPodm2Ahi55alQQKBgQDjj80n9zDtUh1HSLy++uPTEip+yRkEXjmxQrh7AOk42UUEK2RElm7gId0IH70Pfnkob6Kx11MeBTb2MuiI98KB2220DoPSH+5aaEAubqxJOre5vIkNrFZ/VNa2W5y5NXBiS5//VkZbRFTkVO9D5mQKgWzLavd2txg4Tv673eqo4wKBgQDO4+u/sI77/+3moVZ4DYPvJ9kLGpk8HdYYI3M73FRiSerMpsJWL9rREpu7y2DxMG7B95atCWCXyIhcffEbUDc1PoNt8RZf8G1N9LwICeXRyWwqnNmNJ+LtIhd403JwfrIgsoT/UIKsV/N/L3j/lK5AQx21Sy95fHmNH0sq1wjeTQKBgQDaC1P803JiM6gyQZP5OtVJzcJCMdZyxQZqvw+dbMYL3sOutxUgUzRyNoBf5Gl3iyv0fuNZnITb4wtC5IzDkQMjAmofeTj+AfKVVgcJdqpGkKiyo+B+5X8MCvKhUS7emEHTLbtlIfnQQa1GC8wnN37sQg9I6nmrmszuR2L2CDFtGQKBgQCrmMVPP4zInNRgJs8GUgds0/skR5JzFzgc20JXkv52a2KRDXtAav6tzCZZE227fnVO8iEKMi3olo3rURYqllTEF66BAKaTOmIed2+Bp1vzrYHGskOK3VtNXapkL9TKU3tAvXCsJsXLVkOC6QgNy+7fJAu+87ZTqBdVInCA/su/PQKBgQCfW5e+q12HYqDXWoHBKZDjokR6ylZyMjziIxLBlSrTxcE1J0jmid4WaD76r+bPGhv3N1UghQ05NFD4vlFaKyW34Fwc9RoM42vINtWe+I1TTquTGzdZ05uM2nEluNPuk8Wc8xWmDNW0yj1jgpu6H4kH0X/YTObNKP7R/fL7FgvGoQ==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt21lXSzq07VRZuDbC/OgfAqvVnG5tQaI6rcMILYdc0Lob+rMblS6QUZS4LKWQsX09CplS2MUfcbRoCVWhy+DA4Z5Va+XFIHKsB02muu1keCh4I9vvGuod+jK8uKOp0O1CCqLaDbZVTVjo5Sox+msukjkA7rrlUuwbnsuhQLp9tFqpwCX1597H1UCD7EweC0bjRh+/ar/UoAmmhwAgTagemdUNyNqzgvtYe6A2law17VSSluDf2+WPRbxdZ/mJ2WYDA7oc4TLLj10XTn/SALHpUtozmuQ1wUVDDrg+jo1QSULj4oj3C6qtRsXqTcxwKAAmzqcwbfKoSCF9wlp/TI+4wIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public  String notify_url = "http://shrrtb.natappfree.cc/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public  String return_url = "http://member.mall.com/memberOrder.html";

    // 签名方式
    public  String sign_type = "RSA2";

    // 字符编码格式
    public  String charset = "utf-8";
    public  String timeout = "1m"; //自动收单的时间

    // 支付宝网关
    public  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
    public String pay(PayVo vo) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,app_id,merchant_private_key, "json", charset, alipay_public_key, sign_type);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject =vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+timeout+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //输出
        System.out.println(result);
        return result;
    }
}
