package com.sky.aspect;

import com.sky.anno.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义公共字段自动填充切面类
 */
@Slf4j
@Component
@Aspect
public class AutoFillAspect {
    @Before("@annotation(com.sky.anno.AutoFill)")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始公共字段填充，AutoFill start");
        //1.获取目标方法上的注解，并拿到注解里的属性值
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();   //方法签名
        Method method = methodSignature.getMethod();
        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        //2 获取目标方法的参数对象
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || args[0] == null) {
            return ;
        }
        Object entity = args[0];//拿到实体对象


        //3. 判断直接中属性值，如果是INSERT，就补充四个字段（创建时间、更新时间、创建人、更新人）
        try {
            if (operationType == OperationType.INSERT) {
                // 通过反射去补充属性值
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateUser.invoke(entity, BaseContext.getCurrentId());
                setCreateUser.invoke(entity, BaseContext.getCurrentId());
                setUpdateTime.invoke(entity,LocalDateTime.now());
                setCreateTime.invoke(entity,LocalDateTime.now());
            }else
                //4.判断直接中的属性值，如果是UPDATE，就补充两个字段（更新时间，更新人）
                if (operationType == OperationType.UPDATE) {
                    Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                    Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                    setUpdateUser.invoke(entity, BaseContext.getCurrentId());
                    setUpdateTime.invoke(entity,LocalDateTime.now());

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
