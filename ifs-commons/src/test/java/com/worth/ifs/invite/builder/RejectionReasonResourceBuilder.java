package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.base.amend.BaseBuilderAmendFunctions;
import com.worth.ifs.invite.resource.RejectionReasonResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link com.worth.ifs.assessment.domain.AssessorFormInputResponse}
 */
public class RejectionReasonResourceBuilder extends BaseBuilder<RejectionReasonResource, RejectionReasonResourceBuilder> {

    private RejectionReasonResourceBuilder(List<BiConsumer<Integer, RejectionReasonResource>> newActions) {
        super(newActions);
    }

    public static RejectionReasonResourceBuilder newRejectionReasonResource() {
        return new RejectionReasonResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected RejectionReasonResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, RejectionReasonResource>> actions) {
        return new RejectionReasonResourceBuilder(actions);
    }

    @Override
    protected RejectionReasonResource createInitial() {
        return new RejectionReasonResource();
    }

    public RejectionReasonResourceBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public RejectionReasonResourceBuilder withReason(String... reasons) {
        return withArray((reason, rejectionReasonResource) -> setField("reason", reason, rejectionReasonResource), reasons);
    }

    public RejectionReasonResourceBuilder withActive(Boolean... actives) {
        return withArray((active, rejectionReasonResource) -> setField("active", active, rejectionReasonResource), actives);
    }

    public RejectionReasonResourceBuilder withPriority(Integer... priorities) {
        return withArray((priority, rejectionReasonResource) -> setField("priority", priority, rejectionReasonResource), priorities);
    }
}