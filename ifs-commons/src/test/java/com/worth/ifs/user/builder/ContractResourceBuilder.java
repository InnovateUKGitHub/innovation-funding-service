package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.resource.ContractResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link com.worth.ifs.user.resource.ContractResource}.
 */
public class ContractResourceBuilder extends BaseBuilder<ContractResource, ContractResourceBuilder> {
    private ContractResourceBuilder(List<BiConsumer<Integer, ContractResource>> multiActions) {
        super(multiActions);
    }

    public static ContractResourceBuilder newContractResource() {
        return new ContractResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ContractResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ContractResource>> actions) {
        return new ContractResourceBuilder(actions);
    }

    public ContractResourceBuilder withId(Long... ids) {
        return withArray((id, contract) -> setField("id", id, contract), ids);
    }

    public ContractResourceBuilder withCurrent(Boolean... currents) {
        return withArray((current, contract) -> setField("current", current, contract), currents);
    }

    public ContractResourceBuilder withText(String... texts) {
        return withArray((text, contract) -> setField("text", text, contract), texts);
    }

    public ContractResourceBuilder withAnnexA(String... annexAs) {
        return withArray((annexA, contract) -> setField("annexA", annexA, contract), annexAs);
    }

    public ContractResourceBuilder withAnnexB(String... annexBs) {
        return withArray((annexB, contract) -> setField("annexB", annexB, contract), annexBs);
    }

    public ContractResourceBuilder withAnnexC(String... annexCs) {
        return withArray((annexC, contract) -> setField("annexC", annexC, contract), annexCs);
    }

    @Override
    protected ContractResource createInitial() {
        return createDefault(ContractResource.class);
    }
}
