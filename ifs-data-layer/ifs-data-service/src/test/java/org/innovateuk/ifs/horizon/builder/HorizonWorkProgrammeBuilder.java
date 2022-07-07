package org.innovateuk.ifs.horizon.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.horizon.domain.HorizonWorkProgramme;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

public class HorizonWorkProgrammeBuilder extends BaseBuilder<HorizonWorkProgramme, HorizonWorkProgrammeBuilder> {

    private HorizonWorkProgrammeBuilder(List<BiConsumer<Integer, HorizonWorkProgramme>> multiActions) {
        super(multiActions);
    }

    public static HorizonWorkProgrammeBuilder newHorizonWorkProgramme() {
        return new HorizonWorkProgrammeBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected HorizonWorkProgrammeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, HorizonWorkProgramme>> actions) {
        return new HorizonWorkProgrammeBuilder(actions);
    }

    @Override
    protected HorizonWorkProgramme createInitial() {
        return new HorizonWorkProgramme();
    }

    public HorizonWorkProgrammeBuilder withId(Long... ids) {
        return withArray((id, programme) -> setField("id", id, programme), ids);
    }

    public HorizonWorkProgrammeBuilder withName(String... names) {
        return withArray((name, programme) -> programme.setName(name), names);
    }

    public HorizonWorkProgrammeBuilder withParentWorkProgramme(HorizonWorkProgramme... parentWorkProgrammeAry) {
        return withArray((parentWorkProgramme, programme) -> programme.setParentWorkProgramme(parentWorkProgramme), parentWorkProgrammeAry);
    }

    public HorizonWorkProgrammeBuilder withEnabled(boolean... enabledAry) {
        return withArray((enabled, programme) -> programme.setEnabled(enabled), enabledAry);
    }
}
