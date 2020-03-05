package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationAvailableAssessorResourceBuilder extends BaseBuilder<ApplicationAvailableAssessorResource, ApplicationAvailableAssessorResourceBuilder> {

    private ApplicationAvailableAssessorResourceBuilder(List<BiConsumer<Integer, ApplicationAvailableAssessorResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected ApplicationAvailableAssessorResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationAvailableAssessorResource>> actions) {
        return new ApplicationAvailableAssessorResourceBuilder(actions);
    }

    @Override
    protected ApplicationAvailableAssessorResource createInitial() {
        return newInstance(ApplicationAvailableAssessorResource.class);
    }

    public static ApplicationAvailableAssessorResourceBuilder newApplicationAvailableAssessorResource() {
        return new ApplicationAvailableAssessorResourceBuilder(emptyList());
    }

    public ApplicationAvailableAssessorResourceBuilder withUserId(Long... value) {
        return withArraySetFieldByReflection("userId", value);
    }

    public ApplicationAvailableAssessorResourceBuilder withFirstName(String... value) {
        return withArraySetFieldByReflection("firstName", value);
    }

    public ApplicationAvailableAssessorResourceBuilder withLastName(String... value) {
        return withArraySetFieldByReflection("lastName", value);
    }

    public ApplicationAvailableAssessorResourceBuilder withSkillAreas(String... value) {
        return withArraySetFieldByReflection("skillAreas", value);
    }

    public ApplicationAvailableAssessorResourceBuilder withTotalApplicationsCount(Long... value) {
        return withArraySetFieldByReflection("totalApplicationsCount", value);
    }

    public ApplicationAvailableAssessorResourceBuilder withAssignedCount(Long... value) {
        return withArraySetFieldByReflection("assignedCount", value);
    }

    public ApplicationAvailableAssessorResourceBuilder withSubmittedCount(Long... value) {
        return withArraySetFieldByReflection("submittedCount", value);
    }
}