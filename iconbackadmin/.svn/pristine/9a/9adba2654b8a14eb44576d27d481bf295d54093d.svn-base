package com.golf.service.dao;

import java.util.List;
import java.util.Map;

import com.golf.model.GeneralModel;
import com.golf.model.common.SearchVO;

/**
 * dao 클래스 일반화를 위한 인터페이스
 * 
 * @author wimy
 *
 */
public interface GeneralDAO {

	int getCountBySearch(String sqlMapID, SearchVO search);

	Object selectByKey(String sqlMapID, Object key);

	// List<?> selectByObjects(String sqlMapID, Object...objects);

	List<?> selectBySearch(String sqlMapID, SearchVO search);

	int insert(String sqlMapID, Object obj);
	
	int insert(String sqlMapID, GeneralModel model);
	
	int update(String sqlMapID, Object obj);
	
	int update(String sqlMapID, GeneralModel model);

	int delete(String sqlMapID, Object key);
	
	int delete(String sqlMapID, GeneralModel model);
	
	int insert(String sqlMapID, List<Map<String, Object>> model);
}
