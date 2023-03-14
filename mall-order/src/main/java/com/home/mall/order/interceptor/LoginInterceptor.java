package com.home.mall.order.interceptor;

import com.home.common.constant.AuthServerConstant;
import com.home.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @description:
 * @author: lyq
 * @createDate: 22/2/2023
 * @version: 1.0
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberRespVo> loginUser=new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //远程调用查询订单的状态的时候是不需要进行登录拦截的，所以我们将那个url进行一个放行
        String uri = request.getRequestURI();
        AntPathMatcher matcher=new AntPathMatcher();
        boolean match = matcher.match("/order/order/getOrderStatus/**", uri);
        if(match){
            return true;
        }
        //从session中得到用户的登录信息
        MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute!=null){
            //将用户的登录信息设置到ThreadLocal中
            loginUser.set(attribute);
            return true;
        }else{
            response.sendRedirect("http://mall.com/login.html");
            return false;
        }
    }
}
