package com.confianza.webapp.aop.framework.frmauditoria;

 /**                          
  *                           
  * @modifico	CONFIANZA
  * @version	1.0 
  * @Fecha		30/10/2014 
  * @since		1.0            
  * @app		framework  
  */                          

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.confianza.webapp.repository.framework.frmauditoria.FrmAuditoria;
import com.confianza.webapp.repository.framework.frmconsulta.FrmConsultaRepository;
import com.confianza.webapp.repository.framework.frmlog.FrmLog;
import com.confianza.webapp.repository.framework.frmlogext.FrmLogextRepository;
import com.confianza.webapp.repository.framework.frmsesion.FrmSesion;
import com.confianza.webapp.repository.framework.frmtransaccion.FrmTransaccion;
import com.confianza.webapp.service.framework.frmauditoria.FrmAuditoriaService;
import com.confianza.webapp.service.framework.frmlog.FrmLogService;
import com.confianza.webapp.service.framework.frmtransaccion.FrmTransaccionService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
@Aspect
public class FrmAuditoriaAOPImpl{
	
	@Autowired
	private Gson gson;
		
	@Autowired
	private HttpSession session;
	
	@Autowired
	private FrmLogService frmLogService;
	
	@Autowired
	private FrmAuditoriaService frmAuditoriaService;	
	
	@Autowired
	private FrmTransaccionService frmTransaccionService;
	
	@Pointcut("execution(* com.confianza.webapp.service.framework.frmconsulta.FrmConsultaServiceImpl.updateRecord(..))")
	public void pointUpdateRecord(){		
	}
		
	@AfterReturning(value="pointUpdateRecord()", returning="result")
	public void interceptUpdateRecord(JoinPoint point, String result) throws Throwable{
		Type type = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> resultData=gson.fromJson(result, type);
		
		if(resultData.get("AUDITORIA")!=null){
			String []auditoria=resultData.get("AUDITORIA").toString().split(";");
			String campos[];
			//recupero la sesion del usuario
			FrmSesion frmSesion = (FrmSesion) session.getAttribute("frmSesion");
			//creo la transaccion de este proceso
			FrmTransaccion frmtransaccion=frmTransaccionService.insert(frmSesion.getSesicons());
			FrmAuditoria frmAuditoria;
			FrmLog frmLog;
			
			//creo la auditoria por cada campo actualizado
			for(String aux:auditoria){
				campos=aux.split(",");
				
				if(campos[0].equals("DELETE") || campos[0].equals("INSERT")){
					frmLog=new FrmLog();
					frmLog.setSlogtran(frmtransaccion.getTrancons());
					frmLog.setSlogtabl(campos[1]);
					frmLog.setSlogacci(campos[0]);
					frmLog.setSlogregi(campos[2]);
					frmLogService.insert(frmLog);
				}
				else if(campos[0].equals("UPDATE") ){
					frmAuditoria=new FrmAuditoria();
					frmAuditoria.setAuditran(frmtransaccion.getTrancons());
					frmAuditoria.setAuditabl(campos[1]);
					frmAuditoria.setAudicopk(campos[2]);
					frmAuditoria.setAudicamp(campos[3]);
					frmAuditoria.setAudivaan(campos[4]);
					frmAuditoria.setAudivanu(campos[5]);
					frmAuditoriaService.insert(frmAuditoria);
				}				
			}
			
		}
	}
	
	public static HttpSession getSession() {
	    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	    return attr.getRequest().getSession(); // true == allow create
	}
}