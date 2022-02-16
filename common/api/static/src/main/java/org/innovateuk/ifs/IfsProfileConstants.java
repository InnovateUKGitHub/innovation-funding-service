package org.innovateuk.ifs;

/**
 * Constant representations of profiles for typesafe use including in annotations
 *
 * @Profile(IfsProfileConstants.FOO)
 */
public class IfsProfileConstants {

    public static final String DEBUG = "DEBUG";

    public static final String STUBDEV = "STUBDEV";
    public static final String NOT_STUBDEV = "!" + STUBDEV;

    public static final String INTEGRATION_TEST = "integration-test";
    public static final String NOT_INTEGRATION_TEST = "!" + INTEGRATION_TEST;

    public static final String AMQP_PROFILE = "AMQP";
    public static final String NOT_AMQP_PROFILE = "!" + AMQP_PROFILE;

    public static final String QUESTIONNAIRE_CONFIGURE = "questionnaire-configure";

}
