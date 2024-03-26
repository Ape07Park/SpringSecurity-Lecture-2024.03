package com.example.springSecurity.service;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.springSecurity.entity.MyUserDetails;
import com.example.springSecurity.entity.SecurityUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyOAuth2UserService extends DefaultOAuth2UserService {
	// 내 시큐리티 유저 정보가 있는지 확인을 위해 사용
	private final SecurityUserService securityService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	// Provider(구글, 깃허브 등)로부터 받은 userRequest 데이터에 대해 후처리하는 메소드
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		String uid, email, uname, picture;
		String hashedPwd = bCryptPasswordEncoder.encode("Social Login");
		SecurityUser securityUser = null;

		OAuth2User oAuth2User = super.loadUser(userRequest);

		// 권한을 찍어봄(어떤 정보들이 오는지 확인)
		log.info("getAttributes(): " + oAuth2User.getAttributes());

		String provider = userRequest.getClientRegistration().getRegistrationId();
		switch (provider) {
		case "google":
			String gId = oAuth2User.getAttribute("sub");
			uid = provider + "_" + gId;
			securityUser = securityService.getUserByUid(uid);

			if (securityUser == null) { // 가입 x 시 가입 진행
				uname = oAuth2User.getAttribute("name");
				
				uname = (uname == null) ? "google_user" : uname;
				email = oAuth2User.getAttribute("email");
				picture = oAuth2User.getAttribute("picture"); // http:// ~~

				securityUser = SecurityUser.builder().uid(uid).pwd(hashedPwd).uname(uname)
						.email(email).picture(picture)
						.provider(provider).build(); // role은 초기값으로 ROLE_USER 줌
				securityService.insertSecurityUser(securityUser);
				securityUser = securityService.getUserByUid(uid); // role, isDeleted 받기 위해
				log.info("구글 계정을 통한 회원가입 완료");
			}
			break;

		case "github":
			// id 받아옴-github_234235
			int id = oAuth2User.getAttribute("id");
			uid = provider + "_" + id;
			securityUser = securityService.getUserByUid(uid);
			if (securityUser == null) { // 가입 x 시 가입 진행
				uname = oAuth2User.getAttribute("name");
				// 깃허브에 개인 이름 설정(Daniel Park) x 시 github_user로 넣어서 저장해줌
				uname = (uname == null) ? "github_user" : uname;
				email = oAuth2User.getAttribute("email");
				picture = oAuth2User.getAttribute("avatar_url"); // http:// ~~
				securityUser = SecurityUser.builder().uid(uid).pwd(hashedPwd).uname(uname).email(email).picture(picture)
						.provider(provider).build(); // role은 초기값으로 ROLE_USER 줌
				securityService.insertSecurityUser(securityUser);
				securityUser = securityService.getUserByUid(uid); // role, isDeleted 받기 위해
				log.info("깃허브 계정을 통한 회원가입 완료");
			}

			break;
			
		// naver는 oAuth2User가 아닌 response로 받음
		case "naver":
			Map<String, Object> response = (Map) oAuth2User.getAttribute("response");
			String nid = (String) response.get("id");
			uid = provider + "_" + nid;
			
			securityUser = securityService.getUserByUid(uid);
			if (securityUser == null) { // 가입 x 시 가입 진행
				uname = (String) response.get("nickname");
				// 깃허브에 개인 이름 설정(Daniel Park) x 시 github_user로 넣어서 저장해줌
				uname = (uname == null) ? "naver_user" : uname;
				email = (String) response.get("email");
				picture = (String) response.get("profile_image"); // http:// ~~
				securityUser = SecurityUser.builder().uid(uid).pwd(hashedPwd).uname(uname).email(email).picture(picture)
						.provider(provider).build(); // role은 초기값으로 ROLE_USER 줌
				
				securityService.insertSecurityUser(securityUser);
				securityUser = securityService.getUserByUid(uid); // role, isDeleted 받기 위해
				log.info("네이버 계정을 통한 회원가입 완료");
			}
			break;

		case "kakao":
			long kid = (long) oAuth2User.getAttribute("id");
			
			uid = provider + "_" + kid;		
			securityUser = securityService.getUserByUid(uid);
			
			if (securityUser == null) { // 가입 x 시 가입 진행
				Map<String, String> properties = (Map) oAuth2User.getAttribute("properties");
				Map<String, Object> account = (Map) oAuth2User.getAttribute("kakao_account");
				
				uname = (String) properties.get("nickname");
				// 깃허브에 개인 이름 설정(Daniel Park) x 시 github_user로 넣어서 저장해줌
				uname = (uname == null) ? "kakao_user" : uname;
				
				email = (String) account.get("email");
				picture = (String) properties.get("profile_image"); // http:// ~~
				securityUser = SecurityUser.builder().uid(uid).pwd(hashedPwd).uname(uname).email(email).picture(picture)
						.provider(provider).build(); // role은 초기값으로 ROLE_USER 줌
				
				securityService.insertSecurityUser(securityUser);
				securityUser = securityService.getUserByUid(uid); // role, isDeleted 받기 위해
				log.info("카카오 계정을 통한 회원가입 완료");
			}
			break;
			
		// * picture는 facebook에서 프로필 사진 제공 x로 만들지 못함 
		case "facebook":
			
			// * facebook id는 숫자로만 되어 있지만 String임
			String fid = oAuth2User.getAttribute("id");
			uid = provider + "_" + fid;
			securityUser = securityService.getUserByUid(uid);
			if (securityUser == null) { // 가입 x 시 가입 진행
				uname = oAuth2User.getAttribute("name");
				// 깃허브에 개인 이름 설정(Daniel Park) x 시 github_user로 넣어서 저장해줌
				uname = (uname == null) ? "facebook_user" : uname;
				email = oAuth2User.getAttribute("email");
//				picture = null;
				securityUser = SecurityUser.builder().uid(uid).pwd(hashedPwd).uname(uname).email(email)
						.provider(provider)
//						.picture(picture)
						.build(); // role은 초기값으로 ROLE_USER 줌
				
				securityService.insertSecurityUser(securityUser);
				securityUser = securityService.getUserByUid(uid); // role, isDeleted 받기 위해
				log.info("페이스북 계정을 통한 회원가입 완료");
			}
			break;

		}

		return new MyUserDetails(securityUser, oAuth2User.getAttributes());
	}

}
