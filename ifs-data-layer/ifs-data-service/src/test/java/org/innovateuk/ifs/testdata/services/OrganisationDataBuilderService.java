package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.testdata.builders.OrganisationDataBuilder;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.COMP_ADMIN_EMAIL;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.PROJECT_FINANCE_EMAIL;

/**
 * A service that {@link org.innovateuk.ifs.testdata.BaseGenerateTestData} uses to generate Organisation data.  While
 * {@link org.innovateuk.ifs.testdata.BaseGenerateTestData} is responsible for gathering CSV information and
 * orchestarting the building of it, this service is responsible for taking the CSV data passed to it and using
 * the appropriate builders to generate and update entities.
 */
@Component
@Lazy
public class OrganisationDataBuilderService {

    @Autowired
    private GenericApplicationContext applicationContext;

    private OrganisationDataBuilder organisationDataBuilder;

    @PostConstruct
    public void readCsvs() {

        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);

        organisationDataBuilder = OrganisationDataBuilder.newOrganisationData(serviceLocator);
    }

    public void createOrganisation(CsvUtils.OrganisationLine line) {
        AddressResource address = null;
        if (line.addressType.contains(OrganisationAddressType.INTERNATIONAL) ||
                line.addressType.contains(OrganisationAddressType.KNOWLEDGE_BASE) ||
                line.addressType.contains(OrganisationAddressType.REGISTERED)) {
            address = newAddressResource().
                    withAddressLine1(line.addressLine1).
                    withAddressLine2(line.addressLine2).
                    withAddressLine3(line.addressLine3).
                    withTown(line.town).
                    withPostcode(line.postcode).
                    withCounty(line.county).
                    build();
        }
        OrganisationDataBuilder organisation =
                organisationDataBuilder.createOrganisation(line.name,
                        line.companyRegistrationNumber,
                        lookupOrganisationType(line.organisationType),
                        line.isInternational,
                        line.internationalRegistrationNumber,
                        address,
                        line.addressType,
                        line.dateOfIncorporation,
                        line.sicCodes,
                        line.organisationNumber,
                        line.executiveOfficers
                );

        organisation.build();
    }

    private OrganisationTypeEnum lookupOrganisationType(String organisationType) {
        return OrganisationTypeEnum.valueOf(organisationType.toUpperCase().replace(" ", "_"));
    }
}
