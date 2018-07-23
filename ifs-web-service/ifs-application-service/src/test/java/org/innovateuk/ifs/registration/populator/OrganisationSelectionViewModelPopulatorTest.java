package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.registration.viewmodel.OrganisationSelectionChoiceViewModel;
import org.innovateuk.ifs.registration.viewmodel.OrganisationSelectionViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationSelectionViewModelPopulatorTest {

    @InjectMocks
    private OrganisationSelectionViewModelPopulator populator;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Test
    public void populate() {
        UserResource user = newUserResource().build();
        HttpServletRequest request = mock(HttpServletRequest.class);
        String url = "Something.com";
        List<OrganisationResource> organisations = newOrganisationResource()
                .withName("Organisation 1", "Organisation 2")
                .withOrganisationTypeName("Type 1", "Type 2")
                .build(2);

        when(organisationRestService.getAllUsersOrganisations(user.getId())).thenReturn(restSuccess(organisations));
        when(registrationCookieService.isCollaboratorJourney(request)).thenReturn(false);

        OrganisationSelectionViewModel viewModel = populator.populate(user, request, url);

        organisations.forEach((organisation) -> {
            Optional<OrganisationSelectionChoiceViewModel> maybeChoice = viewModel.getChoices().stream().filter(choice -> choice.getName().equals(organisation.getName())).findAny();
            assertTrue(maybeChoice.isPresent());
            assertEquals(organisation.getOrganisationTypeName(), maybeChoice.get().getType());
            assertEquals(organisation.getId(), (Long) maybeChoice.get().getId());
        });

        assertEquals(viewModel.getNewOrganisationUrl(), url);
        assertEquals(viewModel.isCollaboratorJourney(), false);
    }


}
