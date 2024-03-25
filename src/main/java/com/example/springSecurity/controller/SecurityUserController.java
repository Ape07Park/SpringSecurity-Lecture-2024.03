package com.example.springSecurity.controller;

import java.io.File;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AuthenticateAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.springSecurity.entity.SecurityUser;
import com.example.springSecurity.service.SecurityUserService;
import com.example.springSecurity.util.ImageUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class SecurityUserController {
	
	private final SecurityUserService securityService;
	private final BCryptPasswordEncoder bCryptEncoder;
	private final ImageUtil imageUtil;
 	
	// application Propertise에 존재
	@Value("${spring.servlet.multipart.location}") private String uploadDir;
	
	@GetMapping("/login")
	public String login() {
		return "user/login";
	}
	
	@GetMapping("/register")
	public String registerForm() {
		return "user/register";
	}
	
	@PostMapping("/register")
	public String registerProc(String uid, String pwd, String pwd2, String uname, 
			String email, MultipartHttpServletRequest req, Model model ) {
//		MultipartHttpServletRequest req: 사진 받기 위해 
		
		String filename = null;
		MultipartFile filePart = req.getFile("picture");
		
		SecurityUser securityUser = securityService.getUserByUid(uid);
		if (securityUser != null) {
			model.addAttribute("msg", "사용자 ID 중복");
			model.addAttribute("url", "/ss/user/register");
			return "common/alertMsg";
		}
		// 여기에 정규표현식으로 몇자리 이상, 대문자 꼭 넣기 등 적용 
		if (pwd == null || !pwd.equals(pwd2)) {
			model.addAttribute("msg", "password 입력이 잘못됨");
			model.addAttribute("url", "/ss/user/register");
			return "common/alertMsg";
		}
		
		if (filePart.getContentType().contains("image")) {
			filename = filePart.getOriginalFilename();
			String path = uploadDir + "profile/" + filename;
			try {
				filePart.transferTo(new File(path));
			} catch (Exception e) {
				e.printStackTrace();
			}
			filename = imageUtil.squareImage(uid, filename);
		}
		String hashedPwd = bCryptEncoder.encode(pwd);
		securityUser = SecurityUser.builder()
				.uid(uid).pwd(hashedPwd).uname(uname).email(email).provider("ck world")
				// * picture에 들어갈 이름 설정
				.picture("/ss/file/download/profile/" + filename) // filename만 넣는 경우도 존재
				.build();
		securityService.insertSecurityUser(securityUser);				
		model.addAttribute("msg", "등록을 마쳤습니다. 로그인하세요.");
		model.addAttribute("url", "/ss/user/login");
		return "common/alertMsg";
	}
	
	@ResponseBody
	@GetMapping("/loginSuccess")
	public String loginSuccess() {
		Authentication authenticateAction = SecurityContextHolder.getContext().getAuthentication();
		
		// 세션에 있는 현재 사용자 아이디
		String uid = authenticateAction.getName();
		
		return " loginSuccess - " + uid; 
		
	}
}
