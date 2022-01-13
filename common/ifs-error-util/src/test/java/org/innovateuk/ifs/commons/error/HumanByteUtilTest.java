package org.innovateuk.ifs.commons.error;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class HumanByteUtilTest {

    @Test
    public void byteCountToHuman() {
        assertThat(HumanByteUtil.byteCountToHuman(5368709120L), equalTo("5120MB"));
        assertThat(HumanByteUtil.byteCountToHuman(1048576L), equalTo("1MB"));
        assertThat(HumanByteUtil.byteCountToHuman(1000000L), equalTo("976KB"));
        assertThat(HumanByteUtil.byteCountToHuman(5242880L), equalTo("5MB"));
        assertThat(HumanByteUtil.byteCountToHuman(1800L), equalTo("1KB"));
        assertThat(HumanByteUtil.byteCountToHuman(100L), equalTo("100B"));
        assertThat(HumanByteUtil.byteCountToHuman(10L), equalTo("10B"));

    }
}