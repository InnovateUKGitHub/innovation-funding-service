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

}
