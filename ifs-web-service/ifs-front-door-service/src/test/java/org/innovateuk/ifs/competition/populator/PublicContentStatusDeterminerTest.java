package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.status.PublicContentStatusDeterminer;
import org.innovateuk.ifs.competition.status.PublicContentStatusText;
import org.junit.Ignore;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.junit.Assert.assertEquals;

public class PublicContentStatusDeterminerTest {

    @Test
    public void getApplicablePublicContentStatusText_openDateInFutureReturnsOpeningSoon() throws Exception {
        PublicContentStatusDeterminer publicContentStatusDeterminer = new PublicContentStatusDeterminer();

        ZonedDateTime inOneMonth = ZonedDateTime.now().plusMonths(1L);
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1L);

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withCompetitionOpenDate(tomorrow)
                .withCompetitionCloseDate(inOneMonth).build();

        PublicContentStatusText result = publicContentStatusDeterminer.getApplicablePublicContentStatusText(publicContentItemResource);

        assertEquals(PublicContentStatusText.OPENING_SOON, result);
    }

    @Test
    public void getApplicablePublicContentStatusText_openDateInPastAndClosingDateMoreThanTwoWeeksAwayReturnsOpenNow() throws Exception {
        PublicContentStatusDeterminer publicContentStatusDeterminer = new PublicContentStatusDeterminer();

        ZonedDateTime inOneMonth = ZonedDateTime.now().plusMonths(1L);
        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1L);

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withCompetitionOpenDate(yesterday)
                .withCompetitionCloseDate(inOneMonth).build();

        PublicContentStatusText result = publicContentStatusDeterminer.getApplicablePublicContentStatusText(publicContentItemResource);

        assertEquals(PublicContentStatusText.OPEN_NOW, result);
    }

    //TODO IFS-948: Fix this test - intermittently fails
    @Ignore
    @Test
    public void getApplicablePublicContentStatusText_openDateInPastAndClosingDateInLessThanTwoWeeksAwayReturnsClosingSoon() throws Exception {
        PublicContentStatusDeterminer publicContentStatusDeterminer = new PublicContentStatusDeterminer();

        ZonedDateTime inTwoWeeks = ZonedDateTime.now().plusDays(14L);
        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1L);

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withCompetitionOpenDate(yesterday)
                .withCompetitionCloseDate(inTwoWeeks).build();

        PublicContentStatusText result = publicContentStatusDeterminer.getApplicablePublicContentStatusText(publicContentItemResource);

        assertEquals(PublicContentStatusText.CLOSING_SOON, result);
    }

    @Test
    public void getApplicablePublicContentStatusText_openDateInPastAndClosingDateJustUnderTwoWeeksAwayReturnsOpenNow() throws Exception {
        PublicContentStatusDeterminer publicContentStatusDeterminer = new PublicContentStatusDeterminer();

        ZonedDateTime inTwoWeeksMinusOneDay = ZonedDateTime.now().plusDays(13L);
        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1L);

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withCompetitionOpenDate(yesterday)
                .withCompetitionCloseDate(inTwoWeeksMinusOneDay).build();

        PublicContentStatusText result = publicContentStatusDeterminer.getApplicablePublicContentStatusText(publicContentItemResource);

        assertEquals(PublicContentStatusText.CLOSING_SOON, result);
    }

    @Test
    public void getApplicablePublicContentStatusText_openDateInPastAndClosingDateJustOverTwoWeeksAwayReturnsClosingSoon() throws Exception {
        PublicContentStatusDeterminer publicContentStatusDeterminer = new PublicContentStatusDeterminer();

        ZonedDateTime inTwoWeeksPlusOneDay = ZonedDateTime.now().plusDays(15L);
        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1L);

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withCompetitionOpenDate(yesterday)
                .withCompetitionCloseDate(inTwoWeeksPlusOneDay).build();

        PublicContentStatusText result = publicContentStatusDeterminer.getApplicablePublicContentStatusText(publicContentItemResource);

        assertEquals(PublicContentStatusText.OPEN_NOW, result);
    }

    @Test
    public void getApplicablePublicContentStatusText_openingDateAndClosingDateInPastReturnsClosingSoon() throws Exception {
        PublicContentStatusDeterminer publicContentStatusDeterminer = new PublicContentStatusDeterminer();

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1L);

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withCompetitionOpenDate(yesterday)
                .withCompetitionCloseDate(yesterday).build();

        PublicContentStatusText result = publicContentStatusDeterminer.getApplicablePublicContentStatusText(publicContentItemResource);

        assertEquals(PublicContentStatusText.CLOSING_SOON, result);
    }
}