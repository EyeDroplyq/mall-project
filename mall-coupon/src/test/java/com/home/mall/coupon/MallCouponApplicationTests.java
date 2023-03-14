package com.home.mall.coupon;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallCouponApplicationTests {

    @Test
    //使用反射原理向一个Integer类型的list中添加了string类型的数据
    public void contextLoads() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Integer> list=new ArrayList<>();
        Class<? extends List> listClass = list.getClass();
        Method method = listClass.getMethod("add", Object.class);
        method.invoke(list,"String");
        System.out.println(list.get(0));
    }

}
