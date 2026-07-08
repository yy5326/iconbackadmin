package com.golf.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.golf.service.model.GolfFileVO;
import com.golf.model.common.BaseFileVO;
import com.golf.util.StringUtil;
import com.golf.util.UploadFile;
import com.google.gson.Gson;

/**
 * 픽업사진등록
 * @author khs
 *
 */
@Service("ReceiptCamService")
public class GolfFileServiceImpl extends BaseServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(GolfFileServiceImpl.class);

	@Autowired
	private UploadFile _upload;
	
	/**
	 * 파일업로드
	 * @param mstVO
	 * @param list
	 * @throws Exception
	 */
	public void insertFile(GolfFileVO vo) throws Exception {
		List<GolfFileVO> files = new ArrayList<GolfFileVO>();
		String server = super.getConfig().getWebMode();
		String rcpInfSeq = vo.getRcpInfSeq();
		String sNewPath = super.getConfig().getUploadPathFiles() + "/" + vo.getFileCode() + "/" + vo.getRcpInfSeq();
		if(StringUtil.isEmpty(rcpInfSeq)) {
			throw new Exception("파일업로드에 필요한 참조키 발송순번이 없습니다.");
		}

		try {
			if (vo.getMultiFile() != null && vo.getMultiFile().size() > 0) {
				List<BaseFileVO> bfs = _upload.saveFiles(vo.getMultiFile(), null, sNewPath);
				for(int i = 0; i < bfs.size(); i++) {
					if (bfs.get(i).getSize() == 0)
						continue;
					GolfFileVO f = (GolfFileVO) bfs.get(i);
					f.setRcpInfSeq(vo.getRcpInfSeq());
					f.setFileCode(vo.getFileCode());
					f.setFileNm(bfs.get(i).getFileName());
					f.setFileSvNm(bfs.get(i).getPhysicalName());
					f.setFileContType(bfs.get(i).getContentType());
					f.setFileSize(bfs.get(i).getSize());

					if(!super.getConfig().getWebMode().equals("local")) { 
						f.setRegUser(vo.getRegUser());
					}else {
						f.setRegUser("local");
					}

					files.add(f);
				}
			}
			//기존 파일 업데이트 - 기존 파일 삭제
			List <GolfFileVO> oldFileVO = (List<GolfFileVO>) getDAO().selectList("golfFile.selectByKey", vo.getRcpInfSeq());
			if (oldFileVO.size()>0) {
				try {
					for(GolfFileVO o : oldFileVO) {
						File oldFile = new File(super.getConfig().getUploadPathFiles() + "/" + o.getFileCode() + "/" + o.getRcpInfSeq() + "/" + o.getFileSvNm());
						getDAO().update("golfFile.updateFile", o);
						if (oldFile.exists()) {
							logger.info("기존 파일 삭제 처리 - GolfFileVO(saveUploadFiles) : {}", new Gson().toJson(o));
							oldFile.delete();
						}
					}
				}catch (Exception e) {
					logger.error(">>>> 파일 없음 : {}", e.getMessage());
				}
			}
			
			for (GolfFileVO f : files) {
				getDAO().insert("golfFile.insertFile", f);
			}
		}catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
}
