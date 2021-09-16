package org.innovateuk.ifs.project.spendprofile.viewmodel;

import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.spendprofile.OrganisationReviewDetails;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;

public class ProjectSpendProfileProjectSummaryViewModelTest {

    @Test
    public void showMoSpendProfileJourney() {
        ProjectSpendProfileProjectSummaryViewModel viewModel;

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, null, false, Collections.emptyMap(),
                false, false, false, false, null);

        assertFalse(viewModel.showMoSpendProfileJourney());

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, null, false, Collections.emptyMap(),
                false, false, false, true, null);

        assertFalse(viewModel.showMoSpendProfileJourney());

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, null, false, Collections.emptyMap(),
                false, false, true, false, null);

        assertFalse(viewModel.showMoSpendProfileJourney());

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, null, false, Collections.emptyMap(),
                false, false, true, true, null);

        assertTrue(viewModel.showMoSpendProfileJourney());
    }

    @Test
    public void userCanReviewSpendProfile() {
        ProjectSpendProfileProjectSummaryViewModel viewModel;

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, null, true, Collections.emptyMap(),
                false, false, false, false, null);

        assertTrue(viewModel.userCanReviewSpendProfile());

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, null, true, Collections.emptyMap(),
                true, false, false, true, null);

        assertFalse(viewModel.userCanReviewSpendProfile());

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, null, true, Collections.emptyMap(),
                false, true, true, false, null);

        assertFalse(viewModel.userCanReviewSpendProfile());

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, null, false, Collections.emptyMap(),
                false, false, true, true, null);

        assertFalse(viewModel.userCanReviewSpendProfile());
    }

    @Test
    public void spendProfileReview() {
        Long leadOrganisationId = 1L;
        ProjectSpendProfileProjectSummaryViewModel viewModel;
        OrganisationResource leadOrganisation;
        OrganisationReviewDetails organisationReviewDetailsWithMo;
        OrganisationReviewDetails organisationReviewDetailsWithoutMo;

        ZonedDateTime reviewedOn = ZonedDateTime.now();

        leadOrganisation = newOrganisationResource()
                .withId(leadOrganisationId)
                .build();

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, null, true, Collections.emptyMap(),
                false, false, false, false, null);

        assertFalse(viewModel.isSpendProfileReviewedByMO());
        assertNull(viewModel.spendProfileReviewedOn());

        UserResource monitoringOfficer = newUserResource()
                .withRoleGlobal(Role.MONITORING_OFFICER)
                .build();

        organisationReviewDetailsWithMo = new OrganisationReviewDetails(leadOrganisationId, null,
                true, true, true, monitoringOfficer, reviewedOn);

        Map<Long, OrganisationReviewDetails> editablePartnersWithMo = new HashMap<>();
        editablePartnersWithMo.put(leadOrganisationId, organisationReviewDetailsWithMo);

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, leadOrganisation, true, editablePartnersWithMo,
                true, false, false, true, null);

        assertTrue(viewModel.isSpendProfileReviewedByMO());
        assertEquals(reviewedOn, viewModel.spendProfileReviewedOn());

        UserResource stakeholder = newUserResource()
                .withRoleGlobal(Role.STAKEHOLDER)
                .build();

        organisationReviewDetailsWithoutMo = new OrganisationReviewDetails(leadOrganisationId, null,
                true, true, true, stakeholder, reviewedOn);

        Map<Long, OrganisationReviewDetails> editablePartnersWithoutMo = new HashMap<>();
        editablePartnersWithMo.put(leadOrganisationId, organisationReviewDetailsWithoutMo);

        viewModel = new ProjectSpendProfileProjectSummaryViewModel(null,
                null, null, null, leadOrganisation, true, editablePartnersWithoutMo,
                false, true, true, false, null);

        assertFalse(viewModel.isSpendProfileReviewedByMO());
        assertNull(viewModel.spendProfileReviewedOn());
    }
}
