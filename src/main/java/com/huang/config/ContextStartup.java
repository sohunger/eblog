package com.huang.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huang.entity.MCategory;
import com.huang.entity.MCategory;
import com.huang.service.MCategoryService;
import com.huang.service.MCategoryService;
import com.huang.service.MPostService;
import com.huang.service.MPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.List;

@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {

    @Autowired
    MCategoryService mCategoryService;

    ServletContext servletContext;

    @Autowired
    MPostService postService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<MCategory> categories = mCategoryService.list(new QueryWrapper<MCategory>()
                .eq("status", 0)
        );
        servletContext.setAttribute("categorys", categories);
        postService.initWeekRank();

    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
