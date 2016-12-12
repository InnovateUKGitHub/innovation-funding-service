package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.junit.Assert.assertEquals;

/**
 * Builder for {@link CompetitionInviteResource}
 */
public class CompetitionInviteResourceBuilderTest {
    @Test
    public void buildOne() {
        Long expectedId = 7L;
        String expectedCompetitionName = "Juggling craziness";
        LocalDateTime expectedAcceptsDate = LocalDateTime.now().plusDays(1);
        LocalDateTime expectedDeadlineDate = LocalDateTime.now();
        LocalDateTime expectedBriefingDate = LocalDateTime.now().minusDays(1);
        BigDecimal expectedAssessorPay = BigDecimal.ONE;
        String expectedEmail = "tom@poly.io";

        CompetitionInviteResource invite = newCompetitionInviteResource()
                .withIds(expectedId)
                .withCompetitionName(expectedCompetitionName)
                .withAcceptsDate(expectedAcceptsDate)
                .withDeadlineDate(expectedDeadlineDate)
                .withBriefingDate(expectedBriefingDate)
                .withAssessorPay(expectedAssessorPay)
                .withEmail(expectedEmail)
                .build();

        assertEquals(expectedId, invite.getId());
        assertEquals(expectedCompetitionName, invite.getCompetitionName());
        assertEquals(expectedAcceptsDate, invite.getAcceptsDate());
        assertEquals(expectedDeadlineDate, invite.getDeadlineDate());
        assertEquals(expectedBriefingDate, invite.getBriefingDate());
        assertEquals(expectedAssessorPay, invite.getAssessorPay());
        assertEquals(expectedEmail, invite.getEmail());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {7L, 13L};
        String[] expectedCompetitionNames = {"Juggling craziness", "Intermediate Juggling"};
        LocalDateTime[] expectedAcceptsDates = {LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1)};
        LocalDateTime[] expectedDeadlineDates = {LocalDateTime.now(), LocalDateTime.now().plusHours(1)};
        LocalDateTime[] expectedBriefingDates = {LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(1)};
        BigDecimal[] expectedAssessorPays = {BigDecimal.ONE, BigDecimal.TEN};
        String[] expectedEmails = {"tom@poly.io", "steve.smith@empire.com"};

        List<CompetitionInviteResource> invites = newCompetitionInviteResource()
                .withIds(expectedIds)
                .withCompetitionName(expectedCompetitionNames)
                .withAcceptsDate(expectedAcceptsDates)
                .withDeadlineDate(expectedDeadlineDates)
                .withBriefingDate(expectedBriefingDates)
                .withAssessorPay(expectedAssessorPays)
                .withEmail(expectedEmails)
                .build(2);

        CompetitionInviteResource first = invites.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedCompetitionNames[0], first.getCompetitionName());
        assertEquals(expectedAcceptsDates[0], first.getAcceptsDate());
        assertEquals(expectedDeadlineDates[0], first.getDeadlineDate());
        assertEquals(expectedBriefingDates[0], first.getBriefingDate());
        assertEquals(expectedAssessorPays[0], first.getAssessorPay());
        assertEquals(expectedEmails[0], first.getEmail());

        CompetitionInviteResource second = invites.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedCompetitionNames[1], second.getCompetitionName());
        assertEquals(expectedAcceptsDates[1], second.getAcceptsDate());
        assertEquals(expectedDeadlineDates[1], second.getDeadlineDate());
        assertEquals(expectedBriefingDates[1], second.getBriefingDate());
        assertEquals(expectedAssessorPays[1], second.getAssessorPay());
        assertEquals(expectedEmails[1], second.getEmail());


    }
}
