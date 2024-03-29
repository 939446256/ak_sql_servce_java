package com.example.demo.config;
import com.example.demo.ak.akSQL.vo.ContextUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;


public class MyFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String ip = request.getRemoteAddr();
        String requestUrl = request.getRequestURI();
        String employeeId = request.getHeader("Context-Employee-Id");
        String userId = request.getHeader("Context-User-Id");
        if(!Objects.isNull(employeeId)){
            ContextUtil.setEmployeeId(employeeId);
        }
        if(!Objects.isNull(userId)){
            ContextUtil.setUserId(userId);
        }
        System.out.printf("%s %s 访问了 %s \n", sdf.format(new Date()), ip, requestUrl);
        System.out.printf("%s 用户ID %s 和员工ID %s \n", sdf.format(new Date()), userId, employeeId);

        //内部接口，直接通过
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
