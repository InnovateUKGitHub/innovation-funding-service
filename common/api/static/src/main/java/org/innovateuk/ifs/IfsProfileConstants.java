package org.innovateuk.ifs;

/**
 * Constant representations of profiles for typesafe use including in annotations
 *
 * @Profile(IfsProfileConstants.FOO)
 */
public class IfsProfileConstants {

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

    /**
     * Used to denote the presence of AMQP in the deployment
     * Audit can then push over AMQP instead of logging
     */
    public static final String AMQP_PROFILE = "AMQP";
    public static final String NOT_AMQP_PROFILE = "!" + AMQP_PROFILE;

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
    public static final String GLUSTER_STORAGE = "GLUSTER_STORAGE";
    public static final String S3_STORAGE = "S3_STORAGE";

}
