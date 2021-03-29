package org.innovateuk.ifs.application.forms.questions.subsidybasis;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.common.populator.ApplicationSubsidyBasisModelPopulator;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisViewModel;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toCollection;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSubsidyBasisModelPopulatorTest extends BaseUnitTest {

    @Mock
    private ProcessRoleRestService processRoleRestService;
    @Mock
    private QuestionStatusRestService questionStatusRestService;
    @Mock
    private OrganisationService organisationService;
    @Mock
    private FinanceService financeService;
    @InjectMocks
    private ApplicationSubsidyBasisModelPopulator populator;

    @Test
    public void populate() {
        // Setup
        ApplicationResource application = newApplicationResource().build();
        OrganisationResource leadOrganisation = newOrganisationResource().withName("lead organisation").build(); // Lead
        OrganisationResource collaboratorOrganisation = newOrganisationResource().withName("collaborator organisation").build();
        ApplicationFinanceResource leadApplicationFinance = newApplicationFinanceResource().withNorthernIrelandDeclaration(true).build();
        ApplicationFinanceResource collaboratorApplicationFinance = newApplicationFinanceResource().withNorthernIrelandDeclaration(false).build();
        ProcessRoleResource leadProcessRole = newProcessRoleResource().withRole(ProcessRoleType.LEADAPPLICANT).withOrganisation(leadOrganisation.getId()).withApplication(application.getId()).build();
        ProcessRoleResource collaboratorProcessRole = newProcessRoleResource().withRole(ProcessRoleType.COLLABORATOR).withOrganisation(collaboratorOrganisation.getId()).withApplication(application.getId()).build();
        QuestionResource subsidyBasisQuestion = newQuestionResource().withQuestionSetupType(QuestionSetupType.SUBSIDY_BASIS).build();
        QuestionStatusResource subsidyBasisQuestionStatusOrganisationOne = newQuestionStatusResource().withMarkedAsComplete(true).withMarkedAsCompleteByOrganisationId(leadOrganisation.getId()).build();
        QuestionStatusResource subsidyBasisQuestionStatusOrganisationTwo = newQuestionStatusResource().withMarkedAsComplete(true).withMarkedAsCompleteByOrganisationId(collaboratorOrganisation.getId()).build();

        // Setup
        when(questionStatusRestService.findQuestionStatusesByQuestionAndApplicationId(subsidyBasisQuestion.getId(), application.getId()))
                .thenReturn(restSuccess(asList(subsidyBasisQuestionStatusOrganisationOne, subsidyBasisQuestionStatusOrganisationTwo)));
        when(processRoleRestService.findProcessRole(application.getId()))
                .thenReturn(restSuccess(singletonList(leadProcessRole)));
        when(processRoleRestService.findProcessRole(application.getId()))
                .thenReturn(restSuccess(asList(leadProcessRole, collaboratorProcessRole)));
        when(organisationService.getApplicationOrganisations(asList(leadProcessRole, collaboratorProcessRole)))
                .thenReturn(sortedSet(leadOrganisation, collaboratorOrganisation));
        when(financeService.getApplicationFinanceByApplicationIdAndOrganisationId(application.getId(), leadOrganisation.getId()))
                .thenReturn(leadApplicationFinance);
        when(financeService.getApplicationFinanceByApplicationIdAndOrganisationId(application.getId(), collaboratorOrganisation.getId()))
                .thenReturn(collaboratorApplicationFinance);

        // Method under test
        ApplicationSubsidyBasisViewModel model = populator.populate(subsidyBasisQuestion, application.getId());

        // Assertions
        assertTrue(model.isSubsidyBasisCompletedByAllOrganisations());

        assertFalse(model.getPartners().isEmpty());
        assertEquals(2, model.getPartners().size());

        assertEquals("lead organisation", model.getPartners().get(0).getName());
        assertTrue(model.getPartners().get(0).isNorthernIslandDeclaration());
        assertTrue(model.getPartners().get(0).isQuestionnaireMarkedAsComplete());
        assertTrue(model.getPartners().get(0).isLead());

        assertEquals("collaborator organisation", model.getPartners().get(1).getName());
        assertFalse(model.getPartners().get(1).isNorthernIslandDeclaration());
        assertTrue(model.getPartners().get(1).isQuestionnaireMarkedAsComplete());
        assertFalse(model.getPartners().get(1).isLead());
    }

    private <E> SortedSet<E> sortedSet(E... items){
        List<E> list = asList(items);
        return asList(items).stream().collect(toCollection(() -> new TreeSet<>(comparingInt(o -> list.indexOf(o)))));
    }

}