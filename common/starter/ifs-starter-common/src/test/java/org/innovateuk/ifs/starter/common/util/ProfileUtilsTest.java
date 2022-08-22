package org.innovateuk.ifs.starter.common.util;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.IfsProfileConstants;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.AbstractEnvironment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ProfileUtilsTest {

    @Test
    void buildSpringActiveProfilesString() {
        assertThat(ProfileUtils.activeProfilesString("one"),
            equalTo(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME + "=one"));

        assertThat(ProfileUtils.activeProfilesString(
                ImmutableList.of(IfsProfileConstants.STUBDEV, IfsProfileConstants.LOCAL_STORAGE)),
            equalTo(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME + "=" +
                IfsProfileConstants.STUBDEV + "," + IfsProfileConstants.LOCAL_STORAGE));
    }

}