package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.AssessmentCountSummaryResource;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class AssessmentCountSummaryResourceBuilder<T extends AssessmentCountSummaryResource, B extends AssessmentCountSummaryResourceBuilder<T,B>>
        extends BaseBuilder<T, B> {

    protected AssessmentCountSummaryResourceBuilder(List<BiConsumer<Integer, T>> multiActions) {
        super(multiActions);
    }

    public B withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public B withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public B withAccepted(Long... accepted) {
        return withArraySetFieldByReflection("accepted", accepted);
    }

    public B withSubmitted(Long... submitted) {
        return withArraySetFieldByReflection("submitted", submitted);
    }
}