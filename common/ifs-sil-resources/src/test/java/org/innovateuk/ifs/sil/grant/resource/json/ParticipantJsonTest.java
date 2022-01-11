package org.innovateuk.ifs.sil.grant.resource.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsString;

public class ParticipantJsonTest {
    private static final BigDecimal PERCENTAGE_AWARD = BigDecimal.valueOf(12);
    private static final BigDecimal PERCENTAGE_CAP = BigDecimal.valueOf(34);
    private static final BigDecimal PERCENTAGE_OVERHEAD = BigDecimal.valueOf(56);

    @Test
    public void shouldSerializeAndDeserialize() throws IOException {
        Participant in = new Participant();
        in.setAwardRate(PERCENTAGE_AWARD);
        in.setCapLimit(PERCENTAGE_CAP);
        in.setOverheadRate(PERCENTAGE_OVERHEAD);
        String json = new ObjectMapper().writeValueAsString(in);
        assertThat(json, containsString("\"awardRate\":\"" + PERCENTAGE_AWARD + "%\""));
        assertThat(json, containsString("\"capLimit\":\"" + PERCENTAGE_CAP + "%\""));
        assertThat(json, containsString("\"overheadRate\":\"" + PERCENTAGE_OVERHEAD + "%\""));
        Participant out = new ObjectMapper().readValue(json, Participant.class);
        assertThat(out.getAwardRate(), comparesEqualTo(PERCENTAGE_AWARD));
        assertThat(out.getCapLimit(), comparesEqualTo(PERCENTAGE_CAP));
        assertThat(out.getOverheadRate(), comparesEqualTo(PERCENTAGE_OVERHEAD));
    }
}
