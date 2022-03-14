package com.atguigu.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface OSSService {

	/**
	 * 文件上传至阿里云
	 */
	String upload(MultipartFile file);
}