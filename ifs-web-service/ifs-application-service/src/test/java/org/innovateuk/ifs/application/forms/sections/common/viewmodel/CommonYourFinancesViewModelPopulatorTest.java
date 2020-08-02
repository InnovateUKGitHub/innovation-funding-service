package org.innovateuk.ifs.application.forms.sections.common.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CommonYourFinancesViewModelPopulatorTest {

    private long applicationId = 9876L;
    private long organisationId = 5432L;
    private long sectionId = 1234L;

    private Consumer<CommonYourProjectFinancesViewModel> expectViewModelIsComplete = model -> assertThat(model.isComplete()).isTrue();
    private Consumer<CommonYourProjectFinancesViewModel> expectViewModelIsOpen = model -> assertThat(model.isOpen()).isTrue();
    private Consumer<CommonYourProjectFinancesViewModel> expectViewModelIsReadonly = model -> assertThat(model.isReadOnly()).isTrue();

    private Consumer<CommonYourProjectFinancesViewModel> expectViewModelIsIncomplete = model -> assertThat(model.isComplete()).isFalse();
    private Consumer<CommonYourProjectFinancesViewModel> expectViewModelIsClosed = model -> assertThat(model.isOpen()).isFalse();
    private Consumer<CommonYourProjectFinancesViewModel> expectViewModelIsEditable = model -> assertThat(model.isReadOnly()).isFalse();

    private Consumer<CommonYourProjectFinancesViewModel> expectedExternalUserFinanceUrl = model ->
            assertThat(model.getFinancesUrl()).isEqualTo("/application/" + applicationId + "/form/FINANCE");

    private Consumer<CommonYourProjectFinancesViewModel> expectedInternalUserFinanceUrl = model ->
            assertThat(model.getFinancesUrl()).isEqualTo("/application/" + applicationId + "/form/FINANCE/" + organisationId);

    @InjectMocks
    private CommonYourFinancesViewModelPopulator populator;

    @Mock
    private ApplicationRestService applicationRestServiceMock;

    @Mock
    private CompetitionRestService competitionRestServiceMock;

    @Mock
    private OrganisationRestService organisationRestServiceMock;

    @Mock
    private SectionService sectionServiceMock;

    @Test
    public void populate() {

        boolean internalUser = false;
        List<Long> sectionsMarkedAsComplete = asList(111L, 333L);
        ApplicationState applicationState = ApplicationState.OPENED;
        CompetitionStatus competitionState = CompetitionStatus.OPEN;

        assertViewModelPopulatedOk(
                internalUser,
                sectionsMarkedAsComplete,
                applicationState,
                competitionState,
                expectViewModelIsIncomplete,
                expectViewModelIsOpen,
                expectViewModelIsEditable,
                expectedExternalUserFinanceUrl);
    }

    @Test
    public void populateComplete() {

        boolean internalUser = false;
        List<Long> sectionsMarkedAsComplete = asList(111L, sectionId, 333L);
        ApplicationState applicationState = ApplicationState.OPENED;
        CompetitionStatus competitionState = CompetitionStatus.OPEN;

        assertViewModelPopulatedOk(
                internalUser,
                sectionsMarkedAsComplete,
                applicationState,
                competitionState,
                expectViewModelIsComplete,
                expectViewModelIsOpen,
                expectViewModelIsReadonly,
                expectedExternalUserFinanceUrl);
    }

    @Test
    public void populateCompetitionClosed() {

        boolean internalUser = false;
        List<Long> sectionsMarkedAsComplete = asList(111L, 333L);
        ApplicationState applicationState = ApplicationState.OPENED;
        CompetitionStatus competitionState = CompetitionStatus.CLOSED;

        assertViewModelPopulatedOk(
                internalUser,
                sectionsMarkedAsComplete,
                applicationState,
                competitionState,
                expectViewModelIsIncomplete,
                expectViewModelIsClosed,
                expectViewModelIsReadonly,
                expectedExternalUserFinanceUrl);
    }

    @Test
    public void populateApplicationClosed() {

        boolean internalUser = false;
        List<Long> sectionsMarkedAsComplete = asList(111L, 333L);
        ApplicationState applicationState = ApplicationState.SUBMITTED;
        CompetitionStatus competitionState = CompetitionStatus.OPEN;

        assertViewModelPopulatedOk(
                internalUser,
                sectionsMarkedAsComplete,
                applicationState,
                competitionState,
                expectViewModelIsIncomplete,
                expectViewModelIsClosed,
                expectViewModelIsReadonly,
                expectedExternalUserFinanceUrl);
    }

    @Test
    public void populateForInternalUser() {

        boolean internalUser = true;
        List<Long> sectionsMarkedAsComplete = asList(111L, 333L);
        ApplicationState applicationState = ApplicationState.OPENED;
        CompetitionStatus competitionState = CompetitionStatus.OPEN;

        assertViewModelPopulatedOk(
                internalUser,
                sectionsMarkedAsComplete,
                applicationState,
                competitionState,
                expectViewModelIsIncomplete,
                expectViewModelIsClosed,
                expectViewModelIsReadonly,
                expectedInternalUserFinanceUrl);
    }

    @SafeVarargs
    private final void assertViewModelPopulatedOk(
            boolean internalUser,
            List<Long> sectionsMarkedAsComplete,
            ApplicationState applicationState,
            CompetitionStatus competitionStatus,
            Consumer<CommonYourProjectFinancesViewModel>... conditionalAssertions) {

        CompetitionResource competition = newCompetitionResource().
                withCompetitionStatus(competitionStatus).
                build();

        ApplicationResource application = newApplicationResource().
                withId(applicationId).
                withCompetition(competition.getId()).
                withName("Lovely application").
                withApplicationState(applicationState).
                build();

        OrganisationResource organisation = OrganisationResourceBuilder.newOrganisationResource().build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(sectionServiceMock.getCompleted(application.getId(), organisationId)).thenReturn(sectionsMarkedAsComplete);
        when(organisationRestServiceMock.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));

        CommonYourProjectFinancesViewModel viewModel = populator.populate(organisationId, application.getId(), sectionId, internalUser);

        assertThat(viewModel.getApplicationId()).isEqualTo(application.getId());
        assertThat(viewModel.getApplicationName()).isEqualTo(application.getName());
        assertThat(viewModel.getSectionId()).isEqualTo(sectionId);

        asList(conditionalAssertions).forEach(p -> p.accept(viewModel));
    }
}
