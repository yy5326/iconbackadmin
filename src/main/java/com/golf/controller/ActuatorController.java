package com.golf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 서비스의 ActuatorController
 *
 */
@Controller
@RequestMapping(value="/monitoring")
public class ActuatorController {

	private static final Logger logger = LoggerFactory.getLogger(ActuatorController.class);

	/**
	 * health 체크
	 * @return String
	 */
	@GetMapping(value = "/health_check.do")
	@ResponseBody
	public String getHealthCheck() {

		logger.info("##### health check called.");

		return HttpStatus.OK.getReasonPhrase();
	}
}
