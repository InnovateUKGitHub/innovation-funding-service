package org.innovateuk.ifs.starter.common.util;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.IfsProfileConstants;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.AbstractEnvironment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProfileUtilsTest {

    @Test
    public void buildSpringActiveProfilesString() {
        assertThat(ProfileUtils.activeProfilesString("one"),
            equalTo(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME + "=one"));

        assertThat(ProfileUtils.activeProfilesString(
                ImmutableList.of(IfsProfileConstants.STUBDEV, IfsProfileConstants.AMQP_PROFILE)),
            equalTo(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME + "=" +
                IfsProfileConstants.STUBDEV + "," + IfsProfileConstants.AMQP_PROFILE));
    }

}