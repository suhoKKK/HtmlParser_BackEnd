package com.HtmlParser.Page.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class PageContoller {

	@GetMapping("/api/parsing")
	public Map htmlParsing(@RequestParam("url") String url) {
		
		LinkedHashMap<String, Object> ret = new LinkedHashMap<>();
		
		//selenium 라이브러리 사용하여 브라우저 html 접근
		WebDriver driver = new ChromeDriver();

		try {

	        driver.get(url);
	        
	       //태그 및 자바스크립트 소스를 제외한 컨텐츠만 추출
	        WebElement bodyElement = driver.findElement(By.tagName("body"));
	        String htmlBody = bodyElement.getText();

			//한칸 이상의 공백문자를 한칸짜리 공백문자로 바꾼 후 배열로 반환
			String processedBody = htmlBody.replaceAll("\\s+", " ");
			String[] contents = processedBody.split(" ");
			
			
			//콘텐츠 문자열과 반복된 횟수 집계
			Map<String, Integer> total = new HashMap<String, Integer>();
			
			for(String content : contents) {

				if(total.containsKey(content)) {
					total.put(content, total.get(content) + 1);
				} else {
					total.put(content, 1);
				}
			}
	        
			//내림차순으로 정렬하여 반환
	        
	        List<Map.Entry<String, Integer>> entries = new LinkedList<>(total.entrySet());
	        Collections.sort(entries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

	        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
	        for (Map.Entry<String, Integer> entry : entries) {
	            result.put(entry.getKey(), entry.getValue());
	        }
	        
	        //브라우저 드라이버 종료 
	        driver.quit();
	        
	        //집계한 정보 반환
//			return result;
	        
	        

	        // ObjectMapper 생성
	        ObjectMapper objectMapper = new ObjectMapper();

	        // LinkedHashMap을 JSON으로 변환
	        String json = objectMapper.writeValueAsString(result);
			
	        ret.put("resultCode", "200");
//	        ret.put("resultList", result);
	        ret.put("resultList", json);
	        ret.put("resultMessage", "정상적으로 처리되었습니다.");
	        return ret;
			
			
		} catch(Exception ex) {

//			return ex.getMessage();

	        //브라우저 드라이버 종료 
	        driver.quit();

//			return "입력한 url을 다시 확인해주십시오.";
	        ret.put("resultCode", "505");
	        ret.put("resultMessage", "입력한 url을 다시 확인해주십시오.");
	        return ret;
			
		}
		
	}
}
