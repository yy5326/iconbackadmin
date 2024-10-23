package com.golf.service.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.golf.model.GeneralModel;
import com.golf.model.common.SearchVO;

/**
 * 일반화 DAO 클래스 : 이 클래스를 상속해서 DAO를 Service티어에서 Autowired, Repository를 적용할 경우 spring DI 오류가 발생한다. - @Autowired, @Repository 대신 new 생성자를 활용해야 한다.
 * 		- 이 문제는 ApplicationContext레벨에서 Repository, Serivce만 스캔, ServletContext레벨에서 Controller만 스캔할 경우 발생한다.
 * 		- dispatcher 레벨에서 전체 컴퍼넌트 스캔(component-scan)을 적용할 경우 DI를 정상적으로 적용할 수 있다.
 * 
 * @author wimy
 *
 */
@Repository
public class GeneralDAOImpl extends SqlSessionDaoSupport implements GeneralDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(GeneralDAOImpl.class);
	
//	@Autowired
//	@Override
//	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
//		super.setSqlSessionFactory(sqlSessionFactory);
//	};
	
	@Resource(name="sqlSessionTemplate")
	@Override
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		super.setSqlSessionTemplate(sqlSessionTemplate);
	};
	
	
	
	
	/**
	 * 1 레코드 결과를 가져옵니다.
	 *  - N건의 결과가 나오면 오류가 발생합니다.
	 * @param sqlMapID
	 * @param obj
	 * @return
	 */
	public Object selectOne(String sqlMapID, Object obj) {
		Object result = getSqlSession().selectOne(sqlMapID, obj);
		return result;
	}
	
	/**
	 * 검색 레코드 수 search.paging.totalCount 세팅을 포함합니다.
	 */
	@Override
	public int getCountBySearch(String sqlMapID, SearchVO search) {
		int iCount = 0;
		iCount = getSqlSession().selectOne(sqlMapID, search);
		search.getPaging().setTotalCount(iCount);
		return iCount;
	}
	
	/**
	 * 1 레코드 결과를 가져옵니다. 없을 경우 null을 반환합니다.
	 *   - ResultSet 결과의 첫 번째 레코드를 가져옵니다.
	 */
	@Override
	public Object selectByKey(String sqlMapID, Object key) {
		List<?> list = getSqlSession().selectList(sqlMapID, key);
		
		if (list == null || list.isEmpty() || list.size() == 0)
			return null;
		else
			return list.get(0);
	}
	
	/**
	 * 목록 가져오기 - 호출될 때 카운트 쿼리도 같이 실행. search.paging.totalCount에 매핑 - sqlMapID가 맞아야 함.
	 * 
	 * @param sqlMapID
	 * @param search
	 */
	@Override
	public List<?> selectBySearch(String sqlMapID, SearchVO search) {
		return this.selectBySearch(sqlMapID, search, "totalCount");
	}
	
	/**
	 * 목록 가져오기 - 호출될 때 카운트 쿼리도 같이 실행. search.paging.totalCount에 매핑 - sqlMapID가 맞아야 함. 
	 * 
	 * @param sqlMapID
	 * @param search
	 * @param countQueryID
	 *            카운트 쿼리 id를 넣을 경우 해당 쿼리를 실행하여 search.paging.totalCount에 결과값 매핑. 빈 값을 넣으면 카운트 쿼리를 실행하지 않음
	 * @return
	 */
	public List<?> selectBySearch(String sqlMapID, SearchVO search, String countQueryID) {
		if (!StringUtils.isEmpty(countQueryID)) {
			try {
				String mapID = sqlMapID.substring(0, sqlMapID.lastIndexOf(".") + 1) + countQueryID;
				int iCount = this.getCountBySearch(mapID, search);
				search.getPaging().setTotalCount(iCount);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}			
		}
		
		return getSqlSession().selectList(sqlMapID, search);
	}
	
	/**
	 * 쿼리에 해당하는 리스트를 가져옵니다.
	 * @param sqlMapID
	 * @param search
	 * @return
	 */
	public List<?> selectList(String sqlMapID, SearchVO search) {
		return getSqlSession().selectList(sqlMapID, search);
	}
	
	/**
	 * 특정 모델을 파라미터에 해당하는 리스트를 가져옵니다.
	 * @param sqlMapID
	 * @param vo
	 * @return
	 */
	public List<?> selectList(String sqlMapID, Object vo) {
		return getSqlSession().selectList(sqlMapID, vo);
	}
	
	/**
	 * 등록
	 */
	@Override
	public int insert(String sqlMapID, Object obj) {
		return getSqlSession().insert(sqlMapID, obj);
	}
	
	/**
	 * 등록
	 */
	@Override
	public int insert(String sqlMapID, GeneralModel model) {
		return getSqlSession().insert(sqlMapID, model);
	}
	
	/**
	 * 등록
	 */
	@Override
	public int insert(String sqlMapID,  List<Map<String, Object>> dbListMap){
		return getSqlSession().insert(sqlMapID, dbListMap);
	}
	
	/**
	 * 수정
	 */
	@Override
	public int update(String sqlMapID, GeneralModel model) {
		return getSqlSession().update(sqlMapID, model);
	}
	
	/**
	 * 수정
	 */
	public int update(String sqlMapID, Object obj) {
		return getSqlSession().update(sqlMapID, obj);
	}
	
	/**
	 * 삭제
	 */
	@Override
	public int delete(String sqlMapID, Object key) {
		return getSqlSession().delete(sqlMapID, key);
	}
	
	/**
	 * 삭제
	 */
	@Override
	public int delete(String sqlMapID, GeneralModel model) {
		return getSqlSession().delete(sqlMapID, model);
	}
}
