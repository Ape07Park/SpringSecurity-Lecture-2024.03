package com.example.springSecurity.util;

public class OsName {
	// os의 이름 가져오기. 이걸 잘 활용해서 os가 어떤 것인지 확인할 수 있게 하기 
	public static void main(String[] args) {
		String osName = System.getProperty("os.name").toLowerCase();
		System.out.println(osName);
	}

}
