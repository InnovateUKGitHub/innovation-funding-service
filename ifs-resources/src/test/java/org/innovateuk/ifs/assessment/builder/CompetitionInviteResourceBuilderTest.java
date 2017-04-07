package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.junit.Assert.assertEquals;

/**
 * Builder for {@link CompetitionInviteResource}
 */
public class CompetitionInviteResourceBuilderTest {
    @Test
    public void buildOne() {
        Long expectedId = 7L;
        String expectedCompetitionName = "Juggling craziness";
        ZonedDateTime expectedAcceptsDate = ZonedDateTime.now().plusDays(1);
        ZonedDateTime expectedDeadlineDate = ZonedDateTime.now();
        ZonedDateTime expectedBriefingDate = ZonedDateTime.now().minusDays(1);
        BigDecimal expectedAssessorPay = BigDecimal.ONE;
        String expectedEmail = "tom@poly.io";
        String expectedHash = "inviteHash";
        InnovationAreaResource expectedCategory = newInnovationAreaResource().build();

        CompetitionInviteResource invite = newCompetitionInviteResource()
                .withIds(expectedId)
                .withCompetitionName(expectedCompetitionName)
                .withAcceptsDate(expectedAcceptsDate)
                .withDeadlineDate(expectedDeadlineDate)
                .withBriefingDate(expectedBriefingDate)
                .withAssessorPay(expectedAssessorPay)
                .withEmail(expectedEmail)
                .withHash(expectedHash)
                .withInnovationArea(expectedCategory)
                .build();

        assertEquals(expectedId, invite.getId());
        assertEquals(expectedCompetitionName, invite.getCompetitionName());
        assertEquals(expectedAcceptsDate, invite.getAcceptsDate());
        assertEquals(expectedDeadlineDate, invite.getDeadlineDate());
        assertEquals(expectedBriefingDate, invite.getBriefingDate());
        assertEquals(expectedAssessorPay, invite.getAssessorPay());
        assertEquals(expectedEmail, invite.getEmail());
        assertEquals(expectedHash, invite.getHash());
        assertEquals(expectedCategory, invite.getInnovationArea());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {7L, 13L};
        String[] expectedCompetitionNames = {"Juggling craziness", "Intermediate Juggling"};
        ZonedDateTime[] expectedAcceptsDates = {ZonedDateTime.now().plusDays(1), ZonedDateTime.now().plusDays(1).plusHours(1)};
        ZonedDateTime[] expectedDeadlineDates = {ZonedDateTime.now(), ZonedDateTime.now().plusHours(1)};
        ZonedDateTime[] expectedBriefingDates = {ZonedDateTime.now().minusDays(1), ZonedDateTime.now().minusDays(1).plusHours(1)};
        BigDecimal[] expectedAssessorPays = {BigDecimal.ONE, BigDecimal.TEN};
        String[] expectedEmails = {"tom@poly.io", "steve.smith@empire.com"};
        String[] expectedHashes = {"hash1", "hash2"};
        List<InnovationAreaResource> expectedCategories = newInnovationAreaResource().build(2);

        List<CompetitionInviteResource> invites = newCompetitionInviteResource()
                .withIds(expectedIds)
                .withCompetitionName(expectedCompetitionNames)
                .withAcceptsDate(expectedAcceptsDates)
                .withDeadlineDate(expectedDeadlineDates)
                .withBriefingDate(expectedBriefingDates)
                .withAssessorPay(expectedAssessorPays)
                .withEmail(expectedEmails)
                .withHash(expectedHashes)
                .withInnovationArea(expectedCategories.get(0), expectedCategories.get(1))
                .build(2);

        CompetitionInviteResource first = invites.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedCompetitionNames[0], first.getCompetitionName());
        assertEquals(expectedAcceptsDates[0], first.getAcceptsDate());
        assertEquals(expectedDeadlineDates[0], first.getDeadlineDate());
        assertEquals(expectedBriefingDates[0], first.getBriefingDate());
        assertEquals(expectedAssessorPays[0], first.getAssessorPay());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedHashes[0], first.getHash());
        assertEquals(expectedCategories.get(0), first.getInnovationArea());

        CompetitionInviteResource second = invites.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedCompetitionNames[1], second.getCompetitionName());
        assertEquals(expectedAcceptsDates[1], second.getAcceptsDate());
        assertEquals(expectedDeadlineDates[1], second.getDeadlineDate());
        assertEquals(expectedBriefingDates[1], second.getBriefingDate());
        assertEquals(expectedAssessorPays[1], second.getAssessorPay());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedHashes[1], second.getHash());
        assertEquals(expectedCategories.get(1), second.getInnovationArea());
    }
}
