package org.openmrs.module.sync2;

public class SyncConstants {

    public static final String PARENT_USERNAME_PROPERTY = "sync2.parent.user.login";

    public static final String PARENT_PASSWORD_PROPERTY = "sync2.parent.user.password";

    public static final String LOCAL_USERNAME_PROPERTY = "sync2.local.user.login";

    public static final String LOCAL_PASSWORD_PROPERTY = "sync2.local.user.password";

    public static final String SYNC2_PATH_TO_DEFAULT_CONFIGURATION = "defaultSyncConfiguration.json";

    public static final String SYNC2_NAME_OF_CUSTOM_CONFIGURATION = "sync2.json";

    public static final String CONFIGURATION_DIR = "configuration";

    public static final String AUDIT_MESSAGE_UUID_FIELD_NAME = "uuid";

    public static final String AUDIT_MESSAGE_ID_FIELD_NAME = "id";

    public static final String PARENT_OBJECT_HASHCODE_ID_FIELD_NAME = "id";

    public static final String PARENT_OBJECT_HASHCODE_UUID_FIELD_NAME = "uuid";

    public static final String PARENT_OBJECT_HASHCODE_OBJECT_UUID_FIELD_NAME = "objectUuid";

    public static final String SUCCESS_MESSAGE = "Success";

    public static final String RESOURCE_PREFERRED_CLIENT = "sync2.resource.preferred.client";
  
    public static final String AUDIT_MESSAGE_OPERATION_FIELD_NAME = "operation";

    public static final String AUDIT_MESSAGE_RESOURCE_FIELD_NAME = "resourceName";

    public static final String AUDIT_MESSAGE_STATUS_FIELD_NAME = "success";

    public static final String AUDIT_MESSAGE_CREATOR_INSTANCE_ID = "creatorInstanceId";

    public static final String AUDIT_MESSAGE_VOIDED_FIELD_NAME = "voided";

    public static final String ALL_OPERATIONS = "ALL";

    public static final String AUDIT_MESSAGE_MERGE_CONFLICT_UUID_NAME = "mergeConflictUuid";

    public static final String PULL_OPERATION = "PULL";

    public static final String PUSH_OPERATION = "PUSH";

    public static final String FHIR_CLIENT = "fhir";

    public static final String REST_CLIENT = "rest";

    public static final String DEFAULT_SYNC_2_CLIENT = REST_CLIENT;

    public static final String RECENT_FEED = "recent";

    public static final String ACTION_VOIDED = "VOIDED";

    public static final String ACTION_DELETED = "DELETED";

    public static final String ACTION_RETIRED = "RETIRED";

    public static final String ACTION_UPDATED = "UPDATED";

    public static final String ACTION_CREATED = "CREATED";

    public static final int ZERO = 0;

    public static final String SYNC2_REST_ENDPOINT = "/ws/rest/sync2";

    public static final String TAG_SERVICE_BEAN = "atomfeed.tagsService";

    public static final String MERGE_CONFLICT_ID_FIELD_NAME = "id";

    public static final String MERGE_CONFLICT_UUID_FIELD_NAME = "uuid";

    public static final String SYNC_2_MERGE_BEHAVIOR = "sync2.mergeBehavior";

    public static final String SYNC_2_DEFAULT_MERGE_BEHAVIOR = "sync2.restrictConflictMergeBehaviour";

    public static final String AUDIT_MESSAGE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String EVENT_HANDLER_KEY = "sync2.event.handler";

    public static final String ATOMFEED_EVENT_HANDLER = "atomfeed";

    public static final String SYNC_PUSH_SERVICE_BEAN = "sync2.syncPushService";

    public static final String SYNC_PULL_SERVICE_BEAN = "sync2.syncPullService";

    public static final String SYNC_CATEGORY_HELPER = "sync2.categoryHelper";

    private SyncConstants() {}
}
