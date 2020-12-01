package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.organisation.domain.ExecutiveOfficer;
import org.innovateuk.ifs.organisation.domain.SicCode;
import org.innovateuk.ifs.organisation.resource.*;
import org.innovateuk.ifs.testdata.builders.data.OrganisationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;

/**
 * Creates Organisations
 */
public class OrganisationDataBuilder extends BaseDataBuilder<OrganisationData, OrganisationDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisationDataBuilder.class);

    public OrganisationDataBuilder createOrganisation(String organisationName,
                                                      String companyRegistrationNumber,
                                                      OrganisationTypeEnum organisationType,
                                                      Boolean isInternational,
                                                      String internationalRegistrationNumber,
                                                      AddressResource address,
                                                      List<OrganisationAddressType> types,
                                                      LocalDate dateofIncorporation,
                                                      List<SicCodeRescource> sicCodes,
                                                      String organisationNumber,
                                                      List<ExecutiveOfficerResource> officers) {

        return with(data -> {
            doAs(systemRegistrar(), () -> {
                OrganisationResource organisation = newOrganisationResource().
                        withId().
                        withName(organisationName).
                        withCompaniesHouseNumber(companyRegistrationNumber).
                        withOrganisationType(organisationType.getId()).
                        withIsInternational(isInternational).
                        withInternationalRegistrationNumber(internationalRegistrationNumber).
                        withDateOfIncorporation(dateofIncorporation).
                        withSicCodes(sicCodes).
                        withOrganisationNumber(organisationNumber).
                        withExecutiveOfficers(officers).
                        build();
                List<OrganisationAddressResource> addresses = new ArrayList<>();
                if (address != null) {
                    types.forEach(type -> {
                        addresses.add(new OrganisationAddressResource(organisation, address, new AddressTypeResource(type.getId(), type.name())));
                    });
                }
                organisation.setAddresses(addresses);
                OrganisationResource created = organisationInitialCreationService.createOrMatch(organisation).getSuccess();
                data.setOrganisation(created);
            });
        });
    }

    public static OrganisationDataBuilder newOrganisationData(ServiceLocator serviceLocator) {

        return new OrganisationDataBuilder(emptyList(), serviceLocator);
    }

    private OrganisationDataBuilder(List<BiConsumer<Integer, OrganisationData>> multiActions,
                                    ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected OrganisationDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationData>> actions) {
        return new OrganisationDataBuilder(actions, serviceLocator);
    }

    @Override
    protected OrganisationData createInitial() {
        return new OrganisationData();
    }

    @Override
    protected void postProcess(int index, OrganisationData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Organisation '{}'", instance.getOrganisation().getName());
    }
}
