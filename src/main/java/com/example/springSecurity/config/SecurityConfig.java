package com.example.springSecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration // @을 통해 pom.xml을 자바 코드로 불러옴
@EnableWebSecurity
public class SecurityConfig {

	@Bean // 주입 가능하게 함
	public DefaultSecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(auto -> auto.disable()) // 괄호 안에 람다함수를 사용해야함, csrf:보안 관련
											// build 패턴과 유사하게 . . . 으로 간다
				.headers(x -> x.frameOptions(y -> y.disable())) // CK Editor image upload
				.authorizeHttpRequests(auto -> auto // 지원 중단될 수 있음

						/*
						 * 튕겨서 들어온 건 헤더에 FORWARD 달고 옴 따라서 FORWARD 달고있는 건 통과 permitAll(): 들어오는 거 허용
						 */
						.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()

						// 여기 표시된 건 로그인 안해도 아무나 다 들어갈 수 있음 더 필요하면 더 추가 o
						.requestMatchers("/user/register", "/img/**", "/css/**", "/js/**", "/error/**")
						.permitAll() // img 밑의 모든 것들

						// 관리자 권한 주기
						.requestMatchers("/admin/**").hasAuthority("ADMIN").anyRequest().authenticated())
					.formLogin(auth -> auth
						// login page 지정
						.loginPage("/user/login") // login 폼
						.loginProcessingUrl("/user/login") // 내가 만드는 것 x 스프링 시큐리티가 낚아 챔. -> UserDetailsService 구현 객체에서
														// 처리 필요
						// 아이디, 비번 파라미터 뭘로 쓸 건지 
						.usernameParameter("uid") 
						.passwordParameter("pwd")
						.defaultSuccessUrl("/user/loginSuccess", true) // 내가 로그인 후 해야할 일 ex) 로그인 끝나고 세션 세팅, 오늘의 메세지 등						
						.permitAll()
				)
			;
			

		return http.build();
	}
}
