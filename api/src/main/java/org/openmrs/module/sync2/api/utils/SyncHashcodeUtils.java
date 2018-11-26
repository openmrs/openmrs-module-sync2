package org.openmrs.module.sync2.api.utils;

import org.apache.commons.codec.digest.DigestUtils;

import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SyncHashcodeUtils {
	private static final SimpleObjectMessageConverter converter = new SimpleObjectMessageConverter();

	private static final String REGEX = "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})";

	private static final String REPLACEMENT = "$1-$2-$3-$4-$5";

	private static final List<String> STOP_WORDS = Arrays.asList("links", "auditInfo");

	public static String getHashcode(SimpleObject simpleObject) {
		if (simpleObject == null) {
			return null;
		}
		return createUuidFromString(converter.convertToJson(removeFields(simpleObject))).toString();
	}

	private static UUID createUuidFromString(String data) {
		String md5Hex = DigestUtils.md5Hex(data);
		md5Hex = md5Hex.replaceFirst(REGEX, REPLACEMENT);
		return UUID.fromString(md5Hex);
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
