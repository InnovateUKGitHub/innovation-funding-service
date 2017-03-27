package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AvailableAssessorResourceBuilder extends AssessorInviteResourceBuilder<AvailableAssessorResource, AvailableAssessorResourceBuilder> {

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

    public AvailableAssessorResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public AvailableAssessorResourceBuilder withEmail(String... value) {
        return withArraySetFieldByReflection("email", value);
    }

    public AvailableAssessorResourceBuilder withBusinessType(BusinessType... value) {
        return withArraySetFieldByReflection("businessType", value);
    }
}
