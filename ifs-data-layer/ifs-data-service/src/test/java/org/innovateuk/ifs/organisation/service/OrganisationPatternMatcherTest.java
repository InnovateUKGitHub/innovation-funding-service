package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeEnum;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeBuilder.newAddressType;
import static org.innovateuk.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrganisationPatternMatcherTest extends BaseServiceUnitTest<OrganisationPatternMatcher> {

    @Override
    protected OrganisationPatternMatcher supplyServiceUnderTest() {
        return new OrganisationPatternMatcher();
    }

    private OrganisationAddressResource submittedOrganisationRegisteredAddress;
    private OrganisationAddress matchingOrganisationRegisteredAddress;
    private OrganisationAddressResource slightlyMismatchingOrganisationAddress;

    @Before
    public void setUp() {
        AddressType operatingAddressType = newAddressType().withId(AddressTypeEnum.REGISTERED.getOrdinal()).build();

        Address registeredAddress = newAddress()
                .withAddressLine1("registeredabc")
                .withAddressLine2("registereddef")
                .withAddressLine3("registered ghi")
                .withCounty("registered jkl")
                .withPostcode("registeredmno")
                .withTown("registeredpqr").build();

        matchingOrganisationRegisteredAddress = newOrganisationAddress()
                .withAddress(registeredAddress)
                .withAddressType(operatingAddressType).build();

        AddressTypeResource submittedRegisteredAddressType = newAddressTypeResource().withId(AddressTypeEnum.REGISTERED.getOrdinal()).build();

        AddressResource submittedRegisteredAddress = newAddressResource()
                .withAddressLine1("registeredabc")
                .withAddressLine2("registereddef")
                .withAddressLine3("registered ghi")
                .withCounty("registered jkl")
                .withPostcode("registeredmno")
                .withTown("registeredpqr").build();

        submittedOrganisationRegisteredAddress = newOrganisationAddressResource()
                .withAddress(submittedRegisteredAddress)
                .withAddressType(submittedRegisteredAddressType)
                .build();

        AddressResource slightlyMismatchingAddress = newAddressResource()
                .withAddressLine1("REGISTEREDABC  ")
                .withAddressLine2("  REGISTEREDDEF")
                .withAddressLine3("registered ghi")
                .withCounty("regiStereD jKl")
                .withPostcode("registeredmno")
                .withTown("registeredpqr").build();

        slightlyMismatchingOrganisationAddress = newOrganisationAddressResource()
                .withAddress(slightlyMismatchingAddress)
                .withAddressType(submittedRegisteredAddressType)
                .build();
    }

    @Test
    public void organisationAddressMatches() throws Exception {
        Organisation organisation = newOrganisation().withAddress(Arrays.asList(matchingOrganisationRegisteredAddress)).build();
        OrganisationResource organisationResource = newOrganisationResource().withAddress(Arrays.asList(submittedOrganisationRegisteredAddress)).build();

        boolean result = service.organisationAddressMatches(organisation, organisationResource, AddressTypeEnum.REGISTERED, true);
        assertTrue(result);
    }

    @Test
    public void organisationAddressMatches_noMatchWhenAddressTypeDiffers() throws Exception {
        Organisation organisation = newOrganisation().withAddress(Arrays.asList(matchingOrganisationRegisteredAddress)).build();
        OrganisationResource organisationResource = newOrganisationResource().withAddress(Arrays.asList(submittedOrganisationRegisteredAddress)).build();

        boolean result = service.organisationAddressMatches(organisation, organisationResource, AddressTypeEnum.OPERATING, true);
        assertFalse(result);
    }

    @Test
    public void organisationAddressMatches_noMatchWhenOrganisationAddressTypeIsMissing() throws Exception {
        Organisation organisation = newOrganisation().build();
        OrganisationResource organisationResource = newOrganisationResource().withAddress(Arrays.asList(submittedOrganisationRegisteredAddress)).build();

        boolean result = service.organisationAddressMatches(organisation, organisationResource, AddressTypeEnum.OPERATING, true);
        assertFalse(result);
    }

    @Test
    public void organisationAddressMatches_noMatchWhenOrganisationResourceAddressTypeIsMissing() throws Exception {
        Organisation organisation = newOrganisation().withAddress(Arrays.asList(matchingOrganisationRegisteredAddress)).build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        boolean result = service.organisationAddressMatches(organisation, organisationResource, AddressTypeEnum.OPERATING, true);
        assertFalse(result);
    }

    @Test
    public void organisationAddressMatches_matchWhenOrganisationAddressSlightlyMismatches() throws Exception {
        Organisation organisation = newOrganisation().withAddress(Arrays.asList(matchingOrganisationRegisteredAddress)).build();
        OrganisationResource organisationResource = newOrganisationResource().withAddress(Arrays.asList(slightlyMismatchingOrganisationAddress)).build();

        boolean result = service.organisationAddressMatches(organisation, organisationResource, AddressTypeEnum.REGISTERED, true);
        assertTrue(result);
    }

    @Test
    public void organisationAddressMatches_matchWhenOrganisationsBothMissAddressAndIsNotRequired() throws Exception {
        Organisation organisation = newOrganisation().build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        boolean result = service.organisationAddressMatches(organisation, organisationResource, AddressTypeEnum.REGISTERED, false);
        assertTrue(result);
    }

    @Test
    public void organisationAddressMatches_noMatchWhenExistingOrganisationMissesAddressAndIsNotRequired() throws Exception {
        Organisation organisation = newOrganisation().build();
        OrganisationResource organisationResource = newOrganisationResource().withAddress(Arrays.asList(submittedOrganisationRegisteredAddress)).build();

        boolean result = service.organisationAddressMatches(organisation, organisationResource, AddressTypeEnum.REGISTERED, false);
        assertFalse(result);
    }

    @Test
    public void organisationAddressMatches_noMatchWhenSubmittedOrganisationMissesAddressAndIsNotRequired() throws Exception {
        Organisation organisation = newOrganisation().withAddress(Arrays.asList(matchingOrganisationRegisteredAddress)).build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        boolean result = service.organisationAddressMatches(organisation, organisationResource, AddressTypeEnum.REGISTERED, false);
        assertFalse(result);
    }

    @Test
    public void organisationAddressMatches_matchWhenBothAddressesOnlyNullValues() throws Exception {
        Organisation organisation = newOrganisation().withAddress(newOrganisationAddress().build(1)).build();
        OrganisationResource organisationResource = newOrganisationResource().withAddress(newOrganisationAddressResource().build(1)).build();

        boolean result = service.organisationAddressMatches(organisation, organisationResource, AddressTypeEnum.REGISTERED, false);
        assertTrue(result);
    }


    @Test
    public void organisationTypeIsResearch() throws Exception {
        Organisation organisation = newOrganisation().withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        boolean result = service.organisationTypeIsResearch(organisation);
        assertTrue(result);
    }

    @Test
    public void organisationTypeIsResearch_noMatchIfTypeIsNotResearch() throws Exception {
        Organisation organisation = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        boolean result = service.organisationTypeIsResearch(organisation);
        assertFalse(result);
    }

    @Test
    public void organisationTypeIsResearch_noMatchIfTypeIsMissing() throws Exception {
        Organisation organisation = newOrganisation().build();
        boolean result = service.organisationTypeIsResearch(organisation);
        assertFalse(result);
    }

    @Test
    public void organisationTypeMatches() throws Exception {
        Organisation organisation = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        OrganisationResource organisationResource  = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        boolean result = service.organisationTypeMatches(organisation, organisationResource);
        assertTrue(result);
    }

    @Test
    public void organisationTypeMatches_noMatchWhenTypesDiffer() throws Exception {
        Organisation organisation = newOrganisation().withOrganisationType(OrganisationTypeEnum.RTO).build();
        OrganisationResource organisationResource  = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        boolean result = service.organisationTypeMatches(organisation, organisationResource);
        assertFalse(result);
    }

    @Test
    public void organisationTypeMatches_noMatchWhenOrganisationMissesType() throws Exception {
        Organisation organisation = newOrganisation().build();
        OrganisationResource organisationResource  = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        boolean result = service.organisationTypeMatches(organisation, organisationResource);
        assertFalse(result);
    }

    @Test
    public void organisationTypeMatches_noMatchWhenOrganisationResourceIsNull() throws Exception {
        Organisation organisation = newOrganisation().build();
        OrganisationResource organisationResource  = null;

        boolean result = service.organisationTypeMatches(organisation, organisationResource);
        assertFalse(result);
    }
}