package com.worth.ifs.project.bankdetails.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.bankdetails.resource.BankDetailsStatusResource;
import com.worth.ifs.project.constant.ProjectActivityStates;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

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
