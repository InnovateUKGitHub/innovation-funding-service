package org.innovateuk.ifs.testdata.data;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.testdata.builders.CompetitionLineBuilder;
import org.innovateuk.ifs.testdata.builders.data.CompetitionLine;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.testdata.builders.CompetitionLineBuilder.newCompetitionLine;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

public class CompetitionWebTestData {

    public static List<CompetitionLine> buildCompetitionLines() {
        return getCompetitionLineBuilders().stream().map(CompetitionLineBuilder::build).collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getCompetitionLineBuilders() {
        return combineLists(
                getNonIfsLineBuilders(),
                getReadyToOpenCompetitionLineBuilders(),
                getOpenCompetitionLineBuilders(),
                getClosedCompetitionLineBuilders(),
                getInAssessmentCompetitionLineBuilders(),
                getAssessorFeedbackCompetitionLineBuilders(),
                getFundersPanelCompetitionLineBuilders(),
                getProjectSetupCompetitionLineBuilders()
        );
    }

    private static List<CompetitionLineBuilder> getProjectSetupCompetitionLineBuilders() {
        return asList(
                defaultGrantCompetition()
                        .withName("Connected digital additive manufacturing"),
                defaultGrantCompetition()
                        .withName("New designs for a circular economy"),
                defaultGrantCompetition()
                        .withName("Rolling stock future developments"),
                defaultGrantCompetition()
                        .withName("Biosciences round three: plastic recovery in the industrial sector"),
                defaultGrantCompetition()
                        .withName("Integrated delivery programme - solar vehicles"),
                defaultGrantCompetition()
                        .withName("Enhanced energy saving competition")
                        .withLeadApplicantType(asList(1L, 2L, 3L, 4L)),
                defaultGrantCompetition()
                        .withName("Growth table comp"),
                defaultGrantCompetition()
                        .withName("No Growth table comp")
                        .withIncludeProjectGrowthTable(false),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 1"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 2"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 3"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 4"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 5"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 6"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 7"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 8"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 9"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 10"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 11"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 12"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 13"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 14"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 15"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 16"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 17"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 18"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 19"),
                defaultGrantCompetition()
                        .withName("Project Setup Comp 20"),
                defaultLoanCompetition()
                        .withName("Project setup loan comp"),
                defaultGrantCompetition()
                        .withName("583 Covid deminis round 1 project setup"),
                defaultGrantCompetition()
                        .withName("Post award service competition"),
                defaultGrantCompetition()
                        .withName("Investor partnership project setup"),
                defaultGrantCompetition()
                        .withName("Connect competition"),
                defaultGrantCompetition()
                        .withName("Innovation continuity loan competition"),
                defaultGrantCompetition()
                        .withName("The Sustainable Innovation Fund: SBRI phase 1"),
                defaultGrantCompetition()
                        .withName("SBRI competition"),
                defaultKtpCompetition()
                        .withName("KTP Africa project setup"),
                defaultGrantCompetition()
                        .withName("Live project competition")
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.PROJECT_SETUP))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getInAssessmentCompetitionLineBuilders() {
        return asList(
                defaultGrantCompetition()
                        .withName("Sustainable living models for the future"),
                defaultGrantCompetition()
                        .withName("Assessments of load capabilities"),
                defaultGrantCompetition()
                        .withName("Expression of Interest: Assistive technologies for caregivers"),
                defaultGrantCompetition()
                        .withName("Living models for the future"),
                defaultGrantCompetition()
                        .withName("583 Covid deminis round 1"),
                defaultGrantCompetition()
                        .withName("Multiple choice assessed"),
                defaultKtpCompetition()
                        .withName("KTP assessment")
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getFundersPanelCompetitionLineBuilders() {
        return asList(
                defaultGrantCompetition()
                        .withName("Internet of Things"),
                defaultKtpCompetition()
                        .withName("KTP in panel"),
                defaultKtpCompetition()
                        .withName("KTP notifications")
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.FUNDERS_PANEL))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getReadyToOpenCompetitionLineBuilders() {
        return asList(
                defaultGrantCompetition()
                        .withName("Photonics for health"),
                defaultGrantCompetition()
                        .withName("Reducing carbon footprints")
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.READY_TO_OPEN))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getAssessorFeedbackCompetitionLineBuilders() {
        return asList(
                defaultGrantCompetition()
                        .withName("Integrated delivery programme - low carbon vehicles")
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.ASSESSOR_FEEDBACK))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getClosedCompetitionLineBuilders() {
        return asList(
                defaultGrantCompetition()
                        .withName("Machine learning for transport infrastructure"),
                defaultGrantCompetition()
                        .withName("Personalised Smart HUDs for space helmets"),
                defaultGrantCompetition()
                        .withName("Smart monitoring in high-pressure engineering systems")
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.CLOSED))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getOpenCompetitionLineBuilders() {
        return asList(
                defaultGrantCompetition()
                        .withName("Home and industrial efficiency programme"),
                defaultGrantCompetition()
                        .withName("Transforming big data"),
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
                        .withName("Expression of Interest: Quantum Computing algorithms for combating antibiotic resistance through simulation"),
                defaultGrantCompetition()
                        .withName("Low-cost propulsion mechanisms for subsonic travel"),
                defaultGrantCompetition()
                        .withName("Reusability of waste material rocketry components"),
                defaultInvestorPartnershipCompetition()
                        .withName("Investor"),
                defaultGrantCompetition()
                        .withName("Performance testing competition"),
                defaultGrantCompetition()
                        .withName("Procurement Competition"),
                defaultGrantCompetition()
                        .withName("Procurement Competition"),
                defaultLoanCompetition()
                        .withName("Loan Competition"),
                defaultGrantCompetition()
                        .withName("H2020 Performance testing competition"),
                defaultGrantCompetition()
                        .withName("International Competition"),
                defaultGrantCompetition()
                        .withName("596 Covid grants framework group"),
                defaultGrantCompetition()
                        .withName("599 Covid de minimis round 2"),
                defaultGrantCompetition()
                        .withName("Multiple choice open"),
                defaultGrantCompetition()
                        .withName("SBRI type one competition"),
                defaultKtpCompetition()
                        .withName("KTP new competition"),
                defaultLoanCompetition()
                        .withName("Competition not submitted before the deadline"),
                defaultLoanCompetition()
                        .withName("Competition for application submitted before competition closing time"),
                defaultGrantCompetition()
                        .withName("Innovation continuity loan"),
                defaultKtpCompetition()
                        .withName("KTP Africa Comp"),
                defaultKtpCompetition()
                        .withName("KTP cofunding"),
                defaultKtpCompetition()
                        .withName("KTP assessment Detailed Finances"),
                defaultKtpCompetition()
                        .withName("KTP assessment Overview Finances"),
                defaultKtpCompetition()
                        .withName("Non KTP competition all finance overview"),
                defaultKtpCompetition()
                        .withName("KTP cofunding single application"),
                defaultGrantCompetition()
                        .withName("No aid comp"),
                defaultGrantCompetition()
                        .withName("WTO comp"),
                defaultGrantCompetition()
                        .withName("Always open competition")
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.OPEN))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getNonIfsLineBuilders() {
        return asList(defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 1"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 2"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 3"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 4"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 5"),
                defaultNonIfsCompetition()
                        .withName("Non IFS Comp 6"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 7"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 8"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 9"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 10"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 11"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 12"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 13"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 14"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 15"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 16"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 17"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 18"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 19"),
                defaultNonIfsCompetition()
                        .withName("Webtest Non IFS Comp 20"));
    }

    private static CompetitionLineBuilder defaultGrantCompetition() {
        return newCompetitionLine()
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
                .withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
                .withFundingType(FundingType.GRANT)
                .withNonIfs(false)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .withIncludeJesForm(true)
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD)
                .withIncludeProjectGrowthTable(true)
                .withIncludeYourOrganisationSection(true)
                .withFundingRules(FundingRules.STATE_AID)
                .withPublished(true)
                .withAlwaysOpen(false);
    }

    private static CompetitionLineBuilder defaultInvestorPartnershipCompetition() {
        return newCompetitionLine()
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
                .withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
                .withFundingType(FundingType.INVESTOR_PARTNERSHIPS)
                .withNonIfs(false)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .withIncludeJesForm(true)
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD)
                .withIncludeProjectGrowthTable(true)
                .withIncludeYourOrganisationSection(true)
                .withFundingRules(FundingRules.STATE_AID)
                .withPublished(true)
                .withAlwaysOpen(false);
    }

    private static CompetitionLineBuilder defaultLoanCompetition() {
        return newCompetitionLine()
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
                .withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
                .withFundingType(FundingType.LOAN)
                .withNonIfs(false)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .withIncludeJesForm(true)
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD)
                .withIncludeProjectGrowthTable(true)
                .withIncludeYourOrganisationSection(true)
                .withFundingRules(FundingRules.STATE_AID)
                .withPublished(true)
                .withAlwaysOpen(false);
    }

    private static CompetitionLineBuilder defaultKtpCompetition() {
        return newCompetitionLine()
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
                .withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
                .withFundingType(FundingType.KTP)
                .withNonIfs(false)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .withIncludeJesForm(true)
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD)
                .withIncludeProjectGrowthTable(true)
                .withIncludeYourOrganisationSection(true)
                .withFundingRules(FundingRules.STATE_AID)
                .withPublished(true)
                .withAlwaysOpen(false);
    }

    private static CompetitionLineBuilder defaultNonIfsCompetition() {
        return newCompetitionLine()
                .withInnovationAreas(asSet(5L)) // Digital industries
                .withInnovationSectorName("Emerging and enabling")
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withAssessorFinanceView(AssessorFinanceView.OVERVIEW) // might not be needed
                .withPublished(true)
                .withNonIfs(true)
                .withNonIfsUrl("https://www.gov.uk/government/organisations/innovate-uk")
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD); // might not be needed
    }
}
