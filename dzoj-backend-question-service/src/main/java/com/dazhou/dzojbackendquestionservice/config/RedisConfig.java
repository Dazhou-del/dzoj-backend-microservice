//package com.dazhou.dzojbackendquestionservice.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.BeanClassLoaderAware;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.SerializationException;
//import reactor.util.annotation.Nullable;
//
//import javax.validation.constraints.NotNull;
//
///**
// * @author dazhou
// * @author dazhou
// * @create 2023-08-31 9:59
// */
//@Configuration
//@Slf4j
//public class RedisConfig implements BeanClassLoaderAware {
//
//    private ClassLoader classLoader;
//
//    @Bean("springSessionDefaultRedisSerializer")
//    public RedisSerializer<Object> getRedisSerializer() {
//        return new JdkSerializationRedisSerializer(classLoader) {
//            @Override
//            public Object deserialize(@Nullable byte[] bytes) {
//                try {
//                    return super.deserialize(bytes);
//                } catch (SerializationException ex) {
//                    log.error(ex.getMessage());
//                }
//                return null;
//            }
//        };
//    }
//
//    @Override
//    public void setBeanClassLoader(@NotNull ClassLoader classLoader) {
//        this.classLoader = classLoader;
//    }
//}
