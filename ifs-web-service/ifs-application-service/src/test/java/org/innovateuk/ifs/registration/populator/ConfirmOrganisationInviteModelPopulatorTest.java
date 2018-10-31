package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.viewmodel.ConfirmOrganisationInviteOrganisationViewModel;
import org.junit.Test;

import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;
import static org.junit.Assert.*;

public class ConfirmOrganisationInviteModelPopulatorTest extends BaseServiceUnitTest<ConfirmOrganisationInviteModelPopulator> {

    @Override
    protected ConfirmOrganisationInviteModelPopulator supplyServiceUnderTest() {
        return new ConfirmOrganisationInviteModelPopulator();
    }

    @Test
    public void populate() {
        long inviteOrganisationId = 1L;
        long leadOrganisationId = 2L;
        String organisationNameConfirmed = "Empire Ltd Confirmed";
        String organisationName = "Empire Ltd";
        String organisationTypeName = "Business";
        String leadApplicantEmail = "steve.smith@empire.com";
        String companiesHouseNumber = "123456789";
        String registerUrl = "/test-register-url/confirm";

        OrganisationResource organisation = newOrganisationResource()
                .withId(inviteOrganisationId)
                .withOrganisationType(BUSINESS.getId())
                .withOrganisationTypeName(organisationTypeName)
                .withName(organisationName)
                .withCompaniesHouseNumber(companiesHouseNumber)
                .build();

        ApplicationInviteResource invite = newApplicationInviteResource()
                .withInviteOrganisationNameConfirmed(organisationNameConfirmed)
                .withLeadApplicantEmail(leadApplicantEmail)
                .withLeadOrganisationId(leadOrganisationId)
                .build();

        ConfirmOrganisationInviteOrganisationViewModel result = service.populate(invite, organisation, registerUrl);

        assertEquals(organisationNameConfirmed, result.getPartOfOrganisation());
        assertEquals(organisationTypeName, result.getOrganisationType());
        assertEquals(organisationName, result.getRegistrationName());
        assertEquals(companiesHouseNumber, result.getRegistrationNumber());
        assertEquals(leadApplicantEmail, result.getLeadApplicantEmail());
        assertTrue(result.isShowRegistrationNumber());
        assertFalse(result.isLeadOrganisation());
        assertEquals(registerUrl, result.getRegisterUrl());
    }

    @Test
    public void populate_noRegistrationNumber() {
        long inviteOrganisationId = 1L;
        long leadOrganisationId = 2L;
        String organisationNameConfirmed = "Empire Ltd Confirmed";
        String organisationName = "Empire Ltd";
        String organisationTypeName = "Business";
        String leadApplicantEmail = "steve.smith@empire.com";
        String registerUrl = "/test-register-url/confirm";

        OrganisationResource organisation = newOrganisationResource()
                .withId(inviteOrganisationId)
                .withOrganisationType(BUSINESS.getId())
                .withOrganisationTypeName(organisationTypeName)
                .withName(organisationName)
                .build();

        ApplicationInviteResource invite = newApplicationInviteResource()
                .withInviteOrganisationNameConfirmed(organisationNameConfirmed)
                .withLeadApplicantEmail(leadApplicantEmail)
                .withLeadOrganisationId(leadOrganisationId)
                .build();

        ConfirmOrganisationInviteOrganisationViewModel result = service.populate(invite, organisation, registerUrl);

        assertEquals(organisationNameConfirmed, result.getPartOfOrganisation());
        assertEquals(organisationTypeName, result.getOrganisationType());
        assertEquals(organisationName, result.getRegistrationName());
        assertNull(result.getRegistrationNumber());
        assertEquals(leadApplicantEmail, result.getLeadApplicantEmail());
        assertFalse(result.isShowRegistrationNumber());
        assertFalse(result.isLeadOrganisation());
        assertEquals(registerUrl, result.getRegisterUrl());
    }

    @Test
    public void populate_noRegistrationNumberWhenOrgTypeIsResearch() {
        long inviteOrganisationId = 1L;
        long leadOrganisationId = 2L;
        String organisationNameConfirmed = "Empire Ltd Confirmed";
        String organisationName = "Empire Ltd";
        String organisationTypeName = "Research";
        String leadApplicantEmail = "steve.smith@empire.com";
        String registerUrl = "/test-register-url/confirm";

        OrganisationResource organisation = newOrganisationResource()
                .withId(inviteOrganisationId)
                .withOrganisationType(RESEARCH.getId())
                .withOrganisationTypeName(organisationTypeName)
                .withName(organisationName)
                .build();

        ApplicationInviteResource invite = newApplicationInviteResource()
                .withInviteOrganisationNameConfirmed(organisationNameConfirmed)
                .withLeadApplicantEmail(leadApplicantEmail)
                .withLeadOrganisationId(leadOrganisationId)
                .build();

        ConfirmOrganisationInviteOrganisationViewModel result = service.populate(invite, organisation, registerUrl);

        assertEquals(organisationNameConfirmed, result.getPartOfOrganisation());
        assertEquals(organisationTypeName, result.getOrganisationType());
        assertEquals(organisationName, result.getRegistrationName());
        assertNull(result.getRegistrationNumber());
        assertEquals(leadApplicantEmail, result.getLeadApplicantEmail());
        assertFalse(result.isShowRegistrationNumber());
        assertFalse(result.isLeadOrganisation());
        assertEquals(registerUrl, result.getRegisterUrl());
    }

    @Test
    public void populate_inviteForLeadOrganisation() {
        long leadOrganisationId = 1L;
        String organisationNameConfirmed = "Empire Ltd Confirmed";
        String organisationName = "Empire Ltd";
        String organisationTypeName = "Business";
        String leadApplicantEmail = "steve.smith@empire.com";
        String companiesHouseNumber = "123456789";
        String registerUrl = "/test-register-url/confirm";

        OrganisationResource organisation = newOrganisationResource()
                .withId(leadOrganisationId)
                .withOrganisationType(BUSINESS.getId())
                .withOrganisationTypeName(organisationTypeName)
                .withName(organisationName)
                .withCompaniesHouseNumber(companiesHouseNumber)
                .build();

        ApplicationInviteResource invite = newApplicationInviteResource()
                .withInviteOrganisationNameConfirmed(organisationNameConfirmed)
                .withLeadApplicantEmail(leadApplicantEmail)
                .withLeadOrganisationId(leadOrganisationId)
                .build();

        ConfirmOrganisationInviteOrganisationViewModel result = service.populate(invite, organisation, registerUrl);

        assertEquals(organisationNameConfirmed, result.getPartOfOrganisation());
        assertEquals(organisationTypeName, result.getOrganisationType());
        assertEquals(organisationName, result.getRegistrationName());
        assertEquals(companiesHouseNumber, result.getRegistrationNumber());
        assertEquals(leadApplicantEmail, result.getLeadApplicantEmail());
        assertTrue(result.isShowRegistrationNumber());
        assertTrue(result.isLeadOrganisation());
        assertEquals(registerUrl, result.getRegisterUrl());
    }
}