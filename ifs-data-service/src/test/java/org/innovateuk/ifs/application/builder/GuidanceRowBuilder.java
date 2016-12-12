package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.form.domain.FormInput;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class GuidanceRowBuilder extends BaseBuilder<GuidanceRow, GuidanceRowBuilder> {

    private GuidanceRowBuilder(List<BiConsumer<Integer, GuidanceRow>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected GuidanceRowBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GuidanceRow>> actions) {
        return new GuidanceRowBuilder(actions);
    }

    public static GuidanceRowBuilder newFormInputGuidanceRow() {
        return new GuidanceRowBuilder(emptyList())
                .with(uniqueIds());
    }

    public GuidanceRowBuilder withFormInput(FormInput formInput) {
        return with(guidanceRow -> setField("formInput", formInput, guidanceRow));
    }

    public GuidanceRowBuilder withSubject(String subject) {
        return with(guidanceRow -> setField("subject", subject, guidanceRow));
    }

    public GuidanceRowBuilder withJustification(String justification) {
        return with(guidanceRow -> setField("justification", justification, guidanceRow));
    }

    public GuidanceRowBuilder withPriority(Integer priority) {
        return with(guidanceRow -> setField("priority", priority, guidanceRow));
    }

    @Override
    protected GuidanceRow createInitial() {
        return new GuidanceRow();
    }
}
