package com.rcbg.afku.instodramat.authusers.configs;

import com.rcbg.afku.instodramat.authusers.filters.ExceptionPropagatorFilter;
import com.rcbg.afku.instodramat.authusers.filters.UserProfileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfiguration {

    UserProfileFilter userProfileFilter;

    ExceptionPropagatorFilter exceptionPropagatorFilter;

    @Autowired
    public FilterConfiguration(UserProfileFilter userProfileFilter, ExceptionPropagatorFilter exceptionPropagatorFilter) {
        this.userProfileFilter = userProfileFilter;
        this.exceptionPropagatorFilter = exceptionPropagatorFilter;
    }

    @Bean
    public FilterRegistrationBean<UserProfileFilter> userProfileFilterReg(){
        FilterRegistrationBean<UserProfileFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(userProfileFilter);
        registrationBean.addUrlPatterns("/api/v1/**");
        registrationBean.addUrlPatterns("/api/v1/*");
        registrationBean.setOrder(Integer.MAX_VALUE);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ExceptionPropagatorFilter> exceptionPropagatorFilterReg(){
        FilterRegistrationBean<ExceptionPropagatorFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(exceptionPropagatorFilter);
        registrationBean.addUrlPatterns("/api/v1/**");
        registrationBean.addUrlPatterns("/api/v1/*");
        registrationBean.setOrder(Integer.MAX_VALUE - 1);

        return registrationBean;
    }
}
