package org.innovateuk.ifs.profile.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.profile.viewmodel.UserProfileViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class UserProfilePopulatorTest extends BaseUnitTest {

    @InjectMocks
    private UserProfilePopulator target;

    @Mock
    private OrganisationRestService organisationRestService;

    @Test
    public void populate() {
        UserResource user = newUserResource()
                .withFirstName("Steve")
                .withLastName("Smith")
                .withEmail("steve.smith@empire.com")
                .build();
        List<OrganisationResource> organisations = newOrganisationResource()
                .withName("organisation")
                .withOrganisationTypeName("Type")
                .withCompanyHouseNumber("123")
                .build(1);

        when(organisationRestService.getAllByUserId(user.getId())).thenReturn(restSuccess(organisations));

        UserProfileViewModel actual = target.populate(user);

        assertEquals(actual.getName(), user.getName());
        assertEquals(actual.getEmailAddress(), user.getEmail());
        assertEquals(actual.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(actual.getOrganisations().iterator().next().getName(),
                organisations.get(0).getName());
        assertEquals(actual.getOrganisations().iterator().next().getRegistrationNumber(),
                organisations.get(0).getCompanyHouseNumber());
        assertEquals(actual.getOrganisations().iterator().next().getType(),
                organisations.get(0).getOrganisationTypeName());

    }
}
