package org.innovateuk.ifs;

/**
 * Constant representations of profiles for typesafe use including in annotations
 *
 * @Profile(IfsProfileConstants.FOO)
 */
public class IfsProfileConstants {

    public static final String MOCK_BEAN_TEST = "MOCK_BEAN_TEST";

    private IfsProfileConstants() {
        // singleton
    }

    /**
     * Used on laptop builds for stub based work
     */
    public static final String STUBDEV = "STUBDEV";
    public static final String NOT_STUBDEV = "!" + STUBDEV;

    /**
     * Used for local k8s builds via skaffold
     */
    public static final String DEV = "dev";
    public static final String NOT_DEV = "!" + DEV;

    /**
     * Use in test scope to denote integration tests
     */
    public static final String INTEGRATION_TEST = "integration-test";
    public static final String NOT_INTEGRATION_TEST = "!" + INTEGRATION_TEST;

    public static final String TEST = "TEST";
    public static final String NOT_TEST = "!" + TEST;

    /**
     * Used to denote the presence of AMQP in the deployment
     * This means something for audit and messaging.
     */
    public static final String DISABLE_AMQP = "DISABLE_AMQP";
    public static final String AMQP_ENABLED = "!" + DISABLE_AMQP;

    /**
     * Used to enable simple and redis caching.
     */
    public static final String SIMPLE_CACHE = "SIMPLE_CACHE";
    public static final String REDIS_STANDALONE_CACHE = "REDIS_STANDALONE_CACHE";
    public static final String REDIS_CLUSTER_CACHE = "REDIS_CLUSTER_CACHE";

    /**
     * File Storage Profiles
     */
    public static final String STUB_AV_SCAN = "STUB_AV_SCAN";
    public static final String NOT_STUB_AV_SCAN = "!" + STUB_AV_SCAN;

    public static final String LOCAL_STORAGE = "LOCAL_STORAGE";
    public static final String NOT_LOCAL_STORAGE = "!" + LOCAL_STORAGE;
    public static final String S3_STORAGE = "S3_STORAGE";

    /**
     * Web-core - just means the config from application-web-core.yml is pulled in
     */
    public static final String WEB_CORE = "web-core";

}
