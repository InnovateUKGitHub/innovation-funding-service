package org.innovateuk.ifs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.ProfileUtils.PROFILE_CONSTANT;
import static org.innovateuk.ifs.ProfileUtils.PROFILE_CONSTANT_ENV;

@ExtendWith(SystemStubsExtension.class)
class ProfileUtilsTest {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    @Test
    void isProfileActive() {
        environmentVariables.set(PROFILE_CONSTANT_ENV, IfsProfileConstants.STUBDEV + "," + IfsProfileConstants.DEV + " , FOO");
        assertThat(ProfileUtils.isProfileActive(IfsProfileConstants.STUBDEV), equalTo(true));
        assertThat(ProfileUtils.isProfileActive(IfsProfileConstants.DEV), equalTo(true));
        assertThat(ProfileUtils.isProfileActive("FOO"), equalTo(true));
        assertThat(ProfileUtils.isProfileActive("BAR", "FOO"), equalTo(true));

        environmentVariables.set(PROFILE_CONSTANT, IfsProfileConstants.STUBDEV);
        assertThat(ProfileUtils.isProfileActive(IfsProfileConstants.STUBDEV), equalTo(true));
        assertThat(ProfileUtils.isProfileActive("asd"), equalTo(false));
    }

    @Test
    void getProfiles() {
        environmentVariables.set(PROFILE_CONSTANT, IfsProfileConstants.STUBDEV + "," + IfsProfileConstants.DEV);
        environmentVariables.set(PROFILE_CONSTANT_ENV, IfsProfileConstants.STUBDEV + "," + IfsProfileConstants.DEV + "," + IfsProfileConstants.AMQP_PROFILE);
        assertThat(ProfileUtils.getProfiles().size(), equalTo(3));
    }
}