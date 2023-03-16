package com.home.mall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.home.mall.order.config.AliPayTemplate;
import com.home.mall.order.service.OrderService;
import com.home.mall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @description: 监听支付宝发过来的异步通知消息
 * @author: lyq
 * @createDate: 16/3/2023
 * @version: 1.0
 */
@RestController
public class AlipayListener {
    @Autowired
    private AliPayTemplate aliPayTemplate;
    @Autowired
    private OrderService orderService;
    @PostMapping("/payed/notify")
    public String payedSuccess(PayAsyncVo vo,HttpServletRequest request) throws AlipayApiException {
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        System.out.println("成功收到支付宝的异步通知，内容为:"+parameterMap);
        //进行验签
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayTemplate.getAlipay_public_key(), aliPayTemplate.getCharset(), aliPayTemplate.getSign_type()); //调用SDK验证签名        if(signVerified){
            //如果验签成功了，进行订单的处理，改变订单的状态以及将这个交易信息保存起来
        if(signVerified){
            System.out.println("签名验证成功...");
            String result=orderService.handlerOrder(vo);
            return result;
        }else{
            return "error";
        }

    }
}
