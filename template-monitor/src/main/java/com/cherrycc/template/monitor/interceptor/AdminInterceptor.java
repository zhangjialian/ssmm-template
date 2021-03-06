package com.cherrycc.template.monitor.interceptor;

import com.alibaba.fastjson.JSON;
import com.cherrycc.template.bo.UserBO;
import com.cherrycc.template.common.systemEnum.PageURLEnum;
import com.cherrycc.template.common.systemEnum.SessionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by zjf on 2017/11/10.
 */
public class AdminInterceptor implements HandlerInterceptor{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String URL_BODY_AND_PARAMS_SPLIT = "?";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        HttpSession session = request.getSession();
        UserBO user = (UserBO) session.getAttribute(SessionEnum.LOGIN_USER.getCode());

        if(user == null){
            //如果是get请求，将当前的路由存入session
            if(request.getMethod().equals(RequestMethod.GET.toString())){
                String mapping = this.getRequestMapping(request);
                session.setAttribute(SessionEnum.REQUEST_UTL.getCode(), mapping);
            }

            logger.info("info@preHandle，用户未登录，返回登录页面");
            response.sendRedirect(PageURLEnum.LOGIN.getUrl());
            return false;
        }

        return true;
    }

    /**
     * 拼装请求路径及参数
     * @param request
     * @return
     */
    private String getRequestMapping(HttpServletRequest request){
        String requestMapping = request.getServletPath();
        if (request.getQueryString() != null){
            requestMapping += URL_BODY_AND_PARAMS_SPLIT + request.getQueryString();
        }
        return requestMapping;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
        /*if(modelAndView != null){
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            modelAndView.addObject("dateTime", dateTime);
        }*/
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {
        if(e != null){
            logger.info("info@afterCompletion，object={}, e=", JSON.toJSONString(o), e);
        }
    }
}
