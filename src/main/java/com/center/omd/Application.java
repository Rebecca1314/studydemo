package com.center.omd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.Charset;

/**
 * 推荐启动的方式
 * @EnableAutoConfiguration
 * 第二种启动方式
 * scanBasePackages：配置扫描的jar包
 * 配置扫描dao层的并注入bean对象 "com.owner.backstage.core.bsc.dao.*"
 */
@SpringBootApplication(scanBasePackages = {"com.center.omd.*","com.owner.backstage.core.bsc.dao.*"},exclude = {DataSourceAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(Application.class,args);
	/*
	 * 关闭打印的banner信息
		SpringApplication app = new SpringApplication(Application.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	*/

	}
	/**
	 * @MethodName configure
	 * @date 2021/2/28 16:25
	 * @param [builder]
	 * 注意部署war包时要继承SpringBootServletInitializer，重写configure方法
	 * 否则只是解压war包，不会加载启动项目
	 * 部署war包时需要重新加载此方法
	 * @return org.springframework.boot.builder.SpringApplicationBuilder
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Application.class);
	}

	//自定义消息转化器，只需要在类中添加消息转化器的@Bean,就会被SpringBoot自动加入到容器中
	@Bean
	public StringHttpMessageConverter stringHttpMessageConverter() {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
		return stringHttpMessageConverter;
	}







}
