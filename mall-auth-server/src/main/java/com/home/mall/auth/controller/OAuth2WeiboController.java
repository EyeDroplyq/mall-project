package com.home.mall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.home.common.constant.AuthServerConstant;
import com.home.common.utils.HttpUtils;
import com.home.common.utils.R;
import com.home.common.vo.MemberRespVo;
import com.home.mall.auth.feign.MemberServerFeign;
import com.home.mall.auth.vo.SocialUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: lyq
 * @createDate: 7/2/2023
 * @version: 1.0
 */
@Controller
@Slf4j
public class OAuth2WeiboController {
    @Autowired
    private MemberServerFeign memberServerFeign;
    @GetMapping(value = "/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code")String code, HttpSession session) throws Exception {
        Map<String,String> header = new HashMap<>();
        Map<String,String> query = new HashMap<>();

//http://mall.com/oauth2.0/weibo/success?code=4ef22611cd812f91dcd75acae8a181c0
        //获取access_token
        //https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
        HashMap<String, String> map = new HashMap<>();
        map.put("client_id","2329640108");
        map.put("client_secret","383546a5cc3e5f002306630a0b36fdc1");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.mall.com/oauth2.0/weibo/success");
        map.put("code",code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", header, query, map);
        if(response.getStatusLine().getStatusCode()==200){
            //如果响应成功
            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity);
            SocialUserVo socialUserVo = JSON.parseObject(res, SocialUserVo.class);
            //远程调用用户服务的第三方登录功能
            R r = memberServerFeign.oauthLogin(socialUserVo);
            if(r.getCode()==0){
                MemberRespVo respVo = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登录成功，用户{}",respVo.toString());
                //将session存在redis中
                /**
                 * 配置了存到redis时的序列化机制
                 * 配置了cookie的作用域
                 */
                session.setAttribute(AuthServerConstant.LOGIN_USER,respVo);
                //登录成功之后返回首页
                return "redirect:http://mall.com";
            }else{
                return "redirect:http://auth.mall.com/login.html";
            }
        }else{
            //如果响应失败
            return "redirect:http://auth.mall.com/login.html";
        }



    }
}
