package org.innovateuk.ifs.nonifs.formpopulator;


import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.nonifs.form.NonIfsDetailsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NonIfsDetailsFormPopulatorTest {

    private static final String COMPETITION_NAME = "COMPETITION_NAME";
    private static final Long INNOVATION_SECTOR = 1L;
    private static final Long INNOVATION_AREA = 2L;
    private static final String COMPETITION_URL = "COMPETITION_URL";
    private static final ZonedDateTime NOTIFIED = ZonedDateTime.now().plusDays(1);
    private static final ZonedDateTime OPEN = ZonedDateTime.now().plusDays(2);
    private static final ZonedDateTime CLOSE = ZonedDateTime.now().plusDays(3);

    @InjectMocks
    private NonIfsDetailsFormPopulator target;

    @Test
    public void testPopulate() {
        CompetitionResource competition = newCompetitionResource()
                .withName(COMPETITION_NAME).withInnovationSector(INNOVATION_SECTOR).withInnovationAreas(asSet(INNOVATION_AREA))
                .withNonIfsUrl(COMPETITION_URL).withReleaseFeedbackDate(NOTIFIED).withStartDate(OPEN).withEndDate(CLOSE)
                .build();

        NonIfsDetailsForm form = target.populate(competition);


        assertThat(form.getTitle(), equalTo(COMPETITION_NAME));
        assertThat(form.getUrl(), equalTo(COMPETITION_URL));
        assertThat(form.getInnovationSectorCategoryId(), equalTo(INNOVATION_SECTOR));
        assertThat(form.getInnovationAreaCategoryId(), equalTo(INNOVATION_AREA));
        assertThat(form.getApplicantNotifiedDate().getDate(), equalTo(NOTIFIED));
        assertThat(form.getOpenDate().getDate(), equalTo(OPEN));
        assertThat(form.getCloseDate().getDate(), equalTo(CLOSE));

    }
}
