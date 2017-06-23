package org.innovateuk.ifs.application.populator.section;


import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.YourFinancesSectionViewModel;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link YourFinancesSectionPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class YourFinancesSectionPopulatorTest {

    @InjectMocks
    private YourFinancesSectionPopulator yourFinancesSectionPopulator;

    @Mock
    private SectionService sectionService;

    @Mock
    private QuestionService questionService;

    @Mock
    private FinanceService financeService;

    @Mock
    private FileEntryRestService fileEntryRestService;

    @Mock
    private ApplicationNavigationPopulator navigationPopulator;

    @Test
    public void testPopulate() {
        ApplicantSectionResource yourProjectCosts = newApplicantSectionResource().withSection(newSectionResource().withType(SectionType.PROJECT_COST_FINANCES).build()).build();
        ApplicantSectionResource yourOrganisation = newApplicantSectionResource().withSection(newSectionResource().withType(SectionType.ORGANISATION_FINANCES).build()).build();
        ApplicantSectionResource yourFunding = newApplicantSectionResource().withSection(newSectionResource().withType(SectionType.FUNDING_FINANCES).build()).build();
        QuestionResource applicationDetails = newQuestionResource().build();
        ApplicantSectionResource section = newApplicantSectionResource()
                .withCurrentApplicant(newApplicantResource().withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build()).build())
                .withCurrentUser(newUserResource().build())
                .withCompetition(newCompetitionResource().build())
                .withApplication(newApplicationResource().build())
                .withApplicantChildrenSections(asList(yourProjectCosts, yourOrganisation, yourFunding))
                .build();
        ApplicationForm form = mock(ApplicationForm.class);
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        ApplicationFinanceResource financeResource = newApplicationFinanceResource()
                .withOrganisation(section.getCurrentApplicant().getOrganisation().getId())
                .withGrantClaimPercentage(0)
                .build();

        when(questionService.getQuestionByCompetitionIdAndFormInputType(section.getCompetition().getId(), FormInputType.APPLICATION_DETAILS)).thenReturn(serviceSuccess(applicationDetails));
        when(sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(asList(yourFunding.getSection().getId(), yourOrganisation.getSection().getId(), yourProjectCosts.getSection().getId()));
        when(questionService.getQuestionStatusesForApplicationAndOrganisation(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(asMap(applicationDetails.getId(), newQuestionStatusResource().withMarkedAsComplete(true).build()));
        when(financeService.getApplicationFinanceDetails(section.getCurrentUser().getId(), section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(financeResource);
        when(financeService.getApplicationFinanceTotals(section.getApplication().getId())).thenReturn(asList(financeResource));

        YourFinancesSectionViewModel viewModel = yourFinancesSectionPopulator.populate(section, form, model, bindingResult, false, Optional.empty(), false);

        assertThat(viewModel.isSection(), equalTo(true));
        assertThat(viewModel.isNotRequestingFunding(), equalTo(true));
        assertThat(viewModel.isFundingSectionLocked(), equalTo(false));
        assertThat(viewModel.getApplicationDetailsQuestionId(), equalTo(applicationDetails.getId()));
        assertThat(viewModel.getOrganisationFinance(), equalTo(financeResource));
        assertThat(viewModel.getYourOrganisationSectionId(), equalTo(yourOrganisation.getSection().getId()));

    }
}
