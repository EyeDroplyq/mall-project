package com.home.common.valid;


import sun.invoke.empty.Empty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @description: 自定义的校验注解对应的校验器，需要实现ConstraintValidator<A,T> A对应注解，T对应字段的类型
 * @author: lyq
 * @createDate: 14/1/2023
 * @version: 1.0
 */
public class ListValueConstrainValidatorForInteger implements ConstraintValidator<ListValue,Integer> {

    private Set<Integer> set=new HashSet<>();
    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        //获取注解中的信息
        int[] vals = constraintAnnotation.vals();
        for (int val : vals) {
            if(String.valueOf(val).equals("")){
                set.add(0);
            }else{
                set.add(val);
            }
        }
    }

    //判断是否满足校验规则
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        //判断传过来的数据是不是属于我们指定的数据之中
        return set.contains(value);
    }
}
