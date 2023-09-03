//package com.dazhou.dzojbackendmodel.model.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.math.BigInteger;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
///**
// * @author dazhou
// * @title
// * @create 2023-08-31 17:27
// */
//@EnableWebMvc
//@Configuration
//public class MvcConfig implements WebMvcConfigurer {
//    /**
//     * 重写Jackson转换器
//     * Long类型转String类型
//     *
//     * 解决前端Long类型精度丢失问题（js解析只能解析到16位）
//     *
//     * @param converters
//     */
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =
//                new MappingJackson2HttpMessageConverter();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//        objectMapper.registerModule(simpleModule);
//        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
//        converters.add(jackson2HttpMessageConverter);
//        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
//    }
//}
