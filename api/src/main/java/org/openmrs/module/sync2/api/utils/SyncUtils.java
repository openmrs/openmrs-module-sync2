package org.openmrs.module.sync2.api.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.sun.syndication.feed.atom.Category;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.db.EventAction;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.openmrs.module.atomfeed.api.service.XMLParseService;
import org.openmrs.module.atomfeed.api.service.impl.XMLParseServiceImpl;
import org.openmrs.module.fhir.api.helper.ClientHelper;
import org.openmrs.module.fhir.api.merge.MergeBehaviour;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.ClientConfiguration;
import org.openmrs.module.sync2.api.model.enums.AtomfeedTagContent;
import org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance;
import org.openmrs.module.sync2.api.model.enums.SyncOperation;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.client.ClientHelperFactory;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.FHIR_CLIENT;
import static org.openmrs.module.sync2.SyncConstants.RESOURCE_PREFERRED_CLIENT;
import static org.openmrs.module.sync2.SyncConstants.REST_CLIENT;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;

public class SyncUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(SyncUtils.class);

	private static final String ATOMFEED_TAG_VALUE_FIELD_NAME = "Category.term";

	public static Map<String, String> getLinks(String json) {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() { };

		try {
			HashMap<String, String> result = mapper.readValue(json, typeRef);
			return result;
		} catch (IOException e) {
			throw new SyncException(String.format("Invalid resource links JSON Object: %s  ", json), e);
		}
	}

	public static String getParentBaseUrl(String clientName) {
		String parentBaseUrl = null;
		parentBaseUrl = getSpecificClientAddress(clientName);
		if (StringUtils.isBlank(parentBaseUrl)) {
			SyncConfigurationService cs = getSyncConfigurationService();
			parentBaseUrl = cs.getSyncConfiguration().getGeneral().getParentFeedLocation();
		}
		return parentBaseUrl;
	}

	public static String getSpecificClientAddress(String clientName) {
		String address = null;
		ClientConfiguration configuration = SyncConfigurationUtils.getClientConfiguration(clientName);
		if (configuration != null) {
			address = configuration.getHostAddress();
		}
		return address;
	}

	public static boolean clientHasSpecificAddress(String clientName, OpenMRSSyncInstance instance) {
		if (instance == OpenMRSSyncInstance.CHILD) {
			return false;
		}
		return StringUtils.isNotBlank(getSpecificClientAddress(clientName));
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
		String base = instance.equals(CHILD) ? getLocalBaseUrl() : getParentBaseUrl(clientName);
		return getFullUrl(base, resourceLinks.get(clientName));
	}

	public static String getPushUrl(Map<String, String> resourceLinks, String clientName, OpenMRSSyncInstance instance) {
		String base = instance.equals(CHILD) ? getLocalBaseUrl() : getParentBaseUrl(clientName);
		return getFullUrl(base, getPushPath(resourceLinks.get(clientName)));
	}

	public static String getClientSpecificLogin(String clientName) {
		String login = null;
		ClientConfiguration configuration = SyncConfigurationUtils.getClientConfiguration(clientName);
		if (configuration != null) {
			login = configuration.getLogin();
		}
		return login;
	}

	public static String getClientSpecificPassword(String clientName) {
		String password = null;
		ClientConfiguration configuration = SyncConfigurationUtils.getClientConfiguration(clientName);
		if (configuration != null) {
			password = configuration.getPassword();
		}
		return password;
	}

	public static boolean clientHasSpecificCridentials(String clientName, OpenMRSSyncInstance instance) {
		if (instance == OpenMRSSyncInstance.CHILD) {
			return false;
		}
		return StringUtils.isNotBlank(getClientSpecificPassword(clientName))
				&& StringUtils.isNotBlank(getClientSpecificLogin(clientName));
	}

	public static String getClientLogin(String clientName, OpenMRSSyncInstance instance) {
		String login = null;
		if (clientHasSpecificCridentials(clientName, instance)) {
			login = getClientSpecificLogin(clientName);
		}
		if (StringUtils.isBlank(login)) {
			AdministrationService adminService = Context.getAdministrationService();
			login = instance.equals(OpenMRSSyncInstance.PARENT) ?
					adminService.getGlobalProperty(SyncConstants.PARENT_USERNAME_PROPERTY) :
					adminService.getGlobalProperty(SyncConstants.LOCAL_USERNAME_PROPERTY);
		}
		return login;
	}

	public static String getClientPassword(String clientName, OpenMRSSyncInstance instance) {
		String password = null;
		if (clientHasSpecificCridentials(clientName, instance)) {
			password = getClientSpecificPassword(clientName);
		}
		if (StringUtils.isBlank(password)) {
			AdministrationService adminService = Context.getAdministrationService();
			password = instance.equals(OpenMRSSyncInstance.PARENT) ?
					adminService.getGlobalProperty(SyncConstants.PARENT_PASSWORD_PROPERTY) :
					adminService.getGlobalProperty(SyncConstants.LOCAL_PASSWORD_PROPERTY);
		}
		return password;
	}

	public static String getValueOfAtomfeedEventTag(List tags, AtomfeedTagContent atomfeedTagContent) {
		for (Object tag : tags) {
			if (checkIfParamIsFeedFilter(tag)) {
				continue;
			}
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

	public static String getFullUrl(String baseAddress, String path) {
		return baseAddress + path;
	}

	private static String getPushPath(String pathWithId) {
		String result = null;
		if (pathWithId.contains("/")) {
			result = pathWithId.substring(SyncConstants.ZERO, pathWithId.lastIndexOf("/"));
		} else {
			result = pathWithId;
		}
		return result;
	}

	private static String getTagValue(Object tag) {
		final HashMap<String, String> map = new HashMap<>();

		for (String pair : tag.toString().split("\n")) {
			String[] keyValue = pair.split("=");
			map.put(keyValue[0], keyValue[1]);
		}
		return map.get(ATOMFEED_TAG_VALUE_FIELD_NAME);
	}

	public static String selectAppropriateClientName(Map<String, String> availableResourceLinks, String category,
			SyncOperation operation) {
		ClassConfiguration classConfiguration = getSyncConfigurationService().getClassConfiguration(category, operation);
		String preferredClient = getClassPreferredClient(classConfiguration);
		if (StringUtils.isBlank(preferredClient)) {
			preferredClient = getGlobalPreferredClient();
		}
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

	public static String getClassPreferredClient(ClassConfiguration classConfiguration) {
		String preferredClient = null;
		if (classConfiguration != null) {
			preferredClient = classConfiguration.getPreferredClient();
		}
		return preferredClient;
	}

	public static String getGlobalPreferredClient() {
		return Context.getAdministrationService().getGlobalProperty(RESOURCE_PREFERRED_CLIENT,
				SyncConstants.DEFAULT_SYNC_2_CLIENT);
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

	public static boolean compareLocalAndPulled(String clientName, String category, Object from, Object dest) {
		boolean result = false;
		if (null != dest && null != from) {
			//If 'from' is  instance of String it represent uuid and should be used to delete object action.
			if (!(from instanceof String)) {
				ClientHelper clientHelper = ClientHelperFactory.createClient(clientName);

				if (clientHelper != null) {
					result = clientHelper.compareResourceObjects(category, from, dest);
				} else {
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

	public static boolean checkIfParamIsFeedFilter(Object tag) {
		if (tag instanceof Category) {
			XMLParseService xmlParseService = new XMLParseServiceImpl();
			try {
				String feedFilterXML = ((Category) tag).getTerm();
				FeedFilter feedFilter = xmlParseService.createFeedFilterFromXMLString(feedFilterXML);
				return feedFilter.getFilter() != null && feedFilter.getBeanName() != null;
			}
			catch (JAXBException e) {
				return false;
			}
		}
		return false;
	}

	public static boolean checkIfParamIsEventAction(String actionName) {
		try {
			EventAction.valueOf(actionName);
		} catch (IllegalArgumentException ex) {
			return false;
		}
		return true;
	}

	public static SyncConfigurationService getSyncConfigurationService() {
		return Context.getService(SyncConfigurationService.class);
	}

	/**
	 * This method configures Gson.
	 * We need to use workaround for null dates.
	 * @return definitive null safe Gson instance
	 */
	public static Gson createDefaultGson() {
		// Trick to get the DefaultDateTypeAdatpter instance
		// Create a first Gson instance
		Gson gson = new GsonBuilder()
				.setDateFormat(ConversionUtil.DATE_FORMAT)
				.create();

		// Get the date adapter
		TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);

		// Ensure the DateTypeAdapter is null safe
		TypeAdapter<Date> safeDateTypeAdapter = dateTypeAdapter.nullSafe();

		// Build the definitive safe Gson instance
		return new GsonBuilder()
				.setDateFormat(ConversionUtil.DATE_FORMAT)
				.registerTypeAdapter(Date.class, safeDateTypeAdapter)
				.create();
	}

	public static MergeBehaviour getMergeBehaviour() {
		String behavior = Context.getAdministrationService().getGlobalProperty(SyncConstants.SYNC_2_MERGE_BEHAVIOR);
		if (behavior == null) {
			behavior = SyncConstants.SYNC_2_DEFAULT_MERGE_BEHAVIOR;
		}
		return Context.getRegisteredComponent(behavior, MergeBehaviour.class);
	}

	public static String getEventHandlerName() {
		return Context.getAdministrationService().getGlobalProperty(SyncConstants.EVENT_HANDLER_KEY,
				SyncConstants.ATOMFEED_EVENT_HANDLER);
	}

	public static boolean parentInstanceUriIsEmpty() {
		return StringUtils.isBlank(SyncUtils.getParentBaseUrl(null));
	}
}
