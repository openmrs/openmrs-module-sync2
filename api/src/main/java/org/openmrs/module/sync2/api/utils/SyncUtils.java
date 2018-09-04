package org.openmrs.module.sync2.api.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.db.EventAction;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.fhir.api.util.FHIREncounterUtil;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.enums.AtomfeedTagContent;
import org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance;
import org.openmrs.module.sync2.client.RestResourceCreationUtil;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_ENCOUNTER;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_OB;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_PATIENT;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_VISIT;
import static org.openmrs.module.sync2.SyncConstants.FHIR_CLIENT;
import static org.openmrs.module.sync2.SyncConstants.RESOURCE_PREFERRED_CLIENT;
import static org.openmrs.module.sync2.SyncConstants.REST_CLIENT;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;

public class SyncUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(SyncUtils.class);

	private static final String ATOMFEED_TAG_VALUE_FIELD_NAME = "Category.term";

	public static Map<String, String> getLinks(String json) {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};

		try {
			HashMap<String, String> result = mapper.readValue(json, typeRef);

			return result;
		} catch (IOException e) {
			throw new SyncException(String.format("Invalid resource links JSON Object: %s  ", json), e);
		}
	}

	public static String getParentBaseUrl() {
		SyncConfigurationService cs = getSyncConfigurationService();
		return cs.getSyncConfiguration().getGeneral().getParentFeedLocation();
	}

	public static String getLocalBaseUrl() {
		SyncConfigurationService cs = getSyncConfigurationService();
		return cs.getSyncConfiguration().getGeneral().getLocalFeedLocation();
	}

	public static String getLocalInstanceId() {
		SyncConfigurationService cs = getSyncConfigurationService();
		return cs.getSyncConfiguration().getGeneral().getLocalInstanceId();
	}

	public static String getPullUrl(Map<String, String> resourceLinks, String clientName, OpenMRSSyncInstance instance) {
		String base = instance.equals(CHILD) ? getLocalBaseUrl() : getParentBaseUrl();
		return getFullUrl(base, resourceLinks.get(clientName));
	}

	public static String getPushUrl(Map<String, String> resourceLinks, String clientName, OpenMRSSyncInstance instance) {
		String base = instance.equals(CHILD) ? getLocalBaseUrl() : getParentBaseUrl();
		return getFullUrl(base, getPushPath(resourceLinks.get(clientName)));

	}

	public static String getValueOfAtomfeedEventTag(List tags, AtomfeedTagContent atomfeedTagContent) {
		for (Object tag : tags) {
			String tagValue = getTagValue(tag);
			boolean isTagEventAction = checkIfParamIsEventAction(tagValue);
			if (atomfeedTagContent == AtomfeedTagContent.EVENT_ACTION && isTagEventAction) {
				return tagValue;
			}
			if (atomfeedTagContent == AtomfeedTagContent.CATEGORY && !isTagEventAction) {
				return tagValue;
			}
		}
		throw new SyncException(String.format("'%s' enum not found in tag list", atomfeedTagContent.name()));
	}

	private static String getFullUrl(String baseAddress, String path) {
		return baseAddress + path;
	}

	private static String getPushPath(String pathWithId) {
		return pathWithId.substring(0, pathWithId.lastIndexOf("/"));
	}

	private static String getTagValue(Object tag) {
		final HashMap<String, String> map = new HashMap<>();

		for (String pair : tag.toString().split("\n")) {
			String[] keyValue = pair.split("=");
			map.put(keyValue[0], keyValue[1]);
		}
		return map.get(ATOMFEED_TAG_VALUE_FIELD_NAME);
	}

	public static String selectAppropriateClientName(Map<String, String> availableResourceLinks) {
		String preferredClient = getPreferredClient();
		if (availableResourceLinks.containsKey(preferredClient)) {
			return preferredClient;
		} else {
			return getFirstKey(availableResourceLinks);
		}
	}

	private static String getFirstKey(Map<String, String> map) {
		if (map.size() > 0) {
			return map.keySet().iterator().next();
		} else {
			throw new SyncException("A map doesn't contain any entries, so it is impossible to get the first key");
		}
	}

	public static String getPreferredClient() {
		return Context.getAdministrationService().getGlobalProperty(RESOURCE_PREFERRED_CLIENT);
	}

	public static String extractUUIDFromResourceLinks(Map<String, String> resourceLinks) {
		for (String client : resourceLinks.keySet()) {
			switch (client) {
				case REST_CLIENT:
					return extractUUIDFromRestResource(resourceLinks.get(client));
				case FHIR_CLIENT:
					return extractUUIDFromFHIRResource(resourceLinks.get(client));
				default:
			}
		}

		LOGGER.error("Couldn't find any supported client to extract uuid from.");
		return null;
	}

	public static void readFeedByCategory(String category, AtomFeedClient atomFeedClient,
			SyncConfigurationService configurationService, String ws) {
		try {
			URI uri = new URI(getResourceUrlWithCategory(category, configurationService, ws));
			atomFeedClient.setUri(uri);
			atomFeedClient.process();
		} catch (URISyntaxException e) {
			throw new SyncException("Atomfeed URI is not correct. ", e);
		} catch (Exception e) {
			throw new SyncException(String.format("Error during processing atomfeeds for category %s: ", category), e);
		}
	}

	public static boolean compareLocalAndPulled(String clientName, String category, Object from, Object dest) {
		boolean result = false;
		if (null != dest && null != from) {
			//If 'from' is  instance of String it represent uuid and should be used to delete object action.
			if (!(from instanceof String)) {
				switch (clientName) {
					case REST_CLIENT:

						//TODO: Work around for deleting patient through REST API. Should be refactored.
						if (from instanceof org.openmrs.Patient && dest instanceof org.openmrs.Patient) {
							boolean fromIsVoided = ((org.openmrs.Patient) from).getVoided();
							boolean destIsVoided = ((org.openmrs.Patient) dest).getVoided();

							if (fromIsVoided && destIsVoided) {
								return true;
							}
						}

						RestResource obj1 = RestResourceCreationUtil.createRestResourceFromOpenMRSData((OpenmrsObject) dest);
						RestResource obj2 = RestResourceCreationUtil.createRestResourceFromOpenMRSData((OpenmrsObject) from);
						result = obj1.equals(obj2);
						break;
					case FHIR_CLIENT:
						switch (category) {
							case CATEGORY_PATIENT:
								result = FHIRPatientUtil.compareCurrentPatients(dest, from);
								break;
							case CATEGORY_ENCOUNTER:
								result = FHIREncounterUtil.compareCurrentEncounters(dest, from);
								break;
							case CATEGORY_VISIT:
								result = FHIREncounterUtil.compareCurrentEncounters(dest, from);
								break;
							case CATEGORY_OB:
								result = FHIRObsUtil.compareCurrentObs(dest, from);
								break;
							default:
								result = dest.equals(from);
						}
						break;
					default:
						result = dest.equals(from);
				}
			}
		}

		return result;
	}

	private static String extractUUIDFromRestResource(String link) {
		String[] tokens = link.split("/");
		// todo throw custom sync2 exception if tokens.length != 6
		return tokens[5].split("\\?")[0];
	}

	private static String extractUUIDFromFHIRResource(String link) {
		String[] tokens = link.split("/");
		// todo throw custom sync2 exception if tokens.length != 5
		return tokens[4];
	}

	public static <T> String prettySerialize(Map<T, T> map) {
		try {
			return new ObjectMapper()
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(map);
		} catch (IOException ex) {
			throw new SyncException("Cannot serialize map", ex);
		}
	}

	public static <T> String serialize(T object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		} catch (IOException ex) {
			throw new SyncException("Cannot serialize", ex);
		}
	}

	public static <T> T deserialize(String object, Class<T> clazz) {
		try {
			return new ObjectMapper().readValue(object, clazz);
		} catch (IOException ex) {
			throw new SyncException("Cannot deserialize", ex);
		}
	}

	public static Map<String, String> deserializeJsonToStringsMap(String json) {
		try {
			return new ObjectMapper().readValue(json, new TypeReference<Map<String, String>>() {});
		} catch (IOException ex) {
			throw new SyncException("Cannot deserialize map", ex);
		}
	}

	public static boolean checkIfParamIsEventAction(String actionName) {
		try {
			EventAction.valueOf(actionName);
		} catch (IllegalArgumentException ex) {
			return false;
		}
		return true;
	}

	private static String getParentUri(SyncConfigurationService configurationService) {
		return configurationService.getSyncConfiguration().getGeneral().getParentFeedLocation();
	}

	private static String getResourceUrlWithCategory(String category, SyncConfigurationService cs, String ws) {
		return getParentUri(cs) + ws + category + "/" + SyncConstants.RECENT_FEED;
	}

	public static SyncConfigurationService getSyncConfigurationService() {
		return Context.getService(SyncConfigurationService.class);
	}
}
