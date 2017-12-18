package org.openmrs.module.sync2.api.utils;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.RESOURCE_PREFERRED_CLIENT;


public class SyncUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncUtils.class);

    private static final String FHIR = "fhir";
    private static final String REST = "rest";

    public static String readResourceFile(String file) throws SyncException {
        try (InputStream in = SyncUtils.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new SyncException("Resource '" + file + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static SyncConfiguration parseJsonFileToSyncConfiguration(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(readResourceFile(resourcePath), SyncConfiguration.class);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static SyncConfiguration parseJsonStringToSyncConfiguration(String value) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(value, SyncConfiguration.class);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static boolean isValidateJson(String json) throws SyncException {
        try {
            final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json);
            while (parser.nextToken() != null) {}
        } catch (JsonParseException jpe) {
            return false;
        } catch (IOException e) {
            throw new SyncException(e);
        }
        return true;
    }

    public static String writeSyncConfigurationToJsonString(SyncConfiguration syncConfiguration) throws SyncException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            return writer.writeValueAsString(syncConfiguration);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static void writeSyncConfigurationToJsonFile(SyncConfiguration syncConfigurations, String file)
            throws SyncException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            File resultFile = new File(SyncUtils.class.getClassLoader().getResource("").getPath() + file);
            writer.writeValue(resultFile, syncConfigurations);
        } catch (IOException e) {
            throw new SyncException(e);
        }
    }

    public static boolean resourceFileExists(String path) {
        InputStream in = SyncUtils.class.getClassLoader().getResourceAsStream(path);
        return in != null;
    }


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

    public static String getBaseUrl(String stringUri) {

        try {
            URI uri = new URI(stringUri);

            return uri.getScheme() + "://" + uri.getAuthority() + "/";
        } catch(URISyntaxException e) {
            throw new SyncException(String.format("Bad Atomfeed URI: %s. ", stringUri), e);
        }

    }

    public static String getCategoryAndActionByTag(String tag) {
        Map<String, String> map = parseTag(tag);
        return map.get("Category.term");
    }

    private static HashMap<String, String> parseTag(String tag) {
        final HashMap<String, String> map = new HashMap<>();

        for (String pair : tag.split("\n")) {
            String[] keyValue = pair.split("=");
            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }

    public static String getPreferredUrl(Map<String, String> resourceLinks) {
        String preferredClient = getPreferredClient();
        String result = resourceLinks.get(preferredClient);
        if (result == null && resourceLinks.size() > 0) {
            result = getFirstResourceLink(resourceLinks);
        }
        return result;
    }

    private static String getFirstResourceLink(Map<String, String> resourceLinks) {
        if (resourceLinks.size() > 0) {
            return resourceLinks.values().iterator().next();
        }
        return "";
    }

    public static String getPreferredClient() {
        return Context.getAdministrationService().getGlobalProperty(RESOURCE_PREFERRED_CLIENT);
    }

    public static String extractUUIDFromResourceLinks(Map<String, String> resourceLinks) {
        for (String client : resourceLinks.keySet()) {
            switch (client) {
                case REST:
                    return extractUUIDFromRestResource(resourceLinks.get(client));
                case FHIR:
                    return extractUUIDFromFHIRResource(resourceLinks.get(client));
                default:
            }
        }

        LOGGER.error("Couldn't find any supported client to extract uuid from.");
        return null;
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

    public static String getResourceUrl(String client, String url) {
        switch (client) {
            case FHIR:
                return getFhirResourceUrl(url);
            case REST:
                return getRestResourceUrl(url);
            default:
                LOGGER.error("Couldn't find any supported client to extract resource url from.");
                return null;
        }
    }

    private static String getRestResourceUrl(String url) {
        return url.substring(0, url.lastIndexOf("/"));
        // todo throw custom sync2 exception if tokens.length != 6
    }

    private static String getFhirResourceUrl(String url) {
        return url.substring(0, url.lastIndexOf("/"));
        // todo throw custom sync2 exception if tokens.length != 5
    }
    
    public static <T> String serializeMap(Map<T, T> map) {
        try {
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(map);
        } catch (IOException ex) {
            throw new SyncException("Cannot serialize map", ex);
        }
    }
}
