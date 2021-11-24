package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;

import java.time.ZonedDateTime;

import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

public class CompetitionResourceDocs {

    public static final CompetitionResourceBuilder competitionResourceBuilder = newCompetitionResource()
            .withId(1L)
            .withName("competition name")
            .withStartDate(ZonedDateTime.now())
            .withEndDate(ZonedDateTime.now().plusDays(30))
            .withRegistrationCloseDate(ZonedDateTime.now().plusDays(2))
            .withAssessorAcceptsDate(ZonedDateTime.now().plusDays(35))
            .withAssessorDeadlineDate(ZonedDateTime.now().plusDays(40))
            .withFundersPanelDate(ZonedDateTime.now().plusDays(42))
            .withFundersPanelEndDate(ZonedDateTime.now().plusDays(44))
            .withAssessorFeedbackDate(ZonedDateTime.now().plusDays(56))
            .withReleaseFeedbackDate(ZonedDateTime.now().plusDays(62))
            .withMaxResearchRatio(20)
            .withAcademicGrantClaimPercentage(100)
            .withCompetitionCode("COMP-1")
            .withCompetitionType(1L)
            .withExecutive(1L)
            .withLeadTechnologist(1L)
            .withLeadTechnologistName("Innovation Lead")
            .withInnovationAreas(singleton(1L))
            .withInnovationAreaNames(singleton("Tech"))
            .withInnovationSector(2L)
            .withInnovationSectorName("IT")
            .withLeadApplicantType(asList(1L, 2L))
            .withPafCode("PAF-123")
            .withBudgetCode("BUDGET-456")
            .withActivityCode("Activity-Code")
            .withNonIfs(true)
            .withNonIfsUrl("https://google.co.uk")
            .withMilestones(asList(1L, 2L, 3L))
            .withTermsAndConditions(new GrantTermsAndConditionsResource("T&C", "terms-and-conditions-template", 1))
            .withFundingRules(FundingRules.STATE_AID)
            .withIncludeJesForm(true)
            .withFundingType(FundingType.PROCUREMENT)
            .withCompetitionTerms((FileEntryResource) null)
            .withAlwaysOpen(false);
}
