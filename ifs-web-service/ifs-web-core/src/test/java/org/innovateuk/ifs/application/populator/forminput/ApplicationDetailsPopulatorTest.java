package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResource;
import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResponseResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.populator.AssignButtonsPopulator;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.ApplicationDetailsInputViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResourceBuilder.newApplicantFormInputResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResponseResourceBuilder.newApplicantFormInputResponseResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationDetailsPopulatorTest {

    private static final String INNOVATION_AREA_NAME = "Innovation";
    private static final String RESEARCH_CATEGORY_NAME = "Research";

    @InjectMocks
    private ApplicationDetailsPopulator applicationDetailsPopulator;

    @Mock
    private AssignButtonsPopulator assignButtonsPopulator;
    @Test
    public void testPopulate() {
        ApplicantResource currentApplicant = newApplicantResource().withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build()).build();
        ApplicantQuestionResource question = newApplicantQuestionResource()
                .withCurrentApplicant(currentApplicant)
                .withCurrentUser(newUserResource().build())
                .withCompetition(newCompetitionResource().build())
                .withApplication(newApplicationResource()
                        .withInnovationArea(newInnovationAreaResource().withName(INNOVATION_AREA_NAME).build())
                        .withResearchCategory(newResearchCategoryResource().withName(RESEARCH_CATEGORY_NAME).build())
                        .build())
                .withApplicantQuestionStatuses(
                        newApplicantQuestionStatusResource()
                                .withStatus(newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .withMarkedAsCompleteBy(currentApplicant)
                        .build(1)
                )
                .withQuestion(newQuestionResource().build())
                .build();
        ApplicantFormInputResource applicantFormInput = newApplicantFormInputResource().build();
        ApplicantFormInputResponseResource applicantResponse = newApplicantFormInputResponseResource().build();
        AssignButtonsViewModel assignButtonsViewModel = new AssignButtonsViewModel();

        when(assignButtonsPopulator.populate(question, question, true)).thenReturn(assignButtonsViewModel);

        ApplicationDetailsInputViewModel viewModel = applicationDetailsPopulator.populate(question, null, question, applicantFormInput, applicantResponse);

        assertThat(viewModel.isComplete(), equalTo(true));
        assertThat(viewModel.getApplication(), equalTo(question.getApplication()));
        assertThat(viewModel.getApplicantSection(), equalTo(null));
        assertThat(viewModel.getInnovationAreaText(), equalTo("Change your innovation area"));
        assertThat(viewModel.getResearchCategoryText(), equalTo("Change your research category"));
        assertThat(viewModel.getSelectedInnovationAreaName(), equalTo(INNOVATION_AREA_NAME));
        assertThat(viewModel.getSelectedResearchCategoryName(), equalTo(RESEARCH_CATEGORY_NAME));
        assertThat(viewModel.getAssignButtonsViewModel(), equalTo(assignButtonsViewModel));
    }
}
