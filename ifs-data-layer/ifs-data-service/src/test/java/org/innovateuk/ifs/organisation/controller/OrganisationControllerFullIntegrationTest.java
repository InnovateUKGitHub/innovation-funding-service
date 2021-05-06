package org.innovateuk.ifs.organisation.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.address.mapper.AddressMapperImpl;
import org.innovateuk.ifs.address.mapper.AddressTypeMapperImpl;
import org.innovateuk.ifs.cfg.BaseFullStackIntegrationTest;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapperImpl;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapperImpl;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapperImpl;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.service.OrganisationMatchingServiceImpl;
import org.innovateuk.ifs.organisation.service.OrganisationPatternMatcher;
import org.innovateuk.ifs.organisation.transactional.OrganisationInitialCreationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationServiceImpl;
import org.innovateuk.ifs.service.CustomDateTimeProvider;
import org.innovateuk.ifs.user.mapper.UserMapperImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@ContextConfiguration(classes = {
        OrganisationController.class,
        OrganisationServiceImpl.class,
        OrganisationMatchingServiceImpl.class,
        AuditingEntityListener.class,
        CustomDateTimeProvider.class,
        OrganisationPatternMatcher.class,
        OrganisationMapperImpl.class,
        OrganisationAddressMapperImpl.class,
        AddressMapperImpl.class,
        AddressTypeMapperImpl.class,
        OrganisationTypeMapperImpl.class,
        UserMapperImpl.class
})
public class OrganisationControllerFullIntegrationTest extends BaseFullStackIntegrationTest {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationController organisationController;

    @MockBean
    private OrganisationInitialCreationService organisationInitialCreationService;

    private static String TEST_ORG_NAME_NO_CHN = "Trotters Independent Trading";
    private static String TEST_ORG_NAME_WITH_CHN = "Genco Pura Olive Oil Company";
    private static String UPDATED = " Updated";
    private static String CHN = "123456789";
    private static String CHN_UPDATED = "987654321";

    @Before
    public void clearDatabaseTestData() {
        clearDatabaseByName(TEST_ORG_NAME_NO_CHN);
        clearDatabaseByName(TEST_ORG_NAME_WITH_CHN);
    }

    @Test
    public void validationNoOrgId() {
        HttpStatus statusCode = organisationController.updateOrganisationName(Long.MAX_VALUE,
                TEST_ORG_NAME_WITH_CHN + UPDATED).getStatusCode();
        assertThat(statusCode, equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void validationTestNameTooLong() {
        createDatabaseOrganisation(TEST_ORG_NAME_WITH_CHN, Optional.of(CHN));
        OrganisationResource organisationResource = assertOneResultWithNameAndChn(
                organisationController.findOrganisationsByName(TEST_ORG_NAME_WITH_CHN).getSuccess(),
                TEST_ORG_NAME_WITH_CHN,
                Optional.of(CHN)
        );
        HttpStatus statusCode = organisationController
                .updateOrganisationName(organisationResource.getId(),
                        RandomStringUtils.randomAlphanumeric(260)).getStatusCode();
        assertThat(statusCode, equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testFindOrganisationWithChn() {
        createDatabaseOrganisation(TEST_ORG_NAME_WITH_CHN, Optional.of(CHN));
        assertOneResultWithNameAndChn(
                organisationController.findOrganisationsByCompaniesHouseNumber(CHN).getSuccess(),
                TEST_ORG_NAME_WITH_CHN,
                Optional.of(CHN)
        );
    }

    @Test
    public void testFindOrganisationNoChn() {
        createDatabaseOrganisation(TEST_ORG_NAME_NO_CHN, Optional.empty());
        assertOneResultWithNameAndChn(
                organisationController.findOrganisationsByName(TEST_ORG_NAME_NO_CHN).getSuccess(),
                TEST_ORG_NAME_NO_CHN,
                Optional.empty()
        );
    }

    @Test
    public void testUpdateOrganisation() {
        createDatabaseOrganisation(TEST_ORG_NAME_WITH_CHN, Optional.of(CHN));
        OrganisationResource organisationResource = assertOneResultWithNameAndChn(
            organisationController.findOrganisationsByName(TEST_ORG_NAME_WITH_CHN).getSuccess(),
            TEST_ORG_NAME_WITH_CHN,
            Optional.of(CHN)
        );
        Long id = organisationResource.getId();
        organisationController.updateOrganisationName(id, TEST_ORG_NAME_WITH_CHN + UPDATED);
        assertOneResultWithNameAndChn(
                organisationController.findOrganisationsByName(TEST_ORG_NAME_WITH_CHN + UPDATED).getSuccess(),
                TEST_ORG_NAME_WITH_CHN + UPDATED,
                Optional.of(CHN)
        );
    }

    private OrganisationResource assertOneResultWithNameAndChn(List<OrganisationResource> organisations, String name, Optional<String> companiesHouseNumber) {
        assertThat(organisations.size(), equalTo(1));
        assertThat(organisations.get(0).getName(), equalTo(name));
        if (companiesHouseNumber.isPresent()) {
            assertThat(organisations.get(0).getCompaniesHouseNumber(), equalTo(companiesHouseNumber.get()));
        }
        return organisations.get(0);
    }

    private void clearDatabaseByName(String name) {
        organisationRepository.findByNameOrderById(name).forEach(o -> organisationRepository.deleteById(o.getId()));
        assertThat(organisationController.findOrganisationsByName(name).getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    private void createDatabaseOrganisation(String name, Optional<String> companiesHouseNumber) {
        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(name);
        if (companiesHouseNumber.isPresent()) {
            organisationResource.setCompaniesHouseNumber(companiesHouseNumber.get());
        }
        organisationResource.setOrganisationType(OrganisationTypeEnum.BUSINESS.getId());
        organisationService.create(organisationResource);
    }
}