package org.innovateuk.ifs.project.bankdetails.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsStatusResource;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class BankDetailsStatusResourceBuilder extends BaseBuilder<BankDetailsStatusResource, BankDetailsStatusResourceBuilder> {
    public BankDetailsStatusResourceBuilder(List<BiConsumer<Integer, BankDetailsStatusResource>> newActions) {
        super(newActions);
    }

    @Override
    protected BankDetailsStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, BankDetailsStatusResource>> actions) {
        return new BankDetailsStatusResourceBuilder(actions);
    }

    @Override
    protected BankDetailsStatusResource createInitial() {
        return new BankDetailsStatusResource();
    }

    public static BankDetailsStatusResourceBuilder newBankDetailsStatusResource(){
        return new BankDetailsStatusResourceBuilder(emptyList());
    }

    public BankDetailsStatusResourceBuilder withOrganisationId(Long... organisationIds) {
        return withArray((organisationId, bankDetailsStatus) -> setField("organisationId", organisationId, bankDetailsStatus), organisationIds);
    }

    public BankDetailsStatusResourceBuilder withOrganisationName(String... organisationNames) {
        return withArray((organisationName, bankDetailsStatus) -> setField("organisationName", organisationName, bankDetailsStatus), organisationNames);
    }

    public final BankDetailsStatusResourceBuilder withBankDetailsStatus(ProjectActivityStates... bankDetailsStatuses) {
        return withArray((bankDetailsStatus, bankDetailsStatusResource) -> setField("bankDetailsStatus", bankDetailsStatus, bankDetailsStatusResource), bankDetailsStatuses);
    }
}
