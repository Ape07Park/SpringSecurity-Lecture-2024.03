package com.example.springSecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.springSecurity.entity.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j // log.info
@Controller
@RequestMapping("/member")
public class MemberController {
	
	@ResponseBody
	@GetMapping("/detail/{mid}")
	public String detail(@PathVariable int mid) {
		Member member = new Member();
		log.info("detail");
	
		return "";		
	}

	@ResponseBody
	@GetMapping("/insert")
	public String insert() {
		Member m1 = new Member();
		m1.setName("james"); m1.setEmail("james@gmail.com");
		log.info(m1.toString());
		
		// Builder pattern: 인스턴스 생성 시 필드가 많을 때 유용 (.필드 이름(값)) 
		Member m2 = Member.builder() // builder()는 스태틱 메소드 
						.name("maria").email("maria@naver.com")
						.build();
		log.info(m2.toString());
		
		return m1.toString() + "<br>" + m2.toString();
	}
	
	@ResponseBody
	@GetMapping("/update")
	public String update() {
		Member member = Member.builder()
							.mid(1).name("Brian").email("brian@human.com")
							.build();
		log.info(member.toString());
		
		return member.toString();
	}
}
