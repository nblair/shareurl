package edu.wisc.wisccal.shareurl.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class ShareObjectMapper extends ObjectMapper{

	public ShareObjectMapper() {
		super.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
	}

}
