package org.openmrs.module.sync2.api.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.db.EventAction;
import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.enums.AtomfeedTagContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.FHIR_CLIENT_KEY;
import static org.openmrs.module.sync2.SyncConstants.RESOURCE_PREFERRED_CLIENT;
import static org.openmrs.module.sync2.SyncConstants.REST_CLIENT_KEY;

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

    public static String getParentBaseUrl(SyncConfigurationService configurationService) {
        return configurationService.getSyncConfiguration().getGeneral().getParentFeedLocation();
    }

    public static String getLocalBaseUrl(SyncConfigurationService configurationService) {
        return configurationService.getSyncConfiguration().getGeneral().getLocalFeedLocation();
    }

    public static String getPushPath(String pathWithId) {
        return pathWithId.substring(0, pathWithId.lastIndexOf("/"));
    }

    public static String getFullUrl(String baseAddress, String path) {
        return baseAddress + path;
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
                case REST_CLIENT_KEY:
                    return extractUUIDFromRestResource(resourceLinks.get(client));
                case FHIR_CLIENT_KEY:
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

    public static <T> String serializeMapToPrettyJson(Map<T, T> map) {
        try {
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(map);
        } catch (IOException ex) {
            throw new SyncException("Cannot serialize map", ex);
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
}
