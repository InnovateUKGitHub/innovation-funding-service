package org.innovateuk.ifs.testdata.data;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

public class CompetitionWebTestData {

    public static List<CompetitionResource> buildCompetitionResources() {
        return getCompetitionResourceBuilders().stream().map(CompetitionResourceBuilder::build).collect(Collectors.toList());
    }

    private static List<CompetitionResourceBuilder> getCompetitionResourceBuilders() {
        return asList(
                defaultGrantCompetition()
                        .withName("Connected digital additive manufacturing"),
                defaultGrantCompetition()
                        .withName("Sustainable living models for the future"),
                defaultGrantCompetition()
                        .withName("Internet of Things"),
                defaultGrantCompetition()
                        .withName("Photonics for health"),
                defaultGrantCompetition()
                        .withName("Integrated delivery programme - low carbon vehicles"),
                defaultGrantCompetition()
                        .withName("New designs for a circular economy"),
                defaultGrantCompetition()
                        .withName("Rolling stock future developments"),
                defaultGrantCompetition()
                        .withName("Home and industrial efficiency programme"),
                defaultGrantCompetition()
                        .withName("Machine learning for transport infrastructure"),
                defaultGrantCompetition()
                        .withName("Transforming big data"),
                defaultGrantCompetition()
                        .withName("Reducing carbon footprints"),
                defaultGrantCompetition()
                        .withName("Predicting market trends programme"),
                defaultGrantCompetition()
                        .withName("Aerospace technology investment sector"),
                defaultGrantCompetition()
                        .withName("Generic innovation"),
                defaultGrantCompetition()
                        .withName("Photonics for Research"),
                defaultGrantCompetition()
                        .withName("Photonics for Public"),
                defaultGrantCompetition()
                        .withName("Photonics for RTO and Business"),
                defaultGrantCompetition()
                        .withName("Photonics for All"),
                defaultGrantCompetition()
                        .withName("Assessments of load capabilities"),
                defaultGrantCompetition()
                        .withName("Expression of Interest: Quantum Computing algorithms for combating antibiotic resistance through simulation"),
                defaultGrantCompetition()
                        .withName("Expression of Interest: Assistive technologies for caregivers"),
                defaultGrantCompetition()
                        .withName("Low-cost propulsion mechanisms for subsonic travel"),
                defaultGrantCompetition()
                        .withName("Smart monitoring in high-pressure engineering systems"),
                defaultGrantCompetition()
                        .withName("Reusability of waste material rocketry components"),
                defaultGrantCompetition()
                        .withName("Investor"),
                defaultGrantCompetition()
                        .withName("Personalised Smart HUDs for space helmets"),
                defaultGrantCompetition()
                        .withName("Biosciences round three: plastic recovery in the industrial sector"),
                defaultGrantCompetition()
                        .withName("Integrated delivery programme - solar vehicles"),
                defaultGrantCompetition()
                        .withName("Enhanced energy saving competition"),
                defaultGrantCompetition()
                        .withName("Performance testing competition"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 1"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 2"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 3"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 4"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 5"),
                defaultGrantCompetition()
                        .withName("Non IFS Comp 6"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 7"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 8"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 9"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 10"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 11"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 12"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 13"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 14"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 15"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 16"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 17"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 18"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 19"),
                defaultGrantCompetition()
                        .withName("Webtest Non IFS Comp 20"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 1"),
                defaultGrantCompetition()
                        .withName(),
                defaultGrantCompetition()
                        .withName(),
                defaultGrantCompetition()
                        .withName(),
        );
    }

    private static CompetitionResourceBuilder defaultGrantCompetition() {
        return newCompetitionResource()
                .withCompetitionType(1L) // Programme
                .withInnovationAreas(asSet(5L)) // Digital industries
                .withInnovationSectorName("Materials and manufacturing")
                .withResearchCategories(asSet(33L)) // Feasibility studies
                .withCollaborationLevel(CollaborationLevel.SINGLE_OR_COLLABORATIVE)
                .withLeadApplicantType(asList(1L)) // Buisness
                .withMaxResearchRatio(30)
                .withResubmission(true)
                .withMultiStream(false)
                .withLeadTechnologist(24L) // ian.cooper@innovateuk.test
                .withExecutive(20L) // john.doe@innovateuk.test
                .withSetupComplete(true)
                .withPafCode("875")
                .withBudgetCode("DET1536/1537")
                .withActivityCode("16014")
                .withCompetitionCode("2/1/1506")
                .withAssessorFinanceView(AssessorFinanceView.OVERVIEW) // probably shouldn't have in this builder
                .withFundingType(FundingType.GRANT)
                .withNonIfs(false)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .withIncludeJesForm(true)
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD)
                .withIncludeProjectGrowthTable(true)
                .withIncludeYourOrganisationSection(true)
                .withFundingRules(FundingRules.STATE_AID)
                .withAlwaysOpen(false);
    }
}
