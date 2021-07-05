package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.transactional.*;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionAssessmentConfigService;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupService;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class BuildDataFromFileTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private BuildDataFromFile buildDataFromFile;

    @Mock
    private ApplicationInnovationAreaService applicationInnovationAreaServiceMock;

    @Mock
    private CompetitionAssessmentConfigService competitionAssessmentConfigServiceMock;

    @Mock
    private CompetitionSetupService competitionSetupServiceMock;

    @Mock
    private SectionService sectionServiceMock;

    @Mock
    private QuestionService questionServiceMock;

    @Mock
    private QuestionSetupService questionSetupServiceMock;

    @Mock
    private QuestionPriorityOrderService questionPriorityOrderServiceMock;

    @Mock
    private SectionRepository sectionRepositoryMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private MilestoneRepository milestoneRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private MilestoneService milestoneServiceMock;

    @Mock
    private ApplicationService applicationServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private FormInputResponseService formInputResponseServiceMock;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private FormInputRepository formInputRepositoryMock;

    @Mock
    private QuestionRepository questionRepositoryMock;

    @Mock
    private QuestionStatusService questionStatusServiceMock;

    @Mock
    private EntityManager entityManagerMock;

    @Test
    public void buildFromFile() {

        String content = "competition name, application name, question name, response\ntest competition, test application, test question, test response";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());

        Competition competition = newCompetition().build();
        CompetitionResource competitionResource = newCompetitionResource().build();
        User user = newUser().build();
        ProcessRole processRole = newProcessRole().withUser(user).build();
        Question question = newQuestion().build();

        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionResource.getId())
                .build();
        Application application = newApplication()
                .withCompetition(competition)
                .build();

        when(competitionSetupServiceMock.create()).thenReturn(ServiceResult.serviceSuccess(competitionResource));
        when(competitionSetupServiceMock.save(anyLong(), any(CompetitionResource.class))).thenReturn(ServiceResult.serviceSuccess(competitionResource));
        when(competitionSetupServiceMock.copyFromCompetitionTypeTemplate(anyLong(), eq(13L))).thenReturn(serviceSuccess());
        when(milestoneServiceMock.getMilestoneByTypeAndCompetitionId(any(MilestoneType.class), anyLong())).thenReturn(serviceSuccess(newMilestoneResource().build()));
        when(milestoneServiceMock.updateMilestone(any(MilestoneResource.class))).thenReturn(serviceSuccess());
        when(milestoneServiceMock.updateCompletionStage(anyLong(), eq(CompetitionCompletionStage.RELEASE_FEEDBACK))).thenReturn(serviceSuccess());
        when(sectionRepositoryMock.findByTypeAndCompetitionId(eq(SectionType.APPLICATION_QUESTIONS), anyLong())).thenReturn(Optional.of(newSection().build()));
        when(competitionRepositoryMock.findById(anyLong())).thenReturn(Optional.of(competition));
        when(questionPriorityOrderServiceMock.peristAndPrioritiesQuestions(any(Competition.class), anyList(), any(Section.class)))
                .thenReturn(Collections.singletonList(newQuestion().build()));
        when(competitionAssessmentConfigServiceMock.update(anyLong(), any(CompetitionAssessmentConfigResource.class))).thenReturn(serviceSuccess());
        when(milestoneServiceMock.getAllMilestonesByCompetitionId(anyLong())).thenReturn(serviceSuccess(Collections.emptyList()));
        when(sectionServiceMock.getByCompetitionId(anyLong())).thenReturn(serviceSuccess(Collections.singletonList(newSectionResource().build())));
        when(questionServiceMock.findByCompetition(anyLong())).thenReturn(serviceSuccess(Collections.singletonList(newQuestionResource().build())));
        when(userRepositoryMock.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(organisationRepositoryMock.findOneByName(anyString())).thenReturn(newOrganisation().build());
        when(applicationServiceMock.createApplicationByApplicationNameForUserIdAndCompetitionId(anyString(), anyLong(), anyLong(), anyLong()))
                .thenReturn(serviceSuccess(applicationResource));
        when(milestoneRepositoryMock.findAllByCompetitionId(anyLong())).thenReturn(Collections.singletonList(newMilestone().build()));
        when(applicationRepositoryMock.findById(anyLong())).thenReturn(Optional.of(application));
        doNothing().when(entityManagerMock).refresh(any(Competition.class));
        doNothing().when(entityManagerMock).refresh(any(Application.class));
        when(applicationInnovationAreaServiceMock.setNoInnovationAreaApplies(anyLong())).thenReturn(serviceSuccess(applicationResource));
        when(applicationServiceMock.saveApplicationDetails(anyLong(), any(ApplicationResource.class))).thenReturn(serviceSuccess(ValidationMessages.noErrors()));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(anyLong(), any(ProcessRoleType.class), anyLong()))
                .thenReturn(processRole);
        when(questionRepositoryMock.findByCompetitionIdAndName(anyLong(), anyString())).thenReturn(question);
        when(formInputRepositoryMock.findByQuestionIdAndScopeAndType(anyLong(), any(FormInputScope.class), any(FormInputType.class)))
                .thenReturn(newFormInput().build());
        when(formInputResponseServiceMock.saveQuestionResponse(any(FormInputResponseCommand.class))).thenReturn(serviceSuccess(newFormInputResponseResource().build()));
        when(questionStatusServiceMock.markAsComplete(any(QuestionApplicationCompositeId.class), anyLong())).thenReturn(serviceSuccess(Collections.emptyList()));
        when(questionRepositoryMock.findFirstByCompetitionIdAndQuestionSetupType(anyLong(), any(QuestionSetupType.class)))
                .thenReturn(question);
        when(applicationServiceMock.updateApplicationState(anyLong(), any(ApplicationState.class))).thenReturn(serviceSuccess(applicationResource));

        buildDataFromFile.buildFromFile(inputStream);
    }
}
