package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.resource.ContractResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.*;
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
        return withArray((id, contract) -> setField("id", id, contract) , ids);
    }

    public ContractResourceBuilder withCurrent(Boolean... currents) {
        return withArray((current, contract) -> setField("current", current, contract), currents);
    }

    public ContractResourceBuilder withText(String... texts) {
        return withArray((text, contract) -> setField("text", text, contract), texts);
    }

    public ContractResourceBuilder withAnnexOne(String... annexOnes) {
        return withArray((annexOne, contract) -> setField("annexOne", annexOne, contract), annexOnes);
    }

    public ContractResourceBuilder withAnnexTwo(String... annexTwos) {
        return withArray((annexTwo, contract) -> setField("annexTwo", annexTwo, contract), annexTwos);
    }

    public ContractResourceBuilder withAnnexThree(String... annexThrees) {
        return withArray((annexThree, contract) -> setField("annexThree", annexThree, contract), annexThrees);
    }

    @Override
    protected ContractResource createInitial() {
        return createDefault(ContractResource.class);
    }
}
