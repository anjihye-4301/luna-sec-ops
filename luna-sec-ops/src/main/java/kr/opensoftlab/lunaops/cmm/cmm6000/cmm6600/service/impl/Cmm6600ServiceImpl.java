package kr.opensoftlab.lunaops.cmm.cmm6000.cmm6600.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import egovframework.com.cmm.service.impl.FileManageDAO;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.rte.fdl.idgnr.EgovIdGnrService;
import kr.opensoftlab.lunaops.arm.arm1000.arm1100.service.impl.Arm1100DAO;
import kr.opensoftlab.lunaops.cmm.cmm6000.cmm6600.service.Cmm6600Service;
import kr.opensoftlab.lunaops.com.fms.web.service.FileMngService;



@Service("cmm6600Service")
public class Cmm6600ServiceImpl extends EgovAbstractServiceImpl implements Cmm6600Service {

	
   	@Resource(name="fileMngService")
   	private FileMngService fileMngService;
   	
	@Resource(name = "FileManageDAO")
	private FileManageDAO fileMngDAO;
	
   	@Resource(name = "egovFileIdGnrService")
	private EgovIdGnrService idgenService;

   	@Resource(name = "cmm6600DAO")
   	private Cmm6600DAO cmm6600DAO;
   	
	
    @Resource(name="arm1100DAO")
    private Arm1100DAO arm1100DAO;
   	
	@SuppressWarnings("rawtypes")
	@Override
	public List<Map> selectCmm6600SignUsrList(Map<String, String> paramMap) throws Exception {
		return cmm6600DAO.selectCmm6600SignUsrList(paramMap);
	}

	
	@Override
	public void saveCmm6600SignLine(Map<String, String> paramMap) throws Exception {
		
		String singUsrInfList = (String) paramMap.get("signUsrInfList");
		
		
		if("update".equals(paramMap.get("type"))) {
			cmm6600DAO.deleteCmm6600SignLine(paramMap);
		}
		
		Map<String, Object> ntfParam = new HashMap<String, Object>();
		String targetNm = paramMap.get("targetNm");
		
		
		ntfParam.put("licGrpId", paramMap.get("licGrpId"));
		ntfParam.put("sendUsrId", paramMap.get("regUsrId")); 
		ntfParam.put("armSendTypeCd", "01"); 
		
		
		if(singUsrInfList != null && !"[]".equals(singUsrInfList)) {
			
			JSONArray jsonArray = new JSONArray(singUsrInfList);
			
			
			for(int i=0;i<jsonArray.length();i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.get(i);
				
				String ord = jsonObj.getString("ord");
				String usrId = jsonObj.getString("usrId");
				
				paramMap.put("signUsrId", usrId);
				paramMap.put("ord", ord);
				
				
				cmm6600DAO.insertCmm6600SignLine(paramMap);
				
				
				if("0".equals(ord)) {
					paramMap.put("signTypeCd", "01");
					cmm6600DAO.insertCmm6601SignInfo(paramMap);
					
				
				}else {
					ntfParam.put("armTypeCd", "04"); 
					ntfParam.put("usrId", jsonObj.getString("usrId")); 
					
					
					if("01".equals(paramMap.get("targetCd"))) {
						ntfParam.put("armTitle", "[요구사항] 결재 담당자 지정"); 
						ntfParam.put("armContent", "["+targetNm+"] 요구 사항에 "+ord+"번째 결재자로 지정되었습니다."); 
					
					}else if("02".equals(paramMap.get("targetCd"))) {
						ntfParam.put("armTitle", "[배포 계획] 결재 담당자 지정"); 
						ntfParam.put("armContent", "["+targetNm+"] 배포 계획에 "+ord+"번째 결재자로 지정되었습니다."); 
					}
					
					arm1100DAO.insertArm1100NtfInfo(ntfParam);
					
					
					if("1".equals(ord)) {
						
						if("01".equals(paramMap.get("targetCd"))) {
							ntfParam.put("armTitle", "[요구사항] 결재 담당자 지정"); 
							ntfParam.put("armContent", "["+targetNm+"] 요구 사항에 "+ord+"번째 결재자로 지정되었습니다."); 
						
						}else if("02".equals(paramMap.get("targetCd"))) {
							ntfParam.put("armTitle", "[배포 계획] 결재 요청"); 
							ntfParam.put("armContent", "["+targetNm+"] 배포 계획이 결재 대기 중입니다."); 
						}
						
						arm1100DAO.insertArm1100NtfInfo(ntfParam);
					}
				}
			}
		}
		
	}

	
	@Override
	public int selectCmm6600SignListCnt(Map<String, String> paramMap) throws Exception {
		return cmm6600DAO.selectCmm6600SignListCnt(paramMap);
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public List<Map> selectCmm6600SignList(Map<String, String> paramMap) throws Exception {
		return cmm6600DAO.selectCmm6600SignList(paramMap);
	}

	
	@Override
	public int selectCmm6600UsrSignListCnt(Map<String, String> paramMap) throws Exception {
		return cmm6600DAO.selectCmm6600UsrSignListCnt(paramMap);
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Map> selectCmm6600UsrSignList(Map<String, String> paramMap) throws Exception {
		return cmm6600DAO.selectCmm6600UsrSignList(paramMap);
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public void insertCmm6601SignInfo(Map<String, String> paramMap) throws Exception {
		String rowDatas = paramMap.get("rowDatas");
		
		if(rowDatas != null && !"[]".equals(rowDatas)) {
			
			
			Map<String, Object> ntfParamDft = new HashMap<String, Object>();
			
			
			ntfParamDft.put("licGrpId", paramMap.get("licGrpId"));
			ntfParamDft.put("sendUsrId", paramMap.get("regUsrId")); 
			ntfParamDft.put("armSendTypeCd", "01"); 
			
			
			JSONArray jsonArray = new JSONArray(rowDatas);
			
			
			for(int i=0;i<jsonArray.length();i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.get(i);
				
				
				int ord = Integer.parseInt(jsonObj.getString("ord")) +1;
				
				String targetNm = "";
				
				paramMap.put("ord", String.valueOf(ord));
				paramMap.put("targetId", jsonObj.getString("dplId"));
				
				
				ntfParamDft.put("armTypeCd", "04"); 
				ntfParamDft.put("usrId", jsonObj.getString("signDrfUsrId")); 
				
				
				if("signRjt".equals(paramMap.get("type"))) {
					paramMap.put("signTypeCd", "04");
					ntfParamDft.put("armSendTypeCd", "05"); 
					
					if("01".equals(jsonObj.get("targetCd"))) {
						targetNm = jsonObj.getString("dplNm");
						
						
						ntfParamDft.put("armTitle", "[배포 계획] 결재 반려"); 
						ntfParamDft.put("armContent", "["+targetNm+"] 배포 계획이 결재 반려 되었습니다."); 
					}
					
					else if("02".equals(jsonObj.get("targetCd"))) {
						targetNm = jsonObj.getString("dplNm");
						
						
						ntfParamDft.put("armTitle", "[배포 계획] 결재 반려"); 
						ntfParamDft.put("armContent", "["+targetNm+"] 배포 계획이 결재 반려 되었습니다."); 
					}
					
				
				}else if("signApr".equals(paramMap.get("type"))) {
					
					
					int maxOrd = cmm6600DAO.selectCmm6600MaxOrd(paramMap);
					
					
					if(maxOrd == ord) {
						paramMap.put("signTypeCd", "03");
						ntfParamDft.put("armSendTypeCd", "04"); 
						
						
						if("01".equals(jsonObj.get("targetCd"))) {
							targetNm = jsonObj.getString("dplNm");
							
							
							ntfParamDft.put("armTitle", "[배포 계획] 최종 결재 승인"); 
							ntfParamDft.put("armContent", "["+targetNm+"] 배포 계획이 최종 결재 승인 되었습니다."); 
						}
						
						else if("02".equals(jsonObj.get("targetCd"))) {
							targetNm = jsonObj.getString("dplNm");
							
							
							ntfParamDft.put("armTitle", "[배포 계획] 최종 결재 승인"); 
							ntfParamDft.put("armContent", "["+targetNm+"] 배포 계획이 최종 결재 승인 되었습니다"); 
						}
						
					
					}else {
						
						paramMap.put("ord", String.valueOf(ord+1));
						paramMap.put("signTypeCd", "02");
						
						
						Map nextSignUsrInfo = cmm6600DAO.selectCmm6600NextOrdInfo(paramMap);
						
						
						Map<String, Object> ntfParamApr = new HashMap<String, Object>();
						
						
						ntfParamApr.put("licGrpId", paramMap.get("licGrpId"));
						ntfParamApr.put("sendUsrId", jsonObj.getString("signDrfUsrId")); 
						ntfParamApr.put("armSendTypeCd", "01"); 
						
						ntfParamApr.put("armTypeCd", "04"); 
						ntfParamApr.put("usrId", nextSignUsrInfo.get("signUsrId")); 
						
						
						if("01".equals(jsonObj.get("targetCd"))) {
							targetNm = jsonObj.getString("dplNm");
							
							
							ntfParamDft.put("armTitle", "[배포 계획] 결재 승인"); 
							ntfParamDft.put("armContent", "["+targetNm+"] 배포 계획이 결재 승인 되었습니다."); 
							
							
							ntfParamApr.put("armTitle", "[배포 계획] 결재 승인"); 
							ntfParamApr.put("armContent", "["+targetNm+"] 배포 계획이 결재 승인 되었습니다."); 
						}
						
						else if("02".equals(jsonObj.get("targetCd"))) {
							targetNm = jsonObj.getString("dplNm");
							
							
							ntfParamDft.put("armTitle", "[배포 계획] 결재 승인"); 
							ntfParamDft.put("armContent", "["+targetNm+"] 배포 계획이 결재 승인 되었습니다 <br> 다음 결재자 : "+nextSignUsrInfo.get("signUsrId")); 
							
							
							ntfParamApr.put("armTitle", "[배포 계획] 결재 요청"); 
							ntfParamApr.put("armContent", "["+targetNm+"] 배포 계획이 결재 대기 중입니다."); 
						}
						
						
						arm1100DAO.insertArm1100NtfInfo(ntfParamApr);
					}
				}
				
				
				cmm6600DAO.insertCmm6601SignInfo(paramMap);
				
			}
			
			
			arm1100DAO.insertArm1100NtfInfo(ntfParamDft);
			
		}
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public Map selectCmm6601SignInfo(Map<String, String> paramMap) throws Exception {
		return cmm6600DAO.selectCmm6601SignInfo(paramMap);
	}

}








