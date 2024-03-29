package com.example.demo.config;

import com.google.common.collect.Lists;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class MyFilterRegistrationConfiguration {

    /**
     * 基础过滤器-注册过滤器，并对特定路由进行过滤
     * @return
     */
    @Bean
    public FilterRegistrationBean<Filter> registerMyFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new MyFilter());
        // 设置过滤的路由
        filterRegistrationBean.setUrlPatterns(Lists.newArrayList("/*"));
        // 设置过滤器优先级
        filterRegistrationBean.setOrder(2);
        return filterRegistrationBean;
    }
}
