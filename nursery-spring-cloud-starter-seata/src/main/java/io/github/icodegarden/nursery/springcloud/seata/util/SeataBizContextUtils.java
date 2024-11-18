package io.github.icodegarden.nursery.springcloud.seata.util;

import java.util.List;
import java.util.Map;

import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.apache.seata.rm.tcc.api.BusinessActionContextUtil;

import io.github.icodegarden.nutrient.lang.util.JsonUtils;

/**
 * @author Fangfang.Xu
 */
public class SeataBizContextUtils {

	public static void setValue(String key, Object value) {
		BusinessActionContext context = BusinessActionContextUtil.getContext();
		Map<String, Object> actionContext = context.getActionContext();
		actionContext.put(key, value);
		context.setUpdated(true);
	}

	public static Object getValue(String key) {
		BusinessActionContext context = BusinessActionContextUtil.getContext();
		Map<String, Object> actionContext = context.getActionContext();
		return actionContext.get(key);
	}

	public static <T> T getValueJsonfy(String key, Class<T> cla) {
		BusinessActionContext context = BusinessActionContextUtil.getContext();
		Map<String, Object> actionContext = context.getActionContext();

		Map map = (Map) actionContext.get(key);
		String json = JsonUtils.serialize(map);
		return JsonUtils.deserialize(json, cla);
	}

	public static <T> List<T> getValueJsonfyArray(String key, Class<T> cla) {
		BusinessActionContext context = BusinessActionContextUtil.getContext();
		Map<String, Object> actionContext = context.getActionContext();

		Object arr = actionContext.get(key);
		String jsonArray = JsonUtils.serialize(arr);
		return JsonUtils.deserializeArray(jsonArray, cla);
	}
	
	public static BusinessActionContext getBizContext() {
		return BusinessActionContextUtil.getContext();
	}
}
