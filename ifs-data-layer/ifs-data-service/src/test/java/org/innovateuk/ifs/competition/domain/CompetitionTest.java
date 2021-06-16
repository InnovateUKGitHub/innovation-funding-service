package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.Funder;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.domain.Section;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.competition.resource.Funder.ADVANCED_PROPULSION_CENTRE_APC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompetitionTest {
    private Competition competition;
    private Long id;
    private List<Section> sections;
    private List<AssessmentPeriod> assessmentPeriods;
    private String name;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime registrationDate;
    private Integer maxResearchRatio;
    private Integer academicGrantPercentage;

    private String budgetCode;

    private String activityCode;
    private BigDecimal funderBudget;
    private Funder funder;

    @Before
    public void setUp() throws Exception {

        name = "testCompetitionName";
        startDate = ZonedDateTime.now().minusDays(5);
        registrationDate = startDate.plusDays(4);
        endDate = startDate.plusDays(15);

        maxResearchRatio = 10;
        academicGrantPercentage = 30;

        budgetCode = "BudgetCode";
        activityCode = "ActivityCode";
        funder = ADVANCED_PROPULSION_CENTRE_APC;
        funderBudget = new BigDecimal(0);

        sections = new ArrayList<>();
        sections.add(new Section());
        sections.add(new Section());
        sections.add(new Section());

        GrantTermsAndConditions termsAndConditions = new GrantTermsAndConditions();
        termsAndConditions.setId(1L);

        CompetitionType competitionType = newCompetitionType()
                .withName("Sector")
                .build();

        competition = new Competition(emptyList(), sections, name, startDate, endDate, registrationDate, termsAndConditions);
        competition.setMaxResearchRatio(maxResearchRatio);
        competition.setAcademicGrantPercentage(academicGrantPercentage);

        assessmentPeriods = new ArrayList<>();
        assessmentPeriods.add(new AssessmentPeriod());
        competition.setId(id);
        competition.setBudgetCode(budgetCode);
        competition.setActivityCode(activityCode);
        competition.setFunders(getTestFunders(competition));
        competition.setCompetitionType(competitionType);
        competition.setAssessmentPeriods(assessmentPeriods);
    }


    private List<CompetitionFunder> getTestFunders(Competition competition) {
        List<CompetitionFunder> returnList = new ArrayList<>();
        CompetitionFunder Funder1 = new CompetitionFunder();
        Funder1.setId(1L);
        Funder1.setCompetition(competition);
        Funder1.setFunder(funder);
        Funder1.setFunderBudget(BigInteger.valueOf(1));
        Funder1.setCoFunder(false);
        returnList.add(Funder1);


        CompetitionFunder coFunder1 = new CompetitionFunder();
        coFunder1.setId(1L);
        coFunder1.setCompetition(competition);
        coFunder1.setFunder(funder);
        coFunder1.setFunderBudget(BigInteger.valueOf(1));
        coFunder1.setCoFunder(true);
        returnList.add(coFunder1);

        CompetitionFunder coFunder2 = new CompetitionFunder();
        coFunder2.setId(2L);
        coFunder2.setCompetition(competition);
        coFunder2.setFunder(funder);
        coFunder2.setFunderBudget(BigInteger.valueOf(2));
        coFunder1.setCoFunder(true);
        returnList.add(coFunder2);

        return returnList;
    }

    @Test
    public void competitionShouldReturnCorrectAttributeValues() {
        assertEquals(competition.getId(), id);
        assertEquals(competition.getName(), name);
        assertEquals(competition.getSections(), sections);
        assertEquals(competition.getMaxResearchRatio(), maxResearchRatio);
        assertEquals(competition.getAcademicGrantPercentage(), academicGrantPercentage);
        assertEquals(competition.getRegistrationDate(), registrationDate.truncatedTo(SECONDS));

        assertEquals(competition.getBudgetCode(), budgetCode);
        assertEquals(competition.getActivityCode(), activityCode);
        assertEquals(competition.getFunders().size(), getTestFunders(competition).size());
    }

    @Test
    public void competitionStatusOpen() {
        assertEquals(OPEN, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusReadyToOpen() {
        competition.setStartDate(ZonedDateTime.now().plusDays(1));
        assertEquals(READY_TO_OPEN, competition.getCompetitionStatus());
    }

    @Test
    public void competitionClosingSoon() {
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setCompetitionStatus(OPEN);
        competitionResource.setStartDate(ZonedDateTime.now().minusDays(4));
        competitionResource.setEndDate(ZonedDateTime.now().plusHours(1));
        assertTrue(competitionResource.isClosingSoon());
    }

    @Test
    public void competitionNotClosingSoon() {
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setCompetitionStatus(OPEN);
        competitionResource.setStartDate(ZonedDateTime.now().minusDays(4));
        competitionResource.setEndDate(ZonedDateTime.now().plusHours(3).plusMinutes(1));
        assertFalse(competitionResource.isClosingSoon());
    }

    @Test
    public void competitionStatusInAssessment() {
        competition.setEndDate(ZonedDateTime.now().minusDays(1));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(1), assessmentPeriods.get(0));
        competition.setFundersPanelDate(ZonedDateTime.now().plusDays(1));
        assertEquals(IN_ASSESSMENT, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusInPrevious() {
        competition.setCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE);
        competition.setEndDate(ZonedDateTime.now().minusDays(1));
        assertEquals(PREVIOUS, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusInAssessment_notCompetitionClosed() {
        competition.setEndDate(ZonedDateTime.now().minusDays(3));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(2), assessmentPeriods.get(0));
        competition.closeAssessment(ZonedDateTime.now().minusDays(1), assessmentPeriods.get(0));

        try {
            competition.notifyAssessors(ZonedDateTime.now(), assessmentPeriods.get(0));
        } catch (IllegalStateException e) {
            assertEquals("Tried to notify assessors when assessment is closed", e.getMessage());
        }
    }

    @Test
    public void competitionStatusInAssessment_alreadyInAssessment() {
        competition.setEndDate(ZonedDateTime.now().minusDays(3));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(2), assessmentPeriods.get(0));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(1), assessmentPeriods.get(0));
        assertEquals(IN_ASSESSMENT, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusFundersPanelAsFundersPanelEndDateAbsent() {
        competition.setEndDate(ZonedDateTime.now().minusDays(5));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(4), assessmentPeriods.get(0));
        competition.closeAssessment(ZonedDateTime.now().minusDays(3), assessmentPeriods.get(0));
        competition.setFundersPanelDate(ZonedDateTime.now().minusDays(2));
        assertEquals(FUNDERS_PANEL, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusFundersPanelAsFundersPanelEndDatePresentButInFuture() {
        competition.setEndDate(ZonedDateTime.now().minusDays(6));
        competition.setAssessorAcceptsDate(ZonedDateTime.now().minusDays(5));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(4), assessmentPeriods.get(0));
        competition.closeAssessment(ZonedDateTime.now().minusDays(3), assessmentPeriods.get(0));
        competition.setFundersPanelDate(ZonedDateTime.now().minusDays(2));
        competition.setFundersPanelEndDate(ZonedDateTime.now().plusDays(1));
        assertEquals(FUNDERS_PANEL, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusAssessorFeedback() {
        competition.setEndDate(ZonedDateTime.now().minusDays(6));
        competition.setAssessorAcceptsDate(ZonedDateTime.now().minusDays(5));
        competition.notifyAssessors(ZonedDateTime.now().minusDays(4), assessmentPeriods.get(0));
        competition.closeAssessment(ZonedDateTime.now().minusDays(3), assessmentPeriods.get(0));
        competition.setFundersPanelDate(ZonedDateTime.now().minusDays(2));
        competition.setFundersPanelEndDate(ZonedDateTime.now().minusDays(1));
        assertEquals(ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }

    @Test
    public void getFinanceRowTypesByFinanceForFecCostModel() {
        List<CompetitionFinanceRowTypes> competitionFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .map(financeRowType -> new CompetitionFinanceRowTypes(competition, financeRowType, 0))
                .collect(Collectors.toList());

        ReflectionTestUtils.setField(competition, "competitionFinanceRowTypes", competitionFinanceRowTypes);
        competition.setFundingType(FundingType.KTP);

        ApplicationFinance applicationFinance = new ApplicationFinance();
        applicationFinance.setFecModelEnabled(true);

        List<FinanceRowType> financeRowTypes = competition.getFinanceRowTypesByFinance(applicationFinance);
        assertEquals(Arrays.asList(FinanceRowType.OTHER_COSTS,
                FinanceRowType.FINANCE,
                FinanceRowType.ASSOCIATE_SALARY_COSTS,
                FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS,
                FinanceRowType.CONSUMABLES,
                FinanceRowType.ASSOCIATE_SUPPORT,
                FinanceRowType.KNOWLEDGE_BASE,
                FinanceRowType.ESTATE_COSTS,
                FinanceRowType.KTP_TRAVEL,
                FinanceRowType.ADDITIONAL_COMPANY_COSTS,
                FinanceRowType.PREVIOUS_FUNDING), financeRowTypes);
    }

    @Test
    public void getFinanceRowTypesByFinanceForNonFecCostModel() {
        List<CompetitionFinanceRowTypes> competitionFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .map(financeRowType -> new CompetitionFinanceRowTypes(competition, financeRowType, 0))
                .collect(Collectors.toList());

        ReflectionTestUtils.setField(competition, "competitionFinanceRowTypes", competitionFinanceRowTypes);
        competition.setFundingType(FundingType.KTP);

        ApplicationFinance applicationFinance = new ApplicationFinance();
        applicationFinance.setFecModelEnabled(false);

        List<FinanceRowType> financeRowTypes = competition.getFinanceRowTypesByFinance(applicationFinance);
        assertEquals(Arrays.asList(FinanceRowType.OTHER_COSTS,
                FinanceRowType.FINANCE,
                FinanceRowType.ASSOCIATE_SALARY_COSTS,
                FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS,
                FinanceRowType.CONSUMABLES,
                FinanceRowType.KTP_TRAVEL,
                FinanceRowType.ADDITIONAL_COMPANY_COSTS,
                FinanceRowType.PREVIOUS_FUNDING,
                FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT,
                FinanceRowType.INDIRECT_COSTS), financeRowTypes);
    }
}
