package com.hjj.homieMatching.aop;

import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 登录拦截 AOP
 */
@Aspect
@Component
@Slf4j
public class LoginInterceptor {

    @Resource
    private UserService userService;

    @Before("execution(* com.hjj.homieMatching.controller.*.*(..)) && " +
            "!execution(* com.hjj.homieMatching.controller.UserController.userLogin(..)) && " +
            "!execution(* com.hjj.homieMatching.controller.UserController.userRegister(..))")
    public void beforeControllerMethodExecution() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        if (userService.getLoginUser(request) == null) {
            log.info("拦截登陆成功！");
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
    }
}
