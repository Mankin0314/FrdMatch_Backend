package com.dingjiaxiong.user_center_backend.config;

import com.baomidou.mybatisplus.annotation.DbType;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"com.dingjiaxiong.user_center_backend.mapper"})
public class MybatisPlusConfig {
 
  /**
   * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    //向Mybatis过滤器链中添加分页拦截器
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    //还可以添加i他的拦截器
    return interceptor;
  }

}