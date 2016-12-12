package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.invite.resource.AvailableAssessorResource;
import com.worth.ifs.user.resource.BusinessType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AvailableAssessorResourceBuilder extends BaseBuilder<AvailableAssessorResource, AvailableAssessorResourceBuilder> {

    private AvailableAssessorResourceBuilder(List<BiConsumer<Integer, AvailableAssessorResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AvailableAssessorResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AvailableAssessorResource>> actions) {
        return new AvailableAssessorResourceBuilder(actions);
    }

    @Override
    protected AvailableAssessorResource createInitial() {
        return new AvailableAssessorResource();
    }

    public static AvailableAssessorResourceBuilder newAvailableAssessorResource() {
        return new AvailableAssessorResourceBuilder(emptyList());
    }

    public AvailableAssessorResourceBuilder withUserId(Long... value) {
        return withArraySetFieldByReflection("userId", value);
    }

    public AvailableAssessorResourceBuilder withFirstName(String... value) {
        return withArraySetFieldByReflection("firstName", value);
    }

    public AvailableAssessorResourceBuilder withLastName(String... value) {
        return withArraySetFieldByReflection("lastName", value);
    }

    public AvailableAssessorResourceBuilder withEmail(String... value) {
        return withArraySetFieldByReflection("email", value);
    }

    public AvailableAssessorResourceBuilder withBusinessType(BusinessType... value) {
        return withArraySetFieldByReflection("businessType", value);
    }

    public AvailableAssessorResourceBuilder withInnovationArea(CategoryResource... value) {
        return withArraySetFieldByReflection("innovationArea", value);
    }

    public AvailableAssessorResourceBuilder withCompliant(Boolean... value) {
        return withArraySetFieldByReflection("compliant", value);
    }

    public AvailableAssessorResourceBuilder withAdded(Boolean... value) {
        return withArraySetFieldByReflection("added", value);
    }
}