package seung.app.conf.errors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import seung.commons.SCommonV;

public class SControllerAdviceHigh {

	private static final Logger logger = LoggerFactory.getLogger(SControllerAdviceHigh.class);
	
	public String handleAnyException(
			HttpServletRequest httpServletRequest
			, HttpServletResponse httpServletResponse
			, Exception exception
			) {
		
		String message = exception.getMessage();
		String requestType = "XMLHttpRequest".equals(httpServletRequest.getHeader("X-Requested-With")) ? SCommonV._S_DATA_JSON : SCommonV._S_DATA_HTML;
		
		logger.debug(String.format("[HIGH] [%s] %s", requestType, message));
		
		httpServletRequest.setAttribute("forward_code", 500);
		httpServletRequest.setAttribute("forward_type", requestType);
		httpServletRequest.setAttribute("forward_message", message);
		
		return "forward:/err/" + requestType;
	}
}
