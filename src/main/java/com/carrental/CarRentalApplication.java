package com.carrental;


import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;


@SpringBootApplication
@EnableAsync
public class CarRentalApplication  {

	public static void main(String[] args) {
		ApplicationContext context =  SpringApplication.run(CarRentalApplication.class, args);
//	     String [] beanNames = context.getBeanDefinitionNames();
//		Arrays.sort(beanNames);
//
//		for (String beanName : beanNames) {
//			System.out.println("beanName = " + beanName);
//		}
	}


}
