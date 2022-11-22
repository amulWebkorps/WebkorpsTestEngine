package com.codecompiler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableMongoRepositories
@EnableCaching
public class CodeCompilerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeCompilerApplication.class, args);
		
		ArrayList<String> list=new ArrayList();
		  list.add("Ramji");
		  
		  list.add("sdsasce");
		  List<String> filter = list.stream().filter(s->s.equals("si")).collect(Collectors.toList());
		int tempLen=0;
		String longString="";
		
		for(int i=0;i<list.size();i++){
			String temp=""+list.get(i);
			if(temp.length()>tempLen){
			 tempLen=temp.length();
			longString=""+list.get(i);
		}}
		
		System.out.println(longString);
		System.out.println(filter);

	}

}
