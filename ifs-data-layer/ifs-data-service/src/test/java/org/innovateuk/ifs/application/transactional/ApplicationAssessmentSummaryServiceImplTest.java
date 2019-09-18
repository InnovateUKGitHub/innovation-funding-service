package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationAssessorMapper;
import org.innovateuk.ifs.application.mapper.ApplicationAssessorPageMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapArray;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ApplicationAssessmentSummaryServiceImplTest extends BaseServiceUnitTest<ApplicationAssessmentSummaryServiceImpl> {

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepositoryMock;

    @Mock
    private ApplicationAssessorMapper applicationAssessorMapperMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private ApplicationAssessorPageMapper applicationAssessorPageMapperMock;

    @Override
    protected ApplicationAssessmentSummaryServiceImpl supplyServiceUnderTest() {
        return new ApplicationAssessmentSummaryServiceImpl();
    }

    @Test
    public void getAssignedAssessors() throws Exception {
        Competition competition = newCompetition().build();
        Application application = newApplication()
                .withCompetition(competition)
                .build();

        List<AssessmentParticipant> participants = newAssessmentParticipant()
                .withUser(newUser()
                        .withId(1L, 2L)
                        .buildArray(2, User.class))
                .build(2);
        Long innovationArea = 4L;

        List<Assessment> assessments = newAssessment().build(2);


        List<ApplicationAssessorResource> expected = newApplicationAssessorResource()
                .build(2);


        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(1L, application.getId())).thenReturn(ofNullable(assessments.get(0)));
        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(2L, application.getId())).thenReturn(ofNullable(assessments.get(1)));
        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(assessmentParticipantRepositoryMock.findParticipantsWithAssessments(
                eq(competition.getId()),
                eq(CompetitionParticipantRole.ASSESSOR),
                eq(ACCEPTED),
                eq(application.getId()))).thenReturn(participants);

        when(applicationAssessorMapperMock.mapToResource(participants.get(0), ofNullable(assessments.get(0)))).thenReturn(expected.get(0));
        when(applicationAssessorMapperMock.mapToResource(participants.get(1), ofNullable(assessments.get(1)))).thenReturn(expected.get(1));

        List<ApplicationAssessorResource> found = service.getAssignedAssessors(application.getId()).getSuccess();
        assertEquals(expected, found);
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        Competition competition = newCompetition().build();
        Application application = newApplication()
                .withCompetition(competition)
                .build();

        Page<AssessmentParticipant> page = mock(Page.class);
        String assessorNameFilter = "";

        ApplicationAssessorPageResource expected = new ApplicationAssessorPageResource();

        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(assessmentParticipantRepositoryMock.findParticipantsWithoutAssessments(
                eq(competition.getId()),
                eq(CompetitionParticipantRole.ASSESSOR),
                eq(ACCEPTED),
                eq(application.getId()),
                eq(assessorNameFilter),
                any(Pageable.class))).thenReturn(page);

        when(applicationAssessorPageMapperMock.mapToResource(page)).thenReturn(expected);

        ApplicationAssessorPageResource found = service.getAvailableAssessors(application.getId(), 0, 20, assessorNameFilter).getSuccess();
        assertEquals(expected, found);
    }

    @Test
    public void getApplicationAssessmentSummary() throws Exception {
        Organisation[] organisations = newOrganisation()
                .withName("Acme Ltd.", "IO systems", "Liquid Dynamics", "Piezo Electrics")
                .buildArray(4, Organisation.class);

        Application application = newApplication()
                .withName("Progressive machines")
                .withInnovationArea(newInnovationArea()
                        .withName("Digital Manufacturing")
                        .build())
                .withCompetition(newCompetition()
                        .withName("Connected digital additive manufacturing")
                        .withCompetitionStatus(CLOSED)
                        .build())
                .withProcessRoles(newProcessRole()
                        .withRole(COLLABORATOR, COLLABORATOR, LEADAPPLICANT, COMP_ADMIN)
                        .withOrganisationId(simpleMapArray(organisations, Organisation::getId, Long.class))
                        .buildArray(4, ProcessRole.class))
                .build();

        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource()
                .withId(application.getId())
                .withName(application.getName())
                .withInnovationArea(application.getInnovationArea().getName())
                .withCompetitionId(application.getCompetition().getId())
                .withCompetitionName(application.getCompetition().getName())
                .withLeadOrganisation("Liquid Dynamics")
                .withCompetitionStatus(CLOSED)
                .withPartnerOrganisations(asList("Acme Ltd.", "IO systems"))
                .build();

        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        Stream.of(organisations)
                .forEach(organisation -> when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation)));

        ApplicationAssessmentSummaryResource found = service.getApplicationAssessmentSummary(application.getId()).getSuccess();

        assertEquals(expected, found);

        InOrder inOrder = inOrder(applicationRepositoryMock, organisationRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findById(application.getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[2].getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[0].getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[1].getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationAssessmentSummary_noLeadOrganisation() throws Exception {
        Organisation[] organisations = newOrganisation()
                .withName("Acme Ltd.", "IO systems", "Liquid Dynamics", "Piezo Electrics")
                .buildArray(4, Organisation.class);

        Application application = newApplication()
                .withName("Progressive machines")
                .withInnovationArea(newInnovationArea()
                        .withName("Digital Manufacturing")
                        .build())
                .withCompetition(newCompetition()
                        .withName("Connected digital additive manufacturing")
                        .withCompetitionStatus(CLOSED)
                        .build())
                .withProcessRoles(newProcessRole()
                        .withRole(COLLABORATOR, COLLABORATOR, COLLABORATOR, COMP_ADMIN)
                        .withOrganisationId(simpleMapArray(organisations, Organisation::getId, Long.class))
                        .buildArray(4, ProcessRole.class))
                .build();

        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource()
                .withId(application.getId())
                .withName(application.getName())
                .withInnovationArea(application.getInnovationArea().getName())
                .withCompetitionId(application.getCompetition().getId())
                .withCompetitionName(application.getCompetition().getName())
                .withLeadOrganisation("")
                .withCompetitionStatus(CLOSED)
                .withPartnerOrganisations(asList("Acme Ltd.", "IO systems", "Liquid Dynamics"))
                .build();

        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        Stream.of(organisations)
                .forEach(organisation -> when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation)));

        ApplicationAssessmentSummaryResource found = service.getApplicationAssessmentSummary(application.getId()).getSuccess();

        assertEquals(expected, found);

        InOrder inOrder = inOrder(applicationRepositoryMock, organisationRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findById(application.getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[0].getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[1].getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[2].getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationAssessmentSummary_partnersSortedAlphabetically() throws Exception {
        Organisation[] organisations = newOrganisation()
                .withName("IO systems", "Acme Ltd.", "Liquid Dynamics", "Piezo Electrics")
                .buildArray(4, Organisation.class);

        Application application = newApplication()
                .withName("Progressive machines")
                .withInnovationArea(newInnovationArea()
                        .withName("Digital Manufacturing")
                        .build())
                .withCompetition(newCompetition()
                        .withName("Connected digital additive manufacturing")
                        .withCompetitionStatus(FUNDERS_PANEL)
                        .build())
                .withProcessRoles(newProcessRole()
                        .withRole(COLLABORATOR, COLLABORATOR, LEADAPPLICANT, COMP_ADMIN)
                        .withOrganisationId(simpleMapArray(organisations, Organisation::getId, Long.class))
                        .buildArray(4, ProcessRole.class))
                .build();

        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource()
                .withId(application.getId())
                .withName(application.getName())
                .withInnovationArea(application.getInnovationArea().getName())
                .withCompetitionId(application.getCompetition().getId())
                .withCompetitionName(application.getCompetition().getName())
                .withLeadOrganisation("Liquid Dynamics")
                .withCompetitionStatus(FUNDERS_PANEL)
                .withPartnerOrganisations(asList("Acme Ltd.", "IO systems"))
                .build();

        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        Stream.of(organisations)
                .forEach(organisation -> when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation)));

        ApplicationAssessmentSummaryResource found = service.getApplicationAssessmentSummary(application.getId()).getSuccess();

        assertEquals(expected, found);

        InOrder inOrder = inOrder(applicationRepositoryMock, organisationRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findById(application.getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[2].getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[0].getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[1].getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationAssessmentSummary_multipleCollaboratorsPerOrg() throws Exception {
        Organisation[] organisations = newOrganisation()
                .withName("IO systems", "Acme Ltd.", "Liquid Dynamics", "Piezo Electrics")
                .buildArray(4, Organisation.class);

        Long[] orgIds = Stream.of(0,1,2,3,1,2,3).map(x -> organisations[x].getId()).toArray(Long[]::new);

        Application application = newApplication()
                .withName("Progressive machines")
                .withInnovationArea(newInnovationArea()
                        .withName("Digital Manufacturing")
                        .build())
                .withCompetition(newCompetition()
                        .withName("Connected digital additive manufacturing")
                        .withCompetitionStatus(FUNDERS_PANEL)
                        .build())
                .withProcessRoles(newProcessRole()
                        .withRole(COMP_ADMIN, LEADAPPLICANT, COLLABORATOR, COLLABORATOR, COLLABORATOR, COLLABORATOR, COLLABORATOR )
                        .withOrganisationId(orgIds)
                        .buildArray(7, ProcessRole.class))
                .build();

        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource()
                .withId(application.getId())
                .withName(application.getName())
                .withInnovationArea(application.getInnovationArea().getName())
                .withCompetitionId(application.getCompetition().getId())
                .withCompetitionName(application.getCompetition().getName())
                .withLeadOrganisation("Acme Ltd.")
                .withCompetitionStatus(FUNDERS_PANEL)
                .withPartnerOrganisations(asList("Liquid Dynamics", "Piezo Electrics"))
                .build();

        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        Stream.of(organisations)
                .forEach(organisation -> when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation)));

        ApplicationAssessmentSummaryResource found = service.getApplicationAssessmentSummary(application.getId()).getSuccess();

        assertEquals(expected, found);

        InOrder inOrder = inOrder(applicationRepositoryMock, organisationRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findById(application.getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[2].getId());
        inOrder.verify(organisationRepositoryMock).findById(organisations[3].getId());
        inOrder.verifyNoMoreInteractions();
    }
}