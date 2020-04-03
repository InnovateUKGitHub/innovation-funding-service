package org.innovateuk.ifs.application.forms.questions.terms.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationTermsPartnerModelPopulator;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerRowViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTermsPartnerModelPopulatorTest {

    @Mock
    private ApplicationRestService applicationRestServiceMock;
    @Mock
    private UserRestService userRestServiceMock;
    @Mock
    private OrganisationService organisationServiceMock;
    @Mock
    private SectionService sectionServiceMock;

    @InjectMocks
    private ApplicationTermsPartnerModelPopulator populator;

    @Test
    public void populate() {
        String termsTemplate = "terms-template";
        boolean collaborative = true;

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .build();

        long questionId = 3L;
        SectionResource termsAndConditionsSection = newSectionResource()
                .withQuestions(singletonList(questionId))
                .build();

        OrganisationResource leadOrganisation = newOrganisationResource().withName("Lead").build();
        OrganisationResource collaboratorOrganisation = newOrganisationResource().withName("Collaborator").build();
        SortedSet<OrganisationResource> organisations = new TreeSet<>((o1, o2) -> (int) (o1.getId() - o2.getId()));
        organisations.add(leadOrganisation);
        organisations.add(collaboratorOrganisation);


        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withApplication(application.getId())
                .withOrganisation(collaboratorOrganisation.getId(), leadOrganisation.getId())
                .withRole(COLLABORATOR, LEADAPPLICANT)
                .build(2);

        QuestionStatusResource questionStatus = newQuestionStatusResource().build();

        when(sectionServiceMock.getTermsAndConditionsSection(application.getCompetition())).thenReturn(termsAndConditionsSection);
        when(userRestServiceMock.findProcessRole(application.getId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getApplicationOrganisations(processRoles)).thenReturn(organisations);
        when(sectionServiceMock.getCompletedSectionsByOrganisation(application.getId())).thenReturn(
                Stream.of(
                        new SimpleEntry<Long, Set<Long>>(leadOrganisation.getId(), emptySet()),
                        new SimpleEntry<Long, Set<Long>>(collaboratorOrganisation.getId(), singleton(termsAndConditionsSection.getId()))
                )
                        .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))
        );

        ApplicationTermsPartnerViewModel actual = populator.populate(application, questionId);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(questionId, actual.getQuestionId());

        ApplicationTermsPartnerRowViewModel expectedRow1 =
                new ApplicationTermsPartnerRowViewModel(leadOrganisation.getName(), true, false);
        ApplicationTermsPartnerRowViewModel expectedRow2 =
                new ApplicationTermsPartnerRowViewModel(collaboratorOrganisation.getName(), false, true);

        assertEquals(expectedRow1, actual.getPartners().get(0));
        assertEquals(expectedRow2, actual.getPartners().get(1));

        InOrder inOrder = inOrder(applicationRestServiceMock, userRestServiceMock, organisationServiceMock, sectionServiceMock);
        inOrder.verify(sectionServiceMock).getTermsAndConditionsSection(application.getCompetition());
        inOrder.verify(userRestServiceMock).findProcessRole(application.getId());
        inOrder.verify(organisationServiceMock).getApplicationOrganisations(processRoles);
        inOrder.verify(sectionServiceMock).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }
}