package com.kking.admin.aop;

import com.alibaba.fastjson.JSON;
import com.kking.admin.shiro.MyRedisSessionDAO;
import com.kking.admin.util.Constants;
import com.kking.admin.util.RedisCacheManager;
import com.kking.common.annotation.WriteAuditLog;
import com.kking.dao.entity.TAuditLog;
import com.kking.dao.entity.TSysUser;
import com.kking.dao.entity.UserToken;
import com.kking.dao.mapper.TAuditLogMapper;
import com.kking.dao.mapper.TSysUserMapper;
import org.apache.shiro.session.Session;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Component
@Aspect
public class LogEditAOP {

    private static final Logger logger = LoggerFactory.getLogger(LogEditAOP.class);

    private static String[] types = {"java.lang.Integer", "java.lang.Double",
            "java.lang.Float", "java.lang.Long", "java.lang.Short",
            "java.lang.Byte", "java.lang.Boolean", "java.lang.Char",
            "java.lang.String", "int", "double", "long", "short", "byte",
            "boolean", "char", "float"};


    @Autowired
    TSysUserMapper userMapper;

    @Autowired
    TAuditLogMapper auditLogMapper;

    @Autowired
    MyRedisSessionDAO myRedisSessionDAO;


    @Pointcut("@annotation(com.kking.common.annotation.WriteAuditLog)")
    public void logPointCut() {
    }

    /**
     * 定义切点Pointcut
     * 第一个*号：表示返回类型， *号表示所有的类型
     * 第二个*号：表示类名，*号表示所有的类
     * 第三个*号：表示方法名，*号表示所有的方法
     * 后面括弧里面表示方法的参数，两个句点表示任何参数
     */
    @Pointcut("execution(* com.kking.admin.controller..*.*(..))")
    public void executionService() {
    }

    @AfterReturning(returning="result", pointcut = "logPointCut()")
    public void saveLog(JoinPoint joinPoint, Object result) {
        TAuditLog auditLog = new TAuditLog();

        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //获取注解中的参数
        WriteAuditLog writeAuditLog = method.getAnnotation(WriteAuditLog.class);
        if (writeAuditLog != null) {
            int operatingType = writeAuditLog.operatingType();
            auditLog.setOperatingType(operatingType);
            int business = writeAuditLog.business();
            auditLog.setBusiness(business);
        }

        //获取请求的参数，转换成json
        StringBuilder sb = new StringBuilder();
        //获取所有的参数
        Object[] args = joinPoint.getArgs();

        for (Object arg: args) {
            // 获取对象类型
            if (null == arg) {
                sb.append(arg + "; ");
                continue;
            }
            String typeName = arg.getClass().getName();
            boolean flag = false;
            for (String t : types) {
                //1 判断是否是基础类型
                if (t.equals(typeName)) {
                    sb.append(arg + "; ");
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                sb.append(getFieldsValue(arg) + "; ");
            }
        }
        String[] values = sb.toString().split(";");
        String[] parameterNames = signature.getParameterNames();
        Map<String,String> paramMap = new HashMap<>();


        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], values[i]);
        }
        String params = JSON.toJSONString(paramMap);
        if (params.length() > 255) {
            auditLog.setRequestParam(params.substring(0, 254));
        } else {
            auditLog.setRequestParam(params);
        }


        //获取用户信息
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String session = request.getHeader("X-Token");
        UserToken userToken= myRedisSessionDAO.getToken(session);
        if (null != userToken) {
            Integer userId =  userToken.getUserId();
//            auditLog.setUserId(userId);
//            TSysUser user = userMapper.selectById(userId);
//            if (null != user) {
//                auditLog.setUserAcct(user.getUserName());
//                auditLog.setUserName(user.getUserName());
//            }
            auditLog.setUserId(userId);
            auditLog.setUserName(userToken.getUserName());
            auditLog.setCreateTime(new Date());

        }

        //方法执行成功则插入表
        if (0 == Integer.parseInt(((Map) result).get("code").toString())) {
            auditLogMapper.insert(auditLog);
        }
    }

    //解析实体类，获取实体类中的属性
    private static String getFieldsValue(Object obj) {
        //通过反射获取所有的字段，getFileds()获取public的修饰的字段
        //getDeclaredFields获取private protected public修饰的字段
        Field[] fields = obj.getClass().getDeclaredFields();
        String typeName = obj.getClass().getName();
        for (String t : types) {
            if (t.equals(typeName)) {
                return "";
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Field f : fields) {
            //在反射时能访问私有变量
            f.setAccessible(true);
            try {
//                boolean flag = true;//未测试如果实体类里面继续包含实体类的情况
                for (String str : types) {
                    if (f.getType().getName().equals(str)) {
                        sb.append(f.getName() + " : " + f.get(obj) + ", ");
//                        flag = false;
                        break;
                    }
                }
                /*if (flag) {
                    getFieldsValue(f.get(obj));
                }*/
            } catch (IllegalArgumentException|IllegalAccessException e) {
                logger.error("获取参数的值失败: ", e);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 方法调用之前调用
     * @param joinPoint
     */
    @Before(value = "executionService()")
    public void doBefore(JoinPoint joinPoint){
        //添加日志打印
        String requestId = String.valueOf(UUID.randomUUID());
        MDC.put("logname" , joinPoint.getSignature().getDeclaringTypeName());
        MDC.put(Constants.Log.LOG_TRACE_ID,requestId);
        logger.info("执行方法：{}，请求参数为：{}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));

    }

    /**
     * 方法之后调用
     * @param joinPoint
     * @param returnValue 方法返回值
     */
    @AfterReturning(pointcut = "executionService()",returning="returnValue")
    public void  doAfterReturning(JoinPoint joinPoint,Object returnValue){
        logger.info("执行方法：{}，响应参数为：{}", joinPoint.getSignature().getName(), returnValue);
        MDC.clear();
    }

    /**
     * 统计方法执行耗时Around环绕通知
     * @param joinPoint
     * @return
     */
    @Around("executionService()")
    public Object timeAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取开始执行的时间
        long startTime = System.currentTimeMillis();
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } catch (Throwable e) {
            logger.error("执行方法："+joinPoint.getSignature().getName()+"出错", e);
            throw e;
        }
        // 获取执行结束的时间
        long endTime = System.currentTimeMillis();
        logger.info("执行方法：{}，处理本次请求共耗时：{} ms", joinPoint.getSignature().getName(), endTime-startTime);
        return obj;
    }


}
