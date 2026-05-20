package com.example.webflow.config;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.config.AbstractFlowConfiguration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;
import org.springframework.webflow.mvc.servlet.FlowHandlerMapping;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.webflow.view.AjaxThymeleafViewResolver;

/**
 * Cau hinh Spring Web Flow.
 *
 * Cac thanh phan chinh:
 * 1. FlowDefinitionRegistry: dang ky cac flow definition (XML)
 * 2. FlowExecutor: thuc thi cac flow da dang ky
 * 3. FlowHandlerMapping: anh xa URL request toi flow
 * 4. FlowHandlerAdapter: xu ly flow request va tra ve response
 *
 * Luu y:
 * Mac dinh Spring Web Flow se tim view tuong doi theo thu muc cua flow.
 * Trong Spring Boot + Thymeleaf, chung ta can noi Web Flow voi Thymeleaf
 * ViewResolver de render file HTML trong /templates.
 */
@Configuration
public class WebFlowConfig extends AbstractFlowConfiguration {

    // Dang ky tat ca cac flow XML.
    // setBasePath: thu muc goc chua cac file flow XML
    // addFlowLocationPattern: pattern tim kiem file flow
    // 
    @Bean
    public FlowDefinitionRegistry flowRegistry(FlowBuilderServices flowBuilderServices) {
        return getFlowDefinitionRegistryBuilder()
                .setBasePath("classpath:flows")
                .addFlowLocationPattern("/**/*-flow.xml")
                .setFlowBuilderServices(flowBuilderServices)
                .build();
    }

    // FlowBuilderServices: cung cap cac dich vu de xay dung flow.
    // viewFactoryCreator: tao view cho flow.
    @Bean
    public FlowBuilderServices flowBuilderServices(AjaxThymeleafViewResolver flowAjaxThymeleafViewResolver) {
        MvcViewFactoryCreator viewFactoryCreator = new MvcViewFactoryCreator();
        viewFactoryCreator.setUseSpringBeanBinding(true);
        viewFactoryCreator.setViewResolvers(Collections.singletonList(flowAjaxThymeleafViewResolver));

        return getFlowBuilderServicesBuilder()
                .setViewFactoryCreator(viewFactoryCreator)
                .setDevelopmentMode(true)
                .build();
    }

    // FlowExecutor: thuc thi flow.
    // Khi mot request den, FlowExecutor se:
    // 1. Tim flow definition trong registry
    // 2. Tao flow execution moi hoac resume execution hien tai
    // 3. Xu ly cac state transition
    @Bean
    public FlowExecutor flowExecutor(FlowDefinitionRegistry flowRegistry) {
        return getFlowExecutorBuilder(flowRegistry).build();
    }

    // FlowHandlerMapping: anh xa URL toi Flow.
    // Vi du: URL "/login" se duoc anh xa toi flow co id "login"
    // order = -1: dam bao FlowHandlerMapping duoc uu tien truoc cac handler mapping khac.
    @Bean
    public FlowHandlerMapping flowHandlerMapping(FlowDefinitionRegistry flowRegistry) {
        FlowHandlerMapping handlerMapping = new FlowHandlerMapping();
        handlerMapping.setOrder(-1);
        handlerMapping.setFlowRegistry(flowRegistry);
        return handlerMapping;
    }

    // FlowHandlerAdapter: adapter giua DispatcherServlet va FlowExecutor.
    // Chuyen doi HTTP request thanh flow execution request
    // va flow execution response thanh HTTP response.
    @Bean
    public FlowHandlerAdapter flowHandlerAdapter(FlowExecutor flowExecutor) {
        FlowHandlerAdapter handlerAdapter = new FlowHandlerAdapter();
        handlerAdapter.setFlowExecutor(flowExecutor);
        handlerAdapter.setSaveOutputToFlashScopeOnRedirect(true);
        return handlerAdapter;
    }

    /**
     * ViewResolver rieng cho Web Flow.
     * Dung AjaxThymeleafViewResolver de Web Flow render duoc Thymeleaf template.
     */
    @Bean
    public AjaxThymeleafViewResolver flowAjaxThymeleafViewResolver(SpringTemplateEngine templateEngine) {
        AjaxThymeleafViewResolver viewResolver = new AjaxThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        viewResolver.setOrder(1);
        return viewResolver;
    }
}
