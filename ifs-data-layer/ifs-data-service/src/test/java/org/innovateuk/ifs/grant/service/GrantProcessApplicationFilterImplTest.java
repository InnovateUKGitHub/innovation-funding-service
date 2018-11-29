package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.sil.grant.resource.GrantBuilder.newGrant;
import static org.junit.Assert.*;

public class GrantProcessApplicationFilterImplTest {
    @Test
    public void shouldAlwaysSend() {
        GrantProcessApplicationFilter filter = new GrantProcessApplicationFilterImpl(null);
        Grant grant = newGrant().build();
        assertThat(filter.shouldSend(grant), is(true));
        assertThat(filter.generateFilterReason(grant), is(nullValue()));
    }

    @Test
    public void shouldConditionallySend() {
        GrantProcessApplicationFilter filter = new GrantProcessApplicationFilterImpl("123,456");
        Grant grant = newGrant().withCompetitionId(123L).build();
        assertThat(filter.generateFilterReason(grant), filter.shouldSend(grant), is(true));
        assertThat(filter.generateFilterReason(grant), is(nullValue()));

        grant = newGrant().withCompetitionId(124L).build();
        assertThat(filter.shouldSend(grant), is(false));
        assertThat(filter.generateFilterReason(grant), equalTo("Competition ID 124 not in [123, 456]"));
    }
}