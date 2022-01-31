package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationSubsidyBasisModelPopulator;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisViewModel;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.NorthernIrelandDeclarationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.assessment.builder.ApplicationAssessmentsResourceBuilder;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;


@RunWith(MockitoJUnitRunner.class)
public class NorthernIrelandDeclarationReadOnlyViewModelPopulatorTest {

    @InjectMocks
    private NorthernIrelandDeclarationReadOnlyViewModelPopulator populator;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private GenericQuestionReadOnlyViewModelPopulator genericQuestionReadOnlyViewModelPopulator;

    @Mock
    private ApplicationSubsidyBasisModelPopulator applicationSubsidyBasisPopulator;

    private ApplicationResource application;

    private CompetitionResource competition;

    private QuestionResource question;

    private UserResource user;

    @Before
    public void setup() {
        setField(populator, "genericQuestionReadOnlyViewModelPopulator", genericQuestionReadOnlyViewModelPopulator);
        setField(populator, "applicationSubsidyBasisPopulator", applicationSubsidyBasisPopulator);

        application = newApplicationResource()
                .build();
        competition = newCompetitionResource()
                .build();
        question = newQuestionResource()
                .withShortName("Subsidy basis")
                .withName("Will the project, including any related activities you want Innovate UK to fund, affect trade between Northern Ireland and the EU?")
                .withQuestionSetupType(QuestionSetupType.NORTHERN_IRELAND_DECLARATION)
                .build();
        user = newUserResource().withRoleGlobal(Role.IFS_ADMINISTRATOR).build();
    }

    @Test
    public void populate() {
        FormInputResource textarea = newFormInputResource()
                .withType(FormInputType.MULTIPLE_CHOICE)
                .withScope(FormInputScope.APPLICATION)
                .withQuestion(question.getId())
                .build();
        FormInputResponseResource textareaResponse = newFormInputResponseResource()
                .withFormInputs(textarea.getId())
                .withValue("No")
                .build();

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(), emptyList(), emptyList(),
                asList(textarea), asList(textareaResponse), emptyList(), emptyList(), emptyList());

        String questionStr = "Will the project, including any related activities you want Innovate UK to fund, affect trade between Northern Ireland and the EU?";
        String answerStr = "No";

        when(genericQuestionReadOnlyViewModelPopulator.populate(question, data, ApplicationReadOnlySettings.defaultSettings()))
                .thenReturn(new GenericQuestionReadOnlyViewModel(data,
                        question,
                        "",
                        questionStr,
                        false,
                        answerStr,
                        emptyList(),
                        false,
                        emptyList(),
                        null,
                        "",
                        emptyList(),
                        emptyList(),
                        0 ,0, false , false));
        when(applicationSubsidyBasisPopulator.populate(question, data.getApplication().getId()))
                .thenReturn(new ApplicationSubsidyBasisViewModel(emptyList()));

        NorthernIrelandDeclarationReadOnlyViewModel viewModel = populator.populate(question, data,
                ApplicationReadOnlySettings.defaultSettings());

        assertEquals(questionStr, viewModel.getQuestion());
        assertEquals(answerStr, viewModel.getAnswer());

        assertEquals(application.getId(), (Long) viewModel.getApplicationId());
        assertEquals(question.getId(), (Long) viewModel.getQuestionId());
        assertTrue(viewModel.isComplete());
    }

}
