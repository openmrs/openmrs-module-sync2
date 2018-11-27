package org.openmrs.module.sync2.api.utils;

import org.apache.commons.lang3.SerializationException;
import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.io.IOException;

public class SimpleObjectSerializationUtils {

	/**
	 * Serializes SimpleObject to the JSON representation
	 *
	 * @param simpleObject
	 * @return JSON representation
	 */
	public static String serialize(SimpleObject simpleObject) {
		SimpleObjectMessageConverter converter = new SimpleObjectMessageConverter();
		return converter.convertToJson(simpleObject);
	}

	/**
	 * Deserialize JSON to the SimpleObject
	 *
	 * @param simpleObject
	 * @return SimpleObject representation
	 */
	public static SimpleObject deserialize(String simpleObject) {
		try {
			return SimpleObject.parseJson(simpleObject);
		}
		catch (IOException e) {
			throw new SerializationException("IOException while deserialize object data", e);
		}
	}

	/**
	 * Clones the SimpleObject
	 *
	 * @param simpleObject
	 * @return cloned SimpleObject
	 */
	public static SimpleObject clone(SimpleObject simpleObject) {
		String stringRepresentation = serialize(simpleObject);
		return deserialize(stringRepresentation);
	}

	private SimpleObjectSerializationUtils() { }
}
