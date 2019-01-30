package org.innovateuk.ifs.sil.grant.resource.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.sil.grant.resource.json.GrantConstants.GMT;

public class GrantJsonTest {
    private static final ZonedDateTime OFFER_DATE = ZonedDateTime
            .of(2018, 1, 2, 1, 1, 1, 1, GMT);
    private static final LocalDate START_DATE = LocalDate.of(2019, 3, 4);

    @Test
    public void shouldSerializeAndDeserialize() throws IOException {
        Grant in = new Grant();
        in.setGrantOfferLetterDate(OFFER_DATE);
        in.setStartDate(START_DATE);
        String json = new ObjectMapper().writeValueAsString(in);
        assertThat(json, containsString("\"golDate\":\"02/01/2018\""));
        assertThat(json, containsString("\"startDate\":\"04/03/2019\""));
        Grant out = new ObjectMapper().readValue(json, Grant.class);
        assertThat(out.getGrantOfferLetterDate().getYear(), equalTo(2018));
        assertThat(out.getStartDate().getYear(), equalTo(2019));
    }
}
