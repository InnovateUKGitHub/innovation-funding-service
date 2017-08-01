package org.innovateuk.ifs.project.bankdetails.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.user.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class BankDetailsBuilder extends BaseBuilder<BankDetails, BankDetailsBuilder> {
    private BankDetailsBuilder(List<BiConsumer<Integer, BankDetails>> multiActions) {
        super(multiActions);
    }

    public static BankDetailsBuilder newBankDetails() {
        return new BankDetailsBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected BankDetailsBuilder createNewBuilderWithActions(List<BiConsumer<Integer, BankDetails>> actions) {
        return new BankDetailsBuilder(actions);
    }

    @Override
    protected BankDetails createInitial() {
        return new BankDetails();
    }

    public BankDetailsBuilder withId(Long... ids) {
        return withArray((id, bankDetails) -> setField("id", id, bankDetails), ids);
    }

    public BankDetailsBuilder withOrganisation(Organisation... organisations) {
        return withArray((organisation, bankDetails) -> setField("organisation", organisation, bankDetails), organisations);
    }

    public BankDetailsBuilder withOrganiationAddress(OrganisationAddress... organisationAddresses) {
        return withArray((organisationAddress, bankDetails) -> setField("organisationAddress", organisationAddress, bankDetails), organisationAddresses);
    }

    public BankDetailsBuilder withSortCode(String... sortCodes) {
        return withArray((sortCode, bankDetails) -> setField("sortCode", sortCode, bankDetails), sortCodes);
    }

    public BankDetailsBuilder withAccountNumber(String... accountNumbers) {
        return withArray((accountNumber, bankDetails) -> setField("accountNumber", accountNumber, bankDetails), accountNumbers);
    }

    public BankDetailsBuilder withProject(Project... projects) {
        return withArray((project, bankDetails) -> setField("project", project, bankDetails), projects);
    }

    public BankDetailsBuilder withApproval(Boolean... approvals) {
        return withArray((approval, bankDetails) -> setField("manualApproval", approval, bankDetails), approvals);
    }
}
