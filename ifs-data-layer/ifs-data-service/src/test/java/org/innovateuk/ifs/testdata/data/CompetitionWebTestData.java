package org.innovateuk.ifs.testdata.data;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.testdata.builders.CompetitionLineBuilder;
import org.innovateuk.ifs.testdata.builders.CompetitionLineBuilder.BuilderOrder;
import org.innovateuk.ifs.testdata.builders.data.CompetitionLine;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.category.domain.InnovationArea.DIGITAL_MANUFACTORING_ID;
import static org.innovateuk.ifs.category.domain.InnovationArea.NONE;
import static org.innovateuk.ifs.category.domain.ResearchCategory.*;
import static org.innovateuk.ifs.competition.resource.CompetitionTypeEnum.*;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.*;
import static org.innovateuk.ifs.testdata.builders.CompetitionLineBuilder.aCompetitionLine;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

public class CompetitionWebTestData {

    private static Long LEAD_TECHNOLOGIST_ID = 24L;
    private static Long PETER_FREEMAN_ID = 25L;
    private static Long EXECUTIVE_ID = 20L;

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
        ).stream()
                .sorted(Comparator.comparing(l -> l.getBuilderOrder().ordinal()))
        .collect(Collectors.toList());

    }

    private static List<CompetitionLineBuilder> getProjectSetupCompetitionLineBuilders() {
        return asList(
                grantCompetition()
                        .withName("Connected digital additive manufacturing"),
                grantCompetition()
                        .withName("New designs for a circular economy"),
                grantCompetition()
                        .withName("Rolling stock future developments")
                        .withLeadTechnologist(PETER_FREEMAN_ID)
                        .withResearchRatio(100),
                grantCompetition()
                        .withName("Biosciences round three: plastic recovery in the industrial sector")
                        .withLeadTechnologist(PETER_FREEMAN_ID)
                        .withResearchRatio(50),
                grantCompetition()
                        .withName("Integrated delivery programme - solar vehicles")
                        .withHasAssessmentPanel(true)
                        .withHasInterviewStage(true),
                grantCompetition()
                        .withName("Enhanced energy saving competition")
                        .withLeadApplicantTypes(asSet(BUSINESS, RESEARCH, RTO, PUBLIC_SECTOR_OR_CHARITY)),
                grantCompetition()
                        .withName("Growth table comp"),
                grantCompetition()
                        .withName("No Growth table comp")
                        .withIncludeProjectGrowth(false),
                grantCompetition()
                        .withName("Project Setup Comp 1"),
                grantCompetition()
                        .withName("Project Setup Comp 2"),
                grantCompetition()
                        .withName("Project Setup Comp 3"),
                grantCompetition()
                        .withName("Project Setup Comp 4"),
                grantCompetition()
                        .withName("Project Setup Comp 5"),
                grantCompetition()
                        .withName("Project Setup Comp 6"),
                grantCompetition()
                        .withName("Project Setup Comp 7"),
                grantCompetition()
                        .withName("Project Setup Comp 8"),
                grantCompetition()
                        .withName("Project Setup Comp 9"),
                grantCompetition()
                        .withName("Project Setup Comp 10"),
                grantCompetition()
                        .withName("Project Setup Comp 11"),
                grantCompetition()
                        .withName("Project Setup Comp 12"),
                grantCompetition()
                        .withName("Project Setup Comp 13"),
                grantCompetition()
                        .withName("Project Setup Comp 14"),
                grantCompetition()
                        .withName("Project Setup Comp 15"),
                grantCompetition()
                        .withName("Project Setup Comp 16"),
                grantCompetition()
                        .withName("Project Setup Comp 17"),
                grantCompetition()
                        .withName("Project Setup Comp 18"),
                grantCompetition()
                        .withName("Project Setup Comp 19"),
                grantCompetition()
                        .withName("Project Setup Comp 20"),
                loanCompetition()
                        .withName("Project setup loan comp")
                        .withLeadTechnologist(PETER_FREEMAN_ID),
                grantCompetition()
                        .withName("583 Covid deminis round 1 project setup")
                        .withIncludeProjectGrowth(false)
                        .withIncludeYourOrganisation(false),
                grantCompetition()
                        .withName("Post award service competition"),
                investorPartnershipCompetition()
                        .withName("Investor partnership project setup"),
                grantCompetition()
                        .withName("Connect competition"),
                grantCompetition()
                        .withName("Auditor competition"),
                grantCompetition()
                        .withName("Innovation continuity loan competition"),
                procurementCompetition()
                        .withName("The Sustainable Innovation Fund: SBRI phase 1"),
                procurementCompetition()
                        .withName("SBRI competition"),
                ktpCompetition()
                        .withName("KTP Africa project setup"),
                grantCompetition()
                        .withName("Live project competition")
                        .withLeadApplicantTypes(asSet(BUSINESS, RESEARCH, RTO, PUBLIC_SECTOR_OR_CHARITY)),
                grantCompetition()
                        .withName("subsidy control comp in project setup")
                        .withFundingRules(FundingRules.SUBSIDY_CONTROL)
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.PROJECT_SETUP))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getInAssessmentCompetitionLineBuilders() {
        return asList(
                grantCompetition()
                        .withName("Sustainable living models for the future"),
                grantCompetition()
                        .withName("Assessment is awesome"),
                grantCompetition()
                        .withName("Assessment is awesome 2"),
                grantCompetition()
                        .withName("Assessments of load capabilities"),
                grantCompetition()
                        .withName("Expression of Interest: Assistive technologies for caregivers")
                        .withHasAssessmentPanel(true)
                        .withHasInterviewStage(true)
                        .withCompetitionType(EXPRESSION_OF_INTEREST),
                grantCompetition()
                        .withName("Living models for the future"),
                grantCompetition()
                        .withName("583 Covid deminis round 1")
                        .withIncludeProjectGrowth(false)
                        .withIncludeYourOrganisation(false),
                grantCompetition()
                        .withName("Multiple choice assessed"),
                ktpCompetition()
                        .withName("KTP assessment")
                        .withAssessorFinanceView(AssessorFinanceView.ALL)
                        .withIncludeProjectGrowth(false)
                        .withIncludeYourOrganisation(false),
                ktpCompetition()
                        .withName("KTP assessment Detailed Finances")
                        .withAssessorFinanceView(AssessorFinanceView.DETAILED),
                ktpCompetition()
                        .withName("KTP assessment Overview Finances"),
                ktpCompetition()
                        .withName("KTP cofunding"),
                grantCompetition()
                        .withName("Non KTP competition all finance overview")
                        .withAssessorFinanceView(AssessorFinanceView.ALL),
                ktpCompetition()
                        .withName("KTP cofunding single application"),
                grantCompetition()
                        .withName("Subsidy control comp in assessment")
                        .withFundingRules(FundingRules.SUBSIDY_CONTROL)
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT))
                .collect(Collectors.toList());
    }


    private static List<CompetitionLineBuilder> getFundersPanelCompetitionLineBuilders() {
        return asList(
                grantCompetition()
                        .withName("Internet of Things")
                        .withResearchRatio(100)
                        .withLeadTechnologist(PETER_FREEMAN_ID)
                        .withBuilderOrder(BuilderOrder.FIRST),
                ktpCompetition()
                        .withName("KTP in panel"),
                ktpCompetition()
                        .withName("NON-FEC KTP project competition"),
                ktpCompetition()
                        .withName("KTP notifications")
                        .withBuilderOrder(BuilderOrder.LAST),
                grantCompetition()
                        .withName("Living models for the future world")
                        .withHasInterviewStage(true)
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.FUNDERS_PANEL))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getReadyToOpenCompetitionLineBuilders() {
        return asList(
                grantCompetition()
                        .withName("Photonics for health")
                        .withLeadTechnologist(PETER_FREEMAN_ID),
                grantCompetition()
                        .withName("Comp to Delete 1")
                        .withLeadTechnologist(PETER_FREEMAN_ID),
                ofGemCompetition()
                        .withName("OfGem competition")
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.READY_TO_OPEN))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getAssessorFeedbackCompetitionLineBuilders() {
        return asList(
                grantCompetition()
                        .withName("Integrated delivery programme - low carbon vehicles")
                        .withHasInterviewStage(true)
                        .withHasAssessmentPanel(true)
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.ASSESSOR_FEEDBACK))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getClosedCompetitionLineBuilders() {
        return asList(
                grantCompetition()
                        .withName("Machine learning for transport infrastructure")
                        .withInnovationAreas(asSet(29L))
                        .withInnovationSector("Infrastructure systems")
                        .withAssessorCount(3),
                grantCompetition()
                        .withName("Personalised Smart HUDs for space helmets")
                        .withCompetitionType(AEROSPACE_TECHNOLOGY_INSTITUTE),
                grantCompetition()
                        .withName("Smart monitoring in high-pressure engineering systems")
                        .withCompetitionType(ADVANCED_PROPULSION_CENTRE)
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.CLOSED))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getOpenCompetitionLineBuilders() {
        return asList(
                grantCompetition()
                        .withName("Home and industrial efficiency programme")
                        .withResubmission(true)
                        .withIncludeYourOrganisation(false)
                        .withIncludeProjectGrowth(false)
                        .withResearchRatio(100)
                        .withAssessorCount(1)
                        .withLeadApplicantTypes(asSet(BUSINESS, RTO))
                        .withResearchCategory(asSet(FEASIBILITY_STUDIES_ID, INDUSTRIAL_RESEARCH_ID, EXPERIMENTAL_DEVELOPMENT_ID)),
                grantCompetition()
                        .withName("Predicting market trends programme")
                        .withResearchCategory(asSet(FEASIBILITY_STUDIES_ID, INDUSTRIAL_RESEARCH_ID, EXPERIMENTAL_DEVELOPMENT_ID))
                        .withLeadApplicantTypes(asSet(RTO))
                        .withResearchRatio(50)
                        .withResubmission(true)
                        .withIncludeProjectGrowth(false)
                        .withIncludeYourOrganisation(false)
                        .withAssessorCount(3),
                grantCompetition()
                        .withCompetitionType(SECTOR)
                        .withName("Aerospace technology investment sector")
                        .withResearchCategory(asSet(FEASIBILITY_STUDIES_ID, INDUSTRIAL_RESEARCH_ID, EXPERIMENTAL_DEVELOPMENT_ID))
                        .withResubmission(true)
                        .withInnovationAreas(asSet(22L, 23L)),
                grantCompetition()
                        .withCompetitionType(GENERIC)
                        .withName("Generic innovation")
                        .withLeadTechnologist(PETER_FREEMAN_ID),
                grantCompetition()
                        .withName("Photonics for Research")
                        .withLeadApplicantTypes(asSet(RESEARCH))
                        .withLeadTechnologist(PETER_FREEMAN_ID),
                grantCompetition()
                        .withName("Photonics for Public")
                        .withIncludeProjectGrowth(false)
                        .withResearchCategory(asSet(FEASIBILITY_STUDIES_ID, INDUSTRIAL_RESEARCH_ID, EXPERIMENTAL_DEVELOPMENT_ID))
                        .withLeadTechnologist(PETER_FREEMAN_ID)
                        .withLeadApplicantTypes(asSet(PUBLIC_SECTOR_OR_CHARITY))
                        .withResearchRatio(50),
                grantCompetition()
                        .withName("Photonics for RTO and Business"),
                grantCompetition()
                        .withName("Photonics for All")
                        .withLeadTechnologist(PETER_FREEMAN_ID),
                grantCompetition()
                        .withName("Expression of Interest: Quantum Computing algorithms for combating antibiotic resistance through simulation")
                        .withCompetitionType(EXPRESSION_OF_INTEREST),
                grantCompetition()
                        .withName("Low-cost propulsion mechanisms for subsonic travel")
                        .withCompetitionType(ADVANCED_PROPULSION_CENTRE),
                grantCompetition()
                        .withName("Reusability of waste material rocketry components")
                        .withCompetitionType(AEROSPACE_TECHNOLOGY_INSTITUTE),
                grantCompetition()
                        .withName("Mathematics: the analytical theory of numbers")
                        .withCompetitionType(AEROSPACE_TECHNOLOGY_INSTITUTE),
                investorPartnershipCompetition()
                        .withName("Investor")
                        .withCompetitionType(AEROSPACE_TECHNOLOGY_INSTITUTE),
                grantCompetition()
                        .withName("Performance testing competition")
                        .withLeadApplicantTypes(asSet(BUSINESS, RTO, RESEARCH, PUBLIC_SECTOR_OR_CHARITY)),
                procurementCompetition()
                        .withName("Procurement Competition"),
                procurementCompetition()
                        .withName("Procurement Competition"),
                loanCompetition()
                        .withName("Loan Competition"),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -1")
                        .withAssessorCount(5),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -2")
                        .withAssessorCount(5),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -3")
                        .withAssessorCount(5),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -4")
                        .withAssessorCount(5),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -5")
                        .withAssessorCount(5),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -6")
                        .withAssessorCount(5),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -7")
                        .withAssessorCount(5),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -8")
                        .withAssessorCount(3),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -9")
                        .withAssessorCount(5)
                        .withCompetitionCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -10")
                        .withAssessorCount(5)
                        .withCompetitionCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -11")
                        .withAssessorCount(5)
                        .withCompetitionCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -12")
                        .withAssessorCount(5)
                        .withCompetitionCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp -13")
                        .withAssessorCount(3),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp without Project Setup - 1")
                        .withAssessorCount(5)
                        .withCompetitionCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp without Project Setup - 2")
                        .withAssessorCount(5)
                        .withCompetitionCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp without Project Setup - 3")
                        .withAssessorCount(5)
                        .withCompetitionCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK),
                loanCompetition()
                        .withName("SF Loans Integration Testing Comp without Project Setup - 4")
                        .withAssessorCount(5)
                        .withCompetitionCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE),
                grantCompetition()
                        .withName("H2020 Performance testing competition")
                        .withCompetitionType(HORIZON_2020)
                        .withInnovationAreas(null),
                grantCompetition()
                        .withName("International Competition"),
                grantCompetition()
                        .withName("596 Covid grants framework group")
                        .withIncludeProjectGrowth(false)
                        .withIncludeYourOrganisation(false)
                        .withResubmission(true),
                grantCompetition()
                        .withName("599 Covid de minimis round 2")
                        .withResearchCategory(asSet(FEASIBILITY_STUDIES_ID, INDUSTRIAL_RESEARCH_ID, EXPERIMENTAL_DEVELOPMENT_ID))
                        .withResubmission(true)
                        .withIncludeProjectGrowth(false)
                        .withIncludeYourOrganisation(false),
                grantCompetition()
                        .withName("Multiple choice open"),
                procurementCompetition()
                        .withName("SBRI type one competition")
                        .withCompetitionCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE),
                ktpCompetition()
                        .withName("KTP new competition"),
                loanCompetition()
                        .withName("Competition not submitted before the deadline")
                        .withInnovationSector("Infrastructure systems")
                        .withAssessorCount(5),
                loanCompetition()
                        .withName("Competition for application submitted before competition closing time"),
                grantCompetition()
                        .withName("Innovation continuity loan"),
                ktpCompetition()
                        .withName("KTP Africa Comp")
                        .withCompetitionType(AEROSPACE_TECHNOLOGY_INSTITUTE),
                ktpCompetition()
                        .withName("No aid comp"),
                grantCompetition()
                        .withName("Subsidy control competition")
                        .withResubmission(true)
                        .withFundingRules(FundingRules.SUBSIDY_CONTROL),
                grantCompetition()
                        .withName("Princes Trust Comp 1")
                        .withCompetitionType(THE_PRINCES_TRUST)
                        .withLeadApplicantTypes(asSet(BUSINESS, RTO, RESEARCH, PUBLIC_SECTOR_OR_CHARITY))
                        .withFundingRules(FundingRules.SUBSIDY_CONTROL),
                grantCompetition()
                        .withName("Always open competition")
                        .withAlwaysOpen(true)
                        .withResubmission(true),
                ktpCompetition()
                        .withName("Always open ktp competition")
                        .withAlwaysOpen(true)
                        .withResubmission(true),
                ktpCompetition()
                        .withName("FEC KTP competition"),
                grantCompetition()
                        .withName("Improved organisation search performance competition"),
                ktpCompetition()
                        .withName("KTP dashboard competition"),
                grantCompetition()
                        .withName("Subsidy control t and c competition")
                        .withFundingRules(FundingRules.SUBSIDY_CONTROL),
               grantCompetition()
                        .withName("Subsidy control tactical competition")
                        .withFundingRules(FundingRules.SUBSIDY_CONTROL),
                ktpCompetition()
                        .withName("KTP new competition duplicate"),
                ktpCompetition()
                        .withName("FEC KTP project competition"),
                ktpCompetition()
                        .withName("FEC KTP competition duplicate"),
                loanCompetition()
                        .withName("Loans SF Part-B Competition")
                        .withInnovationSector("Infrastructure systems")
                        .withAssessorCount(5),
                grantCompetition()
                        .withName("Horizon Europe Guarantee competition")
                        .withCompetitionType(HORIZON_EUROPE_GUARANTEE)
                        .withAlwaysOpen(true)
                        .withResearchCategory(asSet(FEASIBILITY_STUDIES_ID, INDUSTRIAL_RESEARCH_ID, EXPERIMENTAL_DEVELOPMENT_ID))
                        .withResubmission(false)
                        .withIncludeProjectGrowth(false)
                        .withIncludeYourOrganisation(false)
        )
                .stream()
                .map(competitionLineBuilder -> competitionLineBuilder.withCompetitionStatus(CompetitionStatus.OPEN))
                .collect(Collectors.toList());
    }

    private static List<CompetitionLineBuilder> getNonIfsLineBuilders() {
        return asList(nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 1"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 2"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 3"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 4"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 5"),
                nonIfsCompetition()
                        .withName("Non IFS Comp 6"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 7"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 8"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 9"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 10"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 11"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 12"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 13"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 14"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 15"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 16"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 17"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 18"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 19"),
                nonIfsCompetition()
                        .withName("Webtest Non IFS Comp 20"),
                nonIfsCompetition()
                        .withName("Transforming big data")
                        .withFundingType(FundingType.GRANT)
                        .withInnovationAreas(asSet(5L)),
                nonIfsCompetition()
                        .withName("Reducing carbon footprints")
                        .withInnovationAreas(asSet(21L)));
    }

    private static CompetitionLineBuilder grantCompetition() {
        return anIfsCompetition()
                .withFundingType(FundingType.GRANT);
    }

    private static CompetitionLineBuilder procurementCompetition() {
        return anIfsCompetition()
                .withFundingType(FundingType.PROCUREMENT)
                .withInnovationSector("None")
                .withInnovationAreas(asSet(67L))
                .withIncludeYourOrganisation(false)
                .withResearchRatio(100)
                .withTermsAndConditionsTemplate("procurement-terms-and-conditions");
    }

    private static CompetitionLineBuilder ofGemCompetition() {
        return thirdPartyCompetition()
                .withCompetitionCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK)
                .withTermsAndConditionsLabel("Strategic Innovation Fund Governance Document")
                .withTermsAndConditionsGuidance("<h2 class=\"govuk-heading-m\">Summary of Strategic Innovation Fund governance document.</h2>\n" +
                        "            <p class=\"govuk-body\">\n" +
                        "                <a href=\"https://www.ofgem.gov.uk/sites/default/files/2021-06/draft_sif_governance_document_consultation_copy_020621_0.pdf\">The SIF governance document</a> is specific to the Ofgem Strategic Innovation Fund (SIF). Applicants wishing to collaborate on projects funded through the SIF must comply with this SIF Governance Document.  It is your responsibility to make sure you have read it.\n" +
                        "            </p>\n" +
                        "            <p class=\"govuk-body\">\n" +
                        "                The SIF governance document represents an agreement between the Ofgem and your organisation and include details about your obligations and the administration of your project.\n" +
                        "            </p>\n" +
                        "            <p class=\"govuk-body\">\n" +
                        "                They cover:\n" +
                        "            </p>\n" +
                        "            <ul class=\"govuk-list govuk-list--bullet\">\n" +
                        "                <li>eligibility criteria</li>\n" +
                        "                <li>application process</li>\n" +
                        "                <li>assessment of projects</li>\n" +
                        "                <li>project delivery and monitoring</li>\n" +
                        "                <li>funding arrangements</li>\n" +
                        "                <li>project administration, including compliance and audits</li>\n" +
                        "                <li>intellectual property rights and royalties</li>\n" +
                        "                <li>learning and collaboration, including external stakeholder relationship development, information sharing and reporting</li>\n" +
                        "            </ul>\n" +
                        "            <p class=\"govuk-body\">This list is not exhaustive.</p>\n" +
                        "            <p class=\"govuk-body\">\n" +
                        "                If your application is successful, Ofgem will issue a direction to the network licensee leading the project, which confirms the deliverables for which they are accountable.\n" +
                        "            </p>\n" +
                        "            <p class=\"govuk-body\">\n" +
                        "                By ticking the box and submitting an application, you are confirming that you have read and are satisfied that you understand the regulatory framework.\n" +
                        "            </p>\n" +
                        "            <p class=\"govuk-body\">If there is any aspect of the SIF you do not understand, please <a href=\"/info/contact\">contact us</a>.</p>\n")
                .withProjectCostGuidanceUrl("https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance/small-business-research-initiative-sbri-project-costs-guidance")
                .withTermsAndConditionsTemplate("third-party-terms-and-conditions");
    }

    private static CompetitionLineBuilder thirdPartyCompetition() {
        return anIfsCompetition()
                .withFundingType(FundingType.PROCUREMENT)
                .withFundingRules(FundingRules.NOT_AID)
                .withInnovationSector("Infrastructure systems")
                .withInnovationAreas(asSet(NONE))
                .withIncludeYourOrganisation(false)
                .withResearchRatio(100);
    }

    private static CompetitionLineBuilder investorPartnershipCompetition() {
        return anIfsCompetition()
                .withFundingType(FundingType.INVESTOR_PARTNERSHIPS)
                .withResearchRatio(100)
                .withResubmission(true);
    }

    private static CompetitionLineBuilder loanCompetition() {
        return anIfsCompetition()
                .withFundingType(FundingType.LOAN)
                .withCollaborationLevel(CollaborationLevel.SINGLE)
                .withResubmission(true);
    }

    private static CompetitionLineBuilder ktpCompetition() {
        return anIfsCompetition()
                .withFundingType(FundingType.KTP)
                .withResubmission(true);
    }

    private static CompetitionLineBuilder anIfsCompetition() {
        return aCompetitionLine()
                .withCompetitionType(PROGRAMME)
                .withInnovationAreas(asSet(DIGITAL_MANUFACTORING_ID))
                .withInnovationSector("Materials and manufacturing")
                .withResearchCategory(asSet(FEASIBILITY_STUDIES_ID))
                .withCollaborationLevel(CollaborationLevel.SINGLE_OR_COLLABORATIVE)
                .withLeadApplicantTypes(asSet(BUSINESS))
                .withAssessorCount(5)
                .withResearchRatio(30)
                .withResubmission(false)
                .withMultiStream(false)
                .withHasAssessmentPanel(false)
                .withHasInterviewStage(false)
                .withLeadTechnologist(LEAD_TECHNOLOGIST_ID)
                .withCompExecutive(EXECUTIVE_ID)
                .withSetupComplete(true)
                .withPafCode("875")
                .withBudgetCode("DET1536/1537")
                .withActivityCode("16014")
                .withCode("2/1/1506")
                .withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
                .withNonIfs(false)
                .withCompetitionCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .withIncludeJesForm(true)
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD)
                .withIncludeProjectGrowth(true)
                .withIncludeYourOrganisation(true)
                .withFundingRules(FundingRules.STATE_AID)
                .withPublished(true)
                .withAlwaysOpen(false);
    }

    private static CompetitionLineBuilder nonIfsCompetition() {
        return aCompetitionLine()
                .withInnovationAreas(asSet(5L)) // Digital industries
                .withInnovationSector("Emerging and enabling")
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
                .withFundingType(FundingType.GRANT)
                .withPublished(true)
                .withNonIfs(true)
                .withNonIfsUrl("https://www.gov.uk/government/organisations/innovate-uk")
                .withCompetitionCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD);
    }

}
