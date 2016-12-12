package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.domain.RejectionReason;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class RejectionReasonBuilder extends BaseBuilder<RejectionReason, RejectionReasonBuilder> {

    private RejectionReasonBuilder(List<BiConsumer<Integer, RejectionReason>> newActions) {
        super(newActions);
    }

    public static RejectionReasonBuilder newRejectionReason() {
        return new RejectionReasonBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected RejectionReasonBuilder createNewBuilderWithActions(List<BiConsumer<Integer, RejectionReason>> actions) {
        return new RejectionReasonBuilder(actions);
    }

    @Override
    protected RejectionReason createInitial() {
        return new RejectionReason();
    }

    public RejectionReasonBuilder withId(Long... ids) {
        return withArray((id, rejectionReason) -> setField("id", id, rejectionReason) , ids);
    }

    public RejectionReasonBuilder withReason(String... reasons) {
        return withArray((reason, rejectionReason) -> setField("reason", reason, rejectionReason), reasons);
    }

    public RejectionReasonBuilder withActive(Boolean... actives) {
        return withArray((active, rejectionReason) -> setField("active", active, rejectionReason), actives);
    }

    public RejectionReasonBuilder withPriority(Integer... priorities) {
        return withArray((priority, rejectionReasonResource) -> setField("priority", priority, rejectionReasonResource), priorities);
    }
}
