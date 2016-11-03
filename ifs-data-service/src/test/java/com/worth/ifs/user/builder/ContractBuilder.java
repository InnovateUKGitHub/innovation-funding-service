package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.Contract;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link Contract}s.
 */
public class ContractBuilder extends BaseBuilder<Contract, ContractBuilder> {

    private ContractBuilder(List<BiConsumer<Integer, Contract>> multiActions) {
        super(multiActions);
    }

    public static ContractBuilder newContract() {
        return new ContractBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ContractBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Contract>> actions) {
        return new ContractBuilder(actions);
    }

    public ContractBuilder withId(Long... ids) {
        return withArray((id, contract) -> setField("id", id, contract) , ids);
    }

    public ContractBuilder withCurrent(Boolean... currents) {
        return withArray((current, contract) -> setField("current", current, contract), currents);
    }

    public ContractBuilder withText(String... texts) {
        return withArray((text, contract) -> setField("text", text, contract), texts);
    }

    public ContractBuilder withAnnexOne(String... annexOnes) {
        return withArray((annexOne, contract) -> setField("annexOne", annexOne, contract), annexOnes);
    }

    public ContractBuilder withAnnexTwo(String... annexTwos) {
        return withArray((annexTwo, contract) -> setField("annexTwo", annexTwo, contract), annexTwos);
    }

    public ContractBuilder withAnnexThree(String... annexThrees) {
        return withArray((annexThree, contract) -> setField("annexThree", annexThree, contract), annexThrees);
    }

    @Override
    protected Contract createInitial() {
        return createDefault(Contract.class);
    }
}
