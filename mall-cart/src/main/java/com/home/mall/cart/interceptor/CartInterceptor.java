package com.home.mall.cart.interceptor;

import com.home.common.constant.AuthServerConstant;
import com.home.common.vo.MemberRespVo;
import com.home.mall.cart.constant.UserKeyConstant;
import com.home.mall.cart.to.UserInfoTo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @description: 拦截器： 在执行目标方法前，判断用户是不是登录状态，并封装传递给controller目标请求
 * @author: lyq
 * @createDate: 8/2/2023
 * @version: 1.0
 */

public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal=new ThreadLocal<>();
    /**
     * 在目标方法之前的处理
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo member = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(member!=null){
            //如果已经登录了,只需要保存用户id
            userInfoTo.setUserId(member.getId());
        }
        //如果没有登录,是临时用户，看看有没有user-key，传过去
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length>0){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(UserKeyConstant.USER_KEY)){
                    //如果cookie里面已经有user-key了，直接保存
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            //如果没有user-key的话，保存生成一个
            String userKey = UUID.randomUUID().toString();
            userInfoTo.setUserKey(userKey);

        }
        //使用ThreadLocal来返回数据，这样同一个线程可以共享数据
        threadLocal.set(userInfoTo);
        //任何请求都放行
        return true;
    }


    /**
     * 在目标方法之后的处理
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        if(!userInfoTo.getTempUser()){
            //如果不是临时用户
            //保存cookie并且延长过期时间
            Cookie cookie = new Cookie(UserKeyConstant.USER_KEY, userInfoTo.getUserKey());
            cookie.setDomain("mall.com");
            cookie.setMaxAge(UserKeyConstant.USER_KEY_TIME);
            response.addCookie(cookie);
        }
    }
}
