package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.YourFundingSectionPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.application.viewmodel.section.YourFundingSectionViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
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
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link YourFundingSectionPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class YourFundingSectionPopulatorTest {

    @InjectMocks
    private YourFundingSectionPopulator yourFundingSectionPopulator;

    @Mock
    private ApplicationNavigationPopulator navigationPopulator;

    @Mock
    private SectionService sectionService;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Mock
    QuestionRestService questionRestService;

    @Mock
    QuestionService questionService;

    @Test
    public void testPopulate() {
        ApplicantSectionResource section = newApplicantSectionResource()
                .withCurrentApplicant(newApplicantResource().withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build()).build())
                .withCurrentUser(newUserResource().build())
                .withCompetition(newCompetitionResource().build())
                .withApplication(newApplicationResource().build())
                .withSection(newSectionResource().build())
                .build();
        ApplicationForm form = mock(ApplicationForm.class);
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        AbstractFormInputViewModel formInputViewModel = mock(AbstractFormInputViewModel.class);

        QuestionResource researchQuestion = newQuestionResource().build();
        SectionResource yourOrganisationSection = newSectionResource().build();

        when(sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(asList(yourOrganisationSection.getId()));
        when(formInputViewModelGenerator.fromSection(section, section, form, false)).thenReturn(asList(formInputViewModel));
        when(questionService.getQuestionStatusesForApplicationAndOrganisation(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(asMap(researchQuestion.getId(), newQuestionStatusResource().withMarkedAsComplete(true).build()));
        when(sectionService.getOrganisationFinanceSection(section.getCompetition().getId())).thenReturn(yourOrganisationSection);
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(section.getCompetition().getId(), QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchQuestion));

        YourFundingSectionViewModel viewModel = yourFundingSectionPopulator.populate(section, form, model, bindingResult, false, Optional.empty(), false);

        assertThat(viewModel.isSection(), equalTo(true));
        assertThat(viewModel.isComplete(), equalTo(false));
        assertThat(viewModel.getFormInputViewModels(), equalTo(asList(formInputViewModel)));
        assertThat(viewModel.isFundingSectionLocked(), equalTo(false));
        assertThat(viewModel.getResearchCategoryQuestionId(), equalTo(researchQuestion.getId()));
        assertThat(viewModel.getYourOrganisationSectionId(), equalTo(yourOrganisationSection.getId()));
    }

}
