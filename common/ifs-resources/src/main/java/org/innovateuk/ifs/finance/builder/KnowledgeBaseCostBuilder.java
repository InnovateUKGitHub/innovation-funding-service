package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.KnowledgeBaseCost;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class KnowledgeBaseCostBuilder extends BaseBuilder<KnowledgeBaseCost, KnowledgeBaseCostBuilder> {

    public KnowledgeBaseCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public KnowledgeBaseCostBuilder withCost(BigInteger... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public KnowledgeBaseCostBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public KnowledgeBaseCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public KnowledgeBaseCostBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static KnowledgeBaseCostBuilder newKnowledgeBaseCost() {
        return new KnowledgeBaseCostBuilder(emptyList()).with(uniqueIds());
    }

    private KnowledgeBaseCostBuilder(List<BiConsumer<Integer, KnowledgeBaseCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected KnowledgeBaseCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, KnowledgeBaseCost>> actions) {
        return new KnowledgeBaseCostBuilder(actions);
    }

    @Override
    protected KnowledgeBaseCost createInitial() {
        return newInstance(KnowledgeBaseCost.class);
    }
}