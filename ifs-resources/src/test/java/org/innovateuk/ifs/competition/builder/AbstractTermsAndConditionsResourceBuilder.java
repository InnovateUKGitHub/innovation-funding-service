package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

abstract class AbstractTermsAndConditionsResourceBuilder<T extends TermsAndConditionsResource, B> extends
        BaseBuilder<T, B> {

    protected AbstractTermsAndConditionsResourceBuilder(List<BiConsumer<Integer, T>> newMultiActions) {
        super(newMultiActions);
    }

    public B withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public B withName(String... names) {
        return withArray(BaseBuilderAmendFunctions::setName, names);
    }

    public B withTemplate(String... templates) {
        return withArraySetFieldByReflection("template", templates);
    }

    public B withVersion(Integer... versions) {
        return withArraySetFieldByReflection("version", versions);
    }

    public B withCreatedBy(String... users) {
        return withArraySetFieldByReflection("createdBy", users);
    }

    public B withCreatedOn(ZonedDateTime... createdOns) {
        return withArraySetFieldByReflection("createdOn", createdOns);
    }

    public B withModifiedBy(String... users) {
        return withArraySetFieldByReflection("modifiedBy", users);
    }

    public B withModifiedOn(ZonedDateTime... modifiedOns) {
        return withArraySetFieldByReflection("modifiedOn", modifiedOns);
    }

}