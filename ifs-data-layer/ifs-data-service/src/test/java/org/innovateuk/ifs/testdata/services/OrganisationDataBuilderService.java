package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.testdata.builders.OrganisationDataBuilder;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.COMP_ADMIN_EMAIL;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.PROJECT_FINANCE_EMAIL;

/**
 * TODO DW - document this class
 */
@Service
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
        OrganisationDataBuilder organisation =
                organisationDataBuilder.createOrganisation(line.name, line.companyRegistrationNumber, lookupOrganisationType(line.organisationType));

        for (OrganisationAddressType organisationType : line.addressType) {
            organisation = organisation.withAddress(organisationType,
                    line.addressLine1, line.addressLine2,
                    line.addressLine3, line.town,
                    line.postcode, line.county);
        }

        organisation.build();
    }

    private OrganisationTypeEnum lookupOrganisationType(String organisationType) {
        return OrganisationTypeEnum.valueOf(organisationType.toUpperCase().replace(" ", "_"));
    }


}
