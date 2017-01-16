package org.innovateuk.ifs.application.transactional.sectionupdater;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.application.transactional.SectionService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class ApplicationFinanceOrganisationUpdaterTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ApplicationFinanceOrganisationUpdater updater;

    @Mock
    private SectionService sectionService;

    @Mock
    private QuestionService questionService;

    @Mock
    private FinanceRowService financeRowService;

    private Application currentApplication;
    private Section currentSection;
    private Long processRoleId;
    private List<SectionResource> fundingFinSection;
    private Long appFinanceId;
    private Long appFinanceRowId;
    private Long orgId;


    @Before
    public void setup() {
        Competition competition = newCompetition().withId(124125L).build();
        currentApplication = newApplication().withCompetition(competition).build();
        currentSection = newSection().withCompetitionAndPriority(competition, 0).build();
        processRoleId = 24125L;
        appFinanceId = 12412L;
        appFinanceRowId = 923592L;
        orgId = 72727L;

        fundingFinSection = newSectionResource().withId(865L).withType(SectionType.FUNDING_FINANCES).build(1);
        List<QuestionResource> finSectionQuestions = newQuestionResource().withId(231L).withFormInputs(asList(5L,6L)).withCompetition(currentApplication.getCompetition().getId()).build(1);
        List<ApplicationFinanceRow> financeRows = newApplicationFinanceRow().with(applicationFinanceRow -> applicationFinanceRow.setId(appFinanceRowId)).build(1);

        when(questionService.getQuestionsBySectionIdAndType(fundingFinSection.get(0).getId(), QuestionType.GENERAL)).thenReturn(serviceSuccess(finSectionQuestions));
        when(sectionService.getSectionsByCompetitionIdAndType(currentApplication.getCompetition().getId(), SectionType.FUNDING_FINANCES)).thenReturn(serviceSuccess(fundingFinSection));
        when(financeRowService.getCosts(appFinanceId, "grant-claim", finSectionQuestions.get(0).getId())).thenReturn(serviceSuccess(financeRows));
        when(sectionService.getCompletedSections(currentApplication.getId(), orgId)).thenReturn(serviceSuccess(new HashSet<>(asList(fundingFinSection.get(0).getId()))));
    }

    @Test
    public void getRelatedSectionTest() {
        assertEquals(SectionType.ORGANISATION_FINANCES, updater.getRelatedSection());
    }

    @Test
    public void handleMarkAsCompleteTestValidFundingNotComplete() {
        when(sectionService.getCompletedSections(currentApplication.getId(), orgId)).thenReturn(serviceSuccess(new HashSet<>()));

        updater.handleMarkAsComplete(currentApplication, currentSection, processRoleId);

        setApplicationFinance();

        updater.handleMarkAsComplete(currentApplication, currentSection, processRoleId);

        verify(sectionService, never()).markSectionAsInComplete(fundingFinSection.get(0).getId(), currentApplication.getId(), processRoleId);
        verify(financeRowService, never()).deleteCost(appFinanceRowId);
    }

    @Test
    public void handleMarkAsCompleteTestValidWithApplicationFinance() {
        setApplicationFinance();

        updater.handleMarkAsComplete(currentApplication, currentSection, processRoleId);

        verify(sectionService).markSectionAsInComplete(fundingFinSection.get(0).getId(), currentApplication.getId(), processRoleId);
        verify(financeRowService).deleteCost(appFinanceRowId);
    }

    @Test
    public void handleMarkAsCompleteTestInvalid() {
        when(questionService.getQuestionsBySectionIdAndType(anyLong(), any(QuestionType.class))).thenReturn(serviceFailure(new Error(GENERAL_NOT_FOUND)));
        when(sectionService.getSectionsByCompetitionIdAndType(anyLong(), any(SectionType.class))).thenReturn(serviceFailure(new Error(GENERAL_NOT_FOUND)));
        when(financeRowService.getCosts(anyLong(), anyString(), anyLong())).thenReturn(serviceFailure(new Error(GENERAL_NOT_FOUND)));
        when(sectionService.getCompletedSections(anyLong(), anyLong())).thenReturn(serviceFailure(new Error(GENERAL_NOT_FOUND)));

        updater.handleMarkAsComplete(currentApplication, currentSection, processRoleId);

        verify(sectionService, never()).markSectionAsInComplete(fundingFinSection.get(0).getId(), currentApplication.getId(), processRoleId);
        verify(financeRowService, never()).deleteCost(appFinanceRowId);
    }

    private void setApplicationFinance() {
        currentApplication.setApplicationFinances(newApplicationFinance().with(applicationFinance -> {
            applicationFinance.setId(appFinanceId);
            applicationFinance.setApplication(currentApplication);
            applicationFinance.setOrganisation(
                    newOrganisation().with(organisation -> {
                        organisation.setId(orgId);
                        organisation.setProcessRoles(
                                newProcessRole().withId(processRoleId).build(1));
                    }).build());
        }).build(1));
    }
}
