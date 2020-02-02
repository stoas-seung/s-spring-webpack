package seung.app.conf.views;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

public class SMappingJackson2JsonView extends MappingJackson2JsonView {

	private static final Logger logger = LoggerFactory.getLogger(SMappingJackson2JsonView.class);
	
	private ArrayList<String> notWrappedModelKeys = new ArrayList<String>();
	
	public void addNotWrappedModelKeys(String key) {
		if(!notWrappedModelKeys.contains(key)) {
			notWrappedModelKeys.add(key);
		}
	}
	
	public void removeNotWrappedModelKeys(String key) {
		notWrappedModelKeys.remove(key);
	}
	
	@Override
	protected Object filterModel(Map<String, Object> model) {
		logger.debug("{}.{}", "SMappingJackson2JsonView", "filterModel");
		for(String key : notWrappedModelKeys) {
			if(model.containsKey(key)) {
				return model.get(key);
			}
		}
		return super.filterModel(model);
	}
	
}
