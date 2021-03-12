package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.AcademicAndSecretarialSupport;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class AcademicAndSecretarialSupportBuilder extends BaseBuilder<AcademicAndSecretarialSupport, AcademicAndSecretarialSupportBuilder> {

    public AcademicAndSecretarialSupportBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public AcademicAndSecretarialSupportBuilder withCost(BigInteger... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public AcademicAndSecretarialSupportBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static AcademicAndSecretarialSupportBuilder newAcademicAndSecretarialSupport() {
        return new AcademicAndSecretarialSupportBuilder(emptyList()).with(uniqueIds());
    }

    private AcademicAndSecretarialSupportBuilder(List<BiConsumer<Integer, AcademicAndSecretarialSupport>> multiActions) {
        super(multiActions);
    }

    @Override
    protected AcademicAndSecretarialSupportBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AcademicAndSecretarialSupport>> actions) {
        return new AcademicAndSecretarialSupportBuilder(actions);
    }

    @Override
    protected AcademicAndSecretarialSupport createInitial() {
        return newInstance(AcademicAndSecretarialSupport.class);
    }
}
