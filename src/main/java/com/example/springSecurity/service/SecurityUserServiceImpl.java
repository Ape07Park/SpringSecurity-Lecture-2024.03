package com.example.springSecurity.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springSecurity.dao.SecurityUserDao;
import com.example.springSecurity.entity.SecurityUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements SecurityUserService {
//	private SecurityUserDao securityDao;
//	
//	// 스프링에서 권장하는 방식 
//	@Autowired
//	public SecurityUserServiceImpl( SecurityUserDao securityDao) {
//		this.securityDao = securityDao;
//	}
	
	private final SecurityUserDao securityDao; // 파이널 타입으로 선언 시 롬복의 @RequiredArgsConstructor를 통해 자동으로 주입 
	
	@Override
	public SecurityUser getUserByUid(String uid) {
		return securityDao.getUserByUid(uid);
	}

	@Override
	public List<SecurityUser> getSecurityUserList(int page) {
		int count = COUNT_PER_PAGE;
		int offset = (page - 1) * COUNT_PER_PAGE;
		
		return securityDao.getSecurityUserList(count, offset);
	}

	@Override
	public int getSecurityUserCount() {
		return securityDao.getSecurityUserCount();
	}

	@Override
	public void insertSecurityUser(SecurityUser securityUser) {
		securityDao.insertSecurityUser(securityUser);
		
	}

	@Override
	public void updateSecurityUser(SecurityUser securityUser) {
		securityDao.updateSecurityUser(securityUser);
		
	}

	@Override
	public void deleteSecurityUser(String uid) {
		securityDao.deleteSecurityUser(uid);
		
	}
	
}
