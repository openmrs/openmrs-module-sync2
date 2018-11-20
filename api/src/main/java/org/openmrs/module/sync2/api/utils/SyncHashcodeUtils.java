package org.openmrs.module.sync2.api.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.SerializationUtils;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.module.sync2.api.model.enums.OpenMRSIdEnum;
import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.UUID;

public class SyncHashcodeUtils {
	private static final SimpleObjectMessageConverter converter = new SimpleObjectMessageConverter();

	private static final String REGEX = "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})";

	private static final String REPLACEMENT = "$1-$2-$3-$4-$5";

	public static UUID getHashcode(SimpleObject simpleObject, Class<?> clazz) {
		return createUuidFromString(converter.convertToJson(removeFields(simpleObject, clazz)));
	}

	private static UUID createUuidFromString(String data) {
		String md5Hex = DigestUtils.md5Hex(data);
		md5Hex = md5Hex.replaceFirst(REGEX, REPLACEMENT);
		return UUID.fromString(md5Hex);
	}

	private static SimpleObject removeFields(SimpleObject simpleObject, Class<?> clazz) {
		SimpleObject result = SerializationUtils.clone(simpleObject);
		removeVoided(result, clazz);
		removeId(result, clazz);
		return result;
	}

	private static void removeVoided(final SimpleObject result, Class<?> clazz) {
		if (BaseOpenmrsData.class.isAssignableFrom(clazz)) {
			result.remove("dateVoided");
			result.remove("voided");
			result.remove("voidedBy");
			result.remove("voidReason");
		} else if (BaseOpenmrsMetadata.class.isAssignableFrom(clazz)) {
			result.remove("dateRetired");
			result.remove("retired");
			result.remove("retiredBy");
			result.remove("retireReason");
		}
	}

	private static void removeId(final SimpleObject result, Class<?> clazz) {
		OpenMRSIdEnum nameEnum = OpenMRSIdEnum.getByClass(clazz);
		if (nameEnum != null && nameEnum.getName() != null) {
			result.remove(nameEnum.getName());
		}
	}
}
