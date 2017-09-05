package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationMatchingServiceTest extends BaseServiceUnitTest<OrganisationMatchingServiceImpl> {

    @Mock
    private OrganisationPatternMatcher organisationPatternMatcher;

    @Override
    protected OrganisationMatchingServiceImpl supplyServiceUnderTest() {
        return new OrganisationMatchingServiceImpl();
    }

    private String academicName;
    private Organisation matchingResearchOrganisation;
    private OrganisationResource submittedResearchOrganisation;

    private String companiesHouseNumber;
    Organisation matchingBusinessOrganisation;
    OrganisationResource submittedBusinessOrganisation;

    @Before
    public void setUp() {
        companiesHouseNumber = "1234";
        matchingBusinessOrganisation = newOrganisation()
                .withCompanyHouseNumber(companiesHouseNumber)
                .withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        submittedBusinessOrganisation = newOrganisationResource()
                .withCompanyHouseNumber(companiesHouseNumber)
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        academicName = "academic";
        matchingResearchOrganisation = newOrganisation()
                .withName(academicName)
                .withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        submittedResearchOrganisation = newOrganisationResource()
                .withName(academicName)
                .withOrganisationType(OrganisationTypeEnum.RESEARCH.getId()).build();
    }

    @Test
    public void findOrganisationMatch_academicOrganisationShouldMatchByAddressAndNameAndCallsPatternMatchers() throws Exception {
        when(organisationRepositoryMock.findByName(eq(academicName))).thenReturn(Arrays.asList(matchingResearchOrganisation));
        when(organisationPatternMatcher.organisationTypeIsResearch(any())).thenReturn(true);
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), any(), anyBoolean())).thenReturn(true);

        Optional<Organisation> result = service.findOrganisationMatch(submittedResearchOrganisation);

        assertTrue(result.isPresent());

        verify(organisationPatternMatcher, times(0)).organisationTypeMatches(any(), any());
        verify(organisationPatternMatcher, times(0)).organisationAddressMatches(any(), any(), eq(OrganisationAddressType.REGISTERED), anyBoolean());
        verify(organisationPatternMatcher, times(1)).organisationAddressMatches(any(), any(), eq(OrganisationAddressType.OPERATING), anyBoolean());
        verify(organisationPatternMatcher, times(1)).organisationTypeIsResearch(any());
    }

    @Test
    public void findOrganisationMatch_academicOrganisationShouldNotMatchWhenAddressDiffers() throws Exception {
        when(organisationRepositoryMock.findByName(eq(academicName))).thenReturn(Arrays.asList(matchingResearchOrganisation));
        when(organisationPatternMatcher.organisationTypeIsResearch(any())).thenReturn(true);
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), any(), anyBoolean())).thenReturn(false);

        Optional<Organisation> result = service.findOrganisationMatch(submittedResearchOrganisation);

        assertFalse(result.isPresent());
    }

    @Test
    public void findOrganisationMatch_academicOrganisationShouldNotMatchWhenTypeIsNotResearch() throws Exception {
        when(organisationRepositoryMock.findByName(eq(academicName))).thenReturn(Arrays.asList(matchingResearchOrganisation));
        when(organisationPatternMatcher.organisationTypeIsResearch(any())).thenReturn(false);
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), any(), anyBoolean())).thenReturn(true);

        Optional<Organisation> result = service.findOrganisationMatch(submittedResearchOrganisation);

        assertFalse(result.isPresent());
    }

    @Test
    public void findOrganisationMatch_academicOrganisationShouldNotMatchWhenBothTypeAndAddressDiffers() throws Exception {
        when(organisationRepositoryMock.findByName(eq(academicName))).thenReturn(Arrays.asList(matchingResearchOrganisation));
        when(organisationPatternMatcher.organisationTypeIsResearch(any())).thenReturn(false);
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), any(), anyBoolean())).thenReturn(false);

        Optional<Organisation> result = service.findOrganisationMatch(submittedResearchOrganisation);

        assertFalse(result.isPresent());
    }

    @Test
    public void findOrganisationMatch_academicOrganisationShouldWhenNoOrganisationWithNameIsFound() throws Exception {
        when(organisationRepositoryMock.findByName(eq(academicName))).thenReturn(Collections.emptyList());
        when(organisationPatternMatcher.organisationTypeIsResearch(any())).thenReturn(true);
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), any(), anyBoolean())).thenReturn(true);

        Optional<Organisation> result = service.findOrganisationMatch(submittedResearchOrganisation);

        assertFalse(result.isPresent());
    }

    @Test
    public void findOrganisationMatch_companiesHouseOrganisationShouldMatchWhenCompaniesHouseNumberAndTypeAndAddressesMatchAndCallsPatternMatchers() throws Exception {
        when(organisationRepositoryMock.findByCompanyHouseNumber(eq(companiesHouseNumber))).thenReturn(Arrays.asList(matchingBusinessOrganisation));
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), any(), anyBoolean())).thenReturn(true);
        when(organisationPatternMatcher.organisationTypeMatches(any(), any())).thenReturn(true);

        Optional<Organisation> result = service.findOrganisationMatch(submittedBusinessOrganisation);

        assertTrue(result.isPresent());

        verify(organisationPatternMatcher, times(1)).organisationTypeMatches(any(), any());
        verify(organisationPatternMatcher, times(1)).organisationAddressMatches(any(), any(), eq(OrganisationAddressType.REGISTERED), anyBoolean());
        verify(organisationPatternMatcher, times(1)).organisationAddressMatches(any(), any(), eq(OrganisationAddressType.OPERATING), anyBoolean());
        verify(organisationPatternMatcher, times(0)).organisationTypeIsResearch(any());
    }

    @Test
    public void findOrganisationMatch_companiesHouseOrganisationShouldNotMatchWhenOrganisationTypeDiffers() throws Exception {
        when(organisationRepositoryMock.findByCompanyHouseNumber(eq(companiesHouseNumber))).thenReturn(Arrays.asList(matchingBusinessOrganisation));
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), any(), anyBoolean())).thenReturn(true);
        when(organisationPatternMatcher.organisationTypeMatches(any(), any())).thenReturn(false);

        Optional<Organisation> result = service.findOrganisationMatch(submittedBusinessOrganisation);

        assertFalse(result.isPresent());
    }

    @Test
    public void findOrganisationMatch_companiesHouseOrganisationShouldNotMatchWhenOperatingAddressDiffers() throws Exception {
        when(organisationRepositoryMock.findByCompanyHouseNumber(eq(companiesHouseNumber))).thenReturn(Arrays.asList(matchingBusinessOrganisation));
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), eq(OrganisationAddressType.OPERATING), anyBoolean())).thenReturn(true);
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), eq(OrganisationAddressType.REGISTERED), anyBoolean())).thenReturn(false);
        when(organisationPatternMatcher.organisationTypeMatches(any(), any())).thenReturn(false);

        Optional<Organisation> result = service.findOrganisationMatch(submittedBusinessOrganisation);

        assertFalse(result.isPresent());
    }

    @Test
    public void findOrganisationMatch_companiesHouseOrganisationShouldNotMatchWhenRegisteredAddressDiffers() throws Exception {
        when(organisationRepositoryMock.findByCompanyHouseNumber(eq(companiesHouseNumber))).thenReturn(Arrays.asList(matchingBusinessOrganisation));
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), eq(OrganisationAddressType.OPERATING), anyBoolean())).thenReturn(true);
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), eq(OrganisationAddressType.REGISTERED), anyBoolean())).thenReturn(false);
        when(organisationPatternMatcher.organisationTypeMatches(any(), any())).thenReturn(false);

        Optional<Organisation> result = service.findOrganisationMatch(submittedBusinessOrganisation);

        assertFalse(result.isPresent());
    }

    @Test
    public void findOrganisationMatch_companiesHouseOrganisationShouldNotMatchNoMatchingOrganisationIsFound() throws Exception {
        when(organisationRepositoryMock.findByCompanyHouseNumber(eq(companiesHouseNumber))).thenReturn(Collections.emptyList());
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), eq(OrganisationAddressType.OPERATING), anyBoolean())).thenReturn(true);
        when(organisationPatternMatcher.organisationAddressMatches(any(), any(), eq(OrganisationAddressType.REGISTERED), anyBoolean())).thenReturn(true);
        when(organisationPatternMatcher.organisationTypeMatches(any(), any())).thenReturn(true);

        Optional<Organisation> result = service.findOrganisationMatch(submittedBusinessOrganisation);

        assertFalse(result.isPresent());
    }
}