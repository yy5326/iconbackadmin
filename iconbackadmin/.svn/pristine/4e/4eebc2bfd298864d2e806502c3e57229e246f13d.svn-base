package com.golf.service.impl;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.golf.service.GlobalProperty;
import com.golf.service.dao.GeneralDAOImpl;


/**
 * 부모 serviceImpl 클래스
 * @author wimy
 *
 */
@Service
public abstract class BaseServiceImpl {
	
	@Resource
	private GeneralDAOImpl _gDao;
		
	@Autowired
	private GlobalProperty _globalProperty;
	
	@Autowired
	private HttpSession _session;
		
	protected GeneralDAOImpl getDAO() {
		return this._gDao;
	}
	
	protected GlobalProperty getConfig() {
		return this._globalProperty;
	}
	
	protected HttpSession getSession() {
		return this._session;
	}
	
	protected ServletContext getServletContext() {
		return this.getSession().getServletContext();
	}
		
	public static String convertToDate(String inputDate) throws Exception{
		if(inputDate !=null) {
			inputDate = inputDate.replaceAll("T", " ");
			inputDate = inputDate.replaceAll("Z", " ");
		}
		return inputDate; 
	}

}
