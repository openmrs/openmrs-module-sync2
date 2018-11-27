package org.openmrs.module.sync2.api.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SyncHashcodeUtils {
	private static final SimpleObjectMessageConverter converter = new SimpleObjectMessageConverter();

	private static final List<String> STOP_WORDS = Arrays.asList("links", "auditInfo");

	public static String getHashcode(SimpleObject simpleObject) {
		if (simpleObject == null) {
			return null;
		}
		return createUuidFromString(converter.convertToJson(removeFields(simpleObject)));
	}

	private static String createUuidFromString(String data) {
		return DigestUtils.sha512Hex(data);
	}

	private static SimpleObject removeFields(SimpleObject simpleObject) {
		SimpleObject result = SimpleObjectSerializationUtils.clone(simpleObject);
		removeVoided(result);
		removeStopWords(result);
		return result;
	}

	private static void removeStopWords(AbstractMap<String, Object> result) {
		for (String word : STOP_WORDS) {
			result.remove(word);
		}

		for (Map.Entry<String, Object> entry : result.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Map<?,?>) {
				removeStopWords((AbstractMap) value);
			}
			if (value instanceof List<?>) {
				for (Object element : (List) value) {
					if (element instanceof Map<?,?>) {
						removeStopWords((AbstractMap) element);
					}
				}
			}
		}
	}

	private static void removeVoided(final SimpleObject result) {
		result.remove("dateVoided");
		result.remove("voided");
		result.remove("voidedBy");
		result.remove("voidReason");

		result.remove("dateRetired");
		result.remove("retired");
		result.remove("retiredBy");
		result.remove("retireReason");
	}
}
