package com.home.mall.auth.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.TypeReference;
import com.home.common.constant.AuthServerConstant;
import com.home.common.exception.BizErrorEnum;
import com.home.common.utils.R;
import com.home.common.vo.MemberRespVo;
import com.home.mall.auth.feign.MemberServerFeign;
import com.home.mall.auth.feign.ThirdPartServiceFeign;
import com.home.mall.auth.vo.UserLoginVo;
import com.home.mall.auth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: lyq
 * @createDate: 6/2/2023
 * @version: 1.0
 */
@Controller
public class LoginController {
    @Autowired
    private ThirdPartServiceFeign thirdPartServiceFeign;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private MemberServerFeign memberServerFeign;

    //注册发送短信验证码功能
    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendSms(@RequestParam("phone") String phone) {
        //为了防止频繁往redis中存数据，所以我们进行判断
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            //如果redis中已经有验证码了，再来看是不是在一分钟之内，如果在一分钟之内不允许发送
            long time = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - time < 60000) {
                //如果在一分钟之内,不允许再次发送
                return R.error(BizErrorEnum.SMS_CODE_EXCEPTION.getCode(), BizErrorEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        String code = RandomUtil.randomNumbers(5) + "_" + System.currentTimeMillis();
        String[] result = code.split("_");
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_PREFIX + phone, code, 10, TimeUnit.MINUTES);
        thirdPartServiceFeign.sendSms(phone, result[0]);
        return R.ok();
    }


    //注册功能
    @PostMapping(value = "/register")
    public String register(@Valid UserRegistVo userRegistVo, BindingResult result, RedirectAttributes redirectAttributes, HttpSession session) {

        if (result.hasErrors()) {
            //如果数据有错误，转发到注册页
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.mall.com/reg.html";
        }

        //1.校验验证码对不对
        String code = userRegistVo.getCode();
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_PREFIX + userRegistVo.getPhone());
        if (!StringUtils.isEmpty(redisCode)) {
            //将redis中的这个验证码删掉，令牌机制
            redisTemplate.delete(AuthServerConstant.SMS_CODE_PREFIX + userRegistVo.getPhone());

            //如果redis中的验证码不为空的话，进行判断是不是相等
            if (code.equals(redisCode.split("_")[0])) {
                //远程调用真正的注册功能
                R r = memberServerFeign.registerMember(userRegistVo);
                if(r.getCode()==0){
                    //调用成功
                    //成功注册后重定向到登录页
                    return "redirect:http://auth.mall.com/login.html";

                }else{
                    //远程调用失败
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.mall.com/reg.html";
                }
            }
        }
        //如果验证码验证不成功（1.redis中没有验证码2.验证码不正确）
        Map<String, String> errors = new HashMap<>();
        errors.put("code", "验证码错误");
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.mall.com/reg.html";

    }


    //登录功能
    @PostMapping(value = "/login")
    public String login(UserLoginVo vo,RedirectAttributes redirectAttributes,HttpSession session){
        //远程调用登录接口，主要是来校验
        R r = memberServerFeign.loginMember(vo);
        if(r.getCode()==0){
            //如果远程调用成功
            //将数据存到session中
            MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER,data);
            return "redirect:http://mall.com";
        }else{
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.mall.com/login.html";
        }

    }

}
