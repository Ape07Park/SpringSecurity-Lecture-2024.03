package com.example.springSecurity.controller;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


@Controller
@RequestMapping("/file")
public class FIleController {
	@Value("${spring.servlet.multipart.location}") private String uploadDir;
	
	// 같은 게 여러 개(다운로드, 업로드, 파일네임)
	@GetMapping("/download/{dir}/{filename}")
	public ResponseEntity<Resource> profile(@PathVariable String dir, @PathVariable String filename) {
	      Path path = Paths.get(uploadDir + dir + "/" + filename);
	      try {
	         String contentType = Files.probeContentType(path);
	         HttpHeaders headers = new HttpHeaders();
	         headers.setContentDisposition(
	               ContentDisposition.builder("attachment")
	                              .filename(filename, StandardCharsets.UTF_8)
	                              .build()
	               );
	         headers.add(HttpHeaders.CONTENT_TYPE, contentType);
	         Resource resource = new InputStreamResource(Files.newInputStream(path));
	         return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return null;
	   }
	
	
	// 프로파일 이미지 보여주는 곳
	@GetMapping("/profile/{filename}")
	 public ResponseEntity<Resource> profile(@PathVariable String filename) {
	      Path path = Paths.get(uploadDir + "profile/" + filename);
	      try {
	         String contentType = Files.probeContentType(path);
	         HttpHeaders headers = new HttpHeaders();
	         headers.setContentDisposition(
	               ContentDisposition.builder("attachment")
	                              .filename(filename, StandardCharsets.UTF_8)
	                              .build()
	               );
	         headers.add(HttpHeaders.CONTENT_TYPE, contentType);
	         Resource resource = new InputStreamResource(Files.newInputStream(path));
	         return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return null;
	   }
	
	// 첨부파일 다운받는 곳
//	@GetMapping("/download/{filename}")
//public ResponseEntity<Resource> download(@PathVariable String filename){
//		 Path path = Paths.get(uploadDir + "image/" + filename);
//	      try {
//	         String contentType = Files.probeContentType(path);
//	         HttpHeaders headers = new HttpHeaders();
//	         headers.setContentDisposition(
//	               ContentDisposition.builder("attachment")
//	                              .filename(filename, StandardCharsets.UTF_8)
//	                              .build()
//	               );
//	         headers.add(HttpHeaders.CONTENT_TYPE, contentType);
//	         Resource resource = new InputStreamResource(Files.newInputStream(path));
//	         return new ResponseEntity<>(resource, headers, HttpStatus.OK);
//	      } catch (Exception e) {
//	         e.printStackTrace();
//	      }
//		return null;
//	}
	
	@ResponseBody  //ajax에서 동작해야 해서 
	@PostMapping("/imageUpload") // insert가 전송방식이 post라 postMapping 사용
	public String imageUpload(MultipartHttpServletRequest req) {
		String callback = req.getParameter("CKEditorFuncNum"); // 숫자 1 
		String error = "";
		String url = null; // 다운로드 할 주소
		Map<String, MultipartFile> map = req.getFileMap();
		
		// map을 반복하고 특정 타입은 file이라 인식하고 받음
		for (Map.Entry<String, MultipartFile> pair: map.entrySet()) {
			MultipartFile file = pair.getValue();
			String filename = file.getOriginalFilename();
			int idx = filename.lastIndexOf(".");
			filename = System.currentTimeMillis() + filename.substring(idx); // 밀리 초만큼 보이는 것 + idx부터 끝까지
			String uploadPath = uploadDir + "image/" + filename;
			try {
				file.transferTo(new File(uploadPath));
			} catch (Exception e) {
				e.printStackTrace();
			}
			url = "/ss/file/download/image/" + filename; 
		}
		 
		String ajaxResponse = "<script>"
	            + "   window.parent.CKEDITOR.tools.callFunction("
	            +       callback + ", '" + url + "', '" + error + "'"
	            + "   );"
	            + "</script>";
		
		return ajaxResponse;
	}
}
