package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class AssessorInviteResourceBuilder<T extends AssessorInviteResource, S extends AssessorInviteResourceBuilder> extends BaseBuilder<T, S> {

    protected AssessorInviteResourceBuilder(List<BiConsumer<Integer, T>> newMultiActions) {
        super(newMultiActions);
    }

    public S withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public S withInnovationAreas(List<InnovationAreaResource>... value) {
        return withArraySetFieldByReflection("innovationAreas", value);
    }

    public S withCompliant(Boolean... value) {
        return withArraySetFieldByReflection("compliant", value);
    }
}
