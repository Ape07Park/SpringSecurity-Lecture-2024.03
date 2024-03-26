package com.example.springSecurity.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.springSecurity.entity.SecurityUser;
import com.example.springSecurity.service.SecurityUserService;
import com.example.springSecurity.util.AsideUtil;
import com.example.springSecurity.util.ImageUtil;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // log.info 쓸려고 만듦
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class SecurityUserController {

	// 생성자 방식의 의존성 주입
	private final SecurityUserService securityService;
	private final BCryptPasswordEncoder bCryptEncoder;
	private final ImageUtil imageUtil;
	private final AsideUtil asideUtil;
	private final ResourceLoader resourceLoader;

	// application Propertise에 존재
	@Value("${spring.servlet.multipart.location}")
	private String uploadDir;
	private String menu = "user";

	@GetMapping("/login")
	public String login() {
		return "user/login";
	}

	@GetMapping("/register")
	public String registerForm() {
		return "user/register";
	}

	@PostMapping("/register")
	public String registerProc(String uid, String pwd, String pwd2, String uname, String email,
			MultipartHttpServletRequest req, Model model) {
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
		securityUser = SecurityUser.builder().uid(uid).pwd(hashedPwd).uname(uname).email(email).provider("ck world")
				// * picture에 들어갈 이름 설정
				.picture("/ss/file/download/profile/" + filename) // filename만 넣는 경우도 존재
				.build();
		securityService.insertSecurityUser(securityUser);
		model.addAttribute("msg", "등록을 마쳤습니다. 로그인하세요.");
		model.addAttribute("url", "/ss/user/login");
		return "common/alertMsg";
	}

	@GetMapping("/loginSuccess")
	public String loginSuccess(HttpSession session, Model model) {
		/*
		 * 누가 로그인 했는지 확인
		 */
		Authentication authenticateAction = SecurityContextHolder.getContext().getAuthentication();

		// 세션에 있는 현재 사용자 아이디- uid 있으니 SecurityUser 가져옴
		String uid = authenticateAction.getName();

		/*
		 * 세션에 필요한 값 설정
		 */
		SecurityUser securityUser = securityService.getUserByUid(uid);

		session.setAttribute("sessUid", uid);
		session.setAttribute("sessUname", securityUser.getUname());
		session.setAttribute("picture", securityUser.getPicture());
		session.setAttribute("email", securityUser.getEmail());

		// 상태 메세지
		// resources/static/data/todayQuote.txt
		Resource resource = resourceLoader.getResource("classpath:/static/data/todayQuote.txt");
		String quoteFile = null;
		try {
			quoteFile = resource.getURI().getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String stateMsg = asideUtil.getTodayQuote(quoteFile);
		session.setAttribute("stateMsg", stateMsg);

		// 환영 메시지
		log.info("Info Login: {}, {}", uid, securityUser.getUname());
		model.addAttribute("msg", securityUser.getUname() + "님 환영합니다.");
		model.addAttribute("url", "/ss/user/list");

		return "common/alertMsg";
	}

	@GetMapping({ "/list/{page}", "/list" })
	public String list(@PathVariable(required = false) Integer page, HttpSession session, Model model) {
		page = (page == null) ? 1 : page;
		session.setAttribute("currentUserPage", page);
		List<SecurityUser> list = securityService.getSecurityUserList(page);
		model.addAttribute("userList", list);

		// for pagination
		int totalUsers = securityService.getSecurityUserCount();
		int totalPages = (int) Math.ceil(totalUsers * 1.0 / securityService.COUNT_PER_PAGE);
		List<Integer> pageList = new ArrayList<>();
		for (int i = 1; i <= totalPages; i++)
			pageList.add(i);
		model.addAttribute("pageList", pageList);
		model.addAttribute("menu", menu);
		
		return "user/list";
	}
	
	@ResponseBody
	@GetMapping("/detail/{uid}")
	public String detail(@PathVariable String uid) {
		SecurityUser securityUser = securityService.getUserByUid(uid);
		JSONObject jUser = new JSONObject();
		jUser.put("uid", uid);
		jUser.put("uid2", uid);
		jUser.put("hashedPwd", securityUser.getPwd());
		jUser.put("uname", securityUser.getUname());
		jUser.put("eamil", securityUser.getEmail());
		jUser.put("provider", securityUser.getProvider());
		jUser.put("role", securityUser.getRole());
		jUser.put("picture", securityUser.getPicture());
		
		return jUser.toString();
	}
}
