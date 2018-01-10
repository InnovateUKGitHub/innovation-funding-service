package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.form.domain.FormInput;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public GuidanceRowBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public GuidanceRowBuilder withFormInput(FormInput formInput) {
        return with(guidanceRow -> setField("formInput", formInput, guidanceRow));
    }

    public GuidanceRowBuilder withSubject(String... subjects) {
        return withArraySetFieldByReflection("subject", subjects);
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
