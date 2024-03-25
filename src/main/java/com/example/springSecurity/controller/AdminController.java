package com.example.springSecurity.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.springSecurity.entity.SecurityUser;
import com.example.springSecurity.service.SecurityUserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	private final SecurityUserService securityUserService;
	
	@ResponseBody
	@GetMapping("/show")
	public String show() {
		
		return "<h1>/admin/show</h1>";
	}
	
	@GetMapping({"/userList/{page}", "/userList"})
	public String getUserList(@PathVariable(required=false) Integer page, HttpSession session, Model model) {
		page = (page == null) ? 1 : page;
		session.setAttribute("currentUserPage", page);
		List<SecurityUser> list = securityUserService.getSecurityUserList(page);
		
		model.addAttribute("userList", list);
		System.out.println(list);
		
		// for pagination
		int totalUsers = securityUserService.getSecurityUserCount();
		int totalPages = (int) Math.ceil(totalUsers * 1.0 / securityUserService.COUNT_PER_PAGE);
		List<Integer> pageList = new ArrayList<>();
		for (int i = 1; i <= totalPages; i++)
			pageList.add(i);
		model.addAttribute("pageList", pageList);
		
		return "admin/userList";
		
	}
}
