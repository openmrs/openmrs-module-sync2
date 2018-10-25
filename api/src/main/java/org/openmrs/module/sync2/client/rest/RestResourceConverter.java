package org.openmrs.module.sync2.client.rest;

import java.util.AbstractMap;

public interface RestResourceConverter {

	void convertObject(String url, Object object);

	boolean deepCompareSimpleObject(AbstractMap<String, Object> from, AbstractMap<String, Object> dest);

}
