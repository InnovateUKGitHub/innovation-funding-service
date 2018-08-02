package org.innovateuk.ifs.project.bankdetails.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class BankDetailsResourceBuilder extends BaseBuilder<BankDetailsResource, BankDetailsResourceBuilder> {
    private BankDetailsResourceBuilder(List<BiConsumer<Integer, BankDetailsResource>> multiActions) {
        super(multiActions);
    }

    public static BankDetailsResourceBuilder newBankDetailsResource() {
        return new BankDetailsResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected BankDetailsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, BankDetailsResource>> actions) {
        return new BankDetailsResourceBuilder(actions);
    }

    @Override
    protected BankDetailsResource createInitial() {
        return new BankDetailsResource();
    }

    public BankDetailsResourceBuilder withId(Long... ids) {
        return withArray((id, bankDetails) -> setField("id", id, bankDetails), ids);
    }

    public BankDetailsResourceBuilder withOrganisation(Long... organisations) {
        return withArray((organisation, bankDetails) -> setField("organisation", organisation, bankDetails), organisations);
    }

    public BankDetailsResourceBuilder withOrganisationTypeName(String organisationTypeName) {
        return with((bankDetails) -> bankDetails.setOrganisationTypeName(organisationTypeName));
    }

    public BankDetailsResourceBuilder withRegistrationNumber(String registrationNumber) {
        return with((bankDetails) -> bankDetails.setRegistrationNumber(registrationNumber));
    }

    public BankDetailsResourceBuilder withCompanyName(String companyName) {
        return with((bankDetails) -> bankDetails.setCompanyName(companyName));
    }

    public BankDetailsResourceBuilder withOrganiationAddress(OrganisationAddressResource organisationAddressResource) {
        return with(bankDetails -> bankDetails.setOrganisationAddress(organisationAddressResource));
    }

    public BankDetailsResourceBuilder withProject(Long project) {
        return with(bankDetails -> bankDetails.setProject(project));
    }

    public BankDetailsResourceBuilder withSortCode(String sortCode) {
        return with(bankDetails -> bankDetails.setSortCode(sortCode));
    }

    public BankDetailsResourceBuilder withAccountNumber(String accountNumber) {
        return with(bankDetails -> bankDetails.setAccountNumber(accountNumber));
    }
}
