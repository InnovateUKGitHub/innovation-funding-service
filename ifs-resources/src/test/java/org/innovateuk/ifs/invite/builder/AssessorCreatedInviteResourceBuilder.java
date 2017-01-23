package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessorCreatedInviteResourceBuilder extends BaseBuilder<AssessorCreatedInviteResource, AssessorCreatedInviteResourceBuilder> {

    private AssessorCreatedInviteResourceBuilder(List<BiConsumer<Integer, AssessorCreatedInviteResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AssessorCreatedInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorCreatedInviteResource>> actions) {
        return new AssessorCreatedInviteResourceBuilder(actions);
    }

    @Override
    protected AssessorCreatedInviteResource createInitial() {
        return new AssessorCreatedInviteResource();
    }

    public static AssessorCreatedInviteResourceBuilder newAssessorCreatedInviteResource() {
        return new AssessorCreatedInviteResourceBuilder(emptyList());
    }

    public AssessorCreatedInviteResourceBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public AssessorCreatedInviteResourceBuilder withInviteId(Long... value) {
        return withArraySetFieldByReflection("inviteId", value);
    }

    // TODO INFUND 7674
    public AssessorCreatedInviteResourceBuilder withInnovationAreas(List<InnovationAreaResource>... value) {
        return withArraySetFieldByReflection("innovationAreas", value);
    }

    public AssessorCreatedInviteResourceBuilder withCompliant(Boolean... value) {
        return withArraySetFieldByReflection("compliant", value);
    }

    public AssessorCreatedInviteResourceBuilder withEmail(String... value) {
        return withArraySetFieldByReflection("email", value);
    }
}