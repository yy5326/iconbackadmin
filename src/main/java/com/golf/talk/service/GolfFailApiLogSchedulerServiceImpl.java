package com.golf.talk.service;

import java.util.List;

import javax.annotation.Resource;

import com.golf.model.RentalHistoryVO;
import com.golf.service.GlobalProperty;
import com.golf.service.impl.BaseServiceImpl;
import com.golf.service.impl.KdngeAPI;
import com.golf.service.model.GolfReceiptInfVO;
import com.google.gson.Gson;
import egovframework.rte.psl.dataaccess.util.EgovMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.golf.service.dao.GeneralDAOImpl;

@Service("golfFailApiLogSchedule")
@RequiredArgsConstructor
public class GolfFailApiLogSchedulerServiceImpl extends BaseServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(GolfFailApiLogSchedulerServiceImpl.class);

    @Resource(name="globalProperty")
    GlobalProperty configBean;

    private final KdngeAPI _kdngeApi;

    @Resource
    private GeneralDAOImpl _gDao;

    public void insertFailApiLogs() {
        try {
            List<GolfReceiptInfVO> failLogs = (List<GolfReceiptInfVO>) _gDao.selectList("golfHistory.selectFailLogList", null);

            if (failLogs != null && !failLogs.isEmpty()) {
                for (GolfReceiptInfVO log : failLogs) {
                    try {
                        String url = getHost() + "/api/icon-golf/response/" + log.getType();

                        EgovMap apiMap = _kdngeApi.callUrl(url, new Gson().toJson(log), "POST");
                        String code = String.valueOf(apiMap.get("code"));

                        if ("200".equals(code)) {
                            _gDao.update("golfHistory.updateFailHistory", log);
                        }

                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            logger.error("GolfFailApiLogScheduler Exception: ", e);
        }
    }
    public String getHost() {
        if (configBean.getWebMode().equals("dev"))
            return "http://211.34.229.132:18087";
        if (configBean.getWebMode().equals("local"))
            return "http://localhost:2222";
        else if (configBean.getWebMode().equals("op"))
            return "";
        else
            return "";
    }
}