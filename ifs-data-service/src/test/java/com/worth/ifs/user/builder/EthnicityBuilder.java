package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.Ethnicity;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.emptyList;

public class EthnicityBuilder extends BaseBuilder<Ethnicity, EthnicityBuilder> {

    private EthnicityBuilder(List<BiConsumer<Integer, Ethnicity>> multiActions) {
        super(multiActions);
    }

    public static EthnicityBuilder newEthnicity() {
        return new EthnicityBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected EthnicityBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Ethnicity>> actions) {
        return new EthnicityBuilder(actions);
    }

    public EthnicityBuilder withId(Long... ids) {
        return withArray((id, ethnicity) -> setField("id", id, ethnicity) , ids);
    }

    public EthnicityBuilder withName(String... names) {
        return withArray((name, ethnicity) -> setField("name", name, ethnicity), names);
    }

    public EthnicityBuilder withDescription(String... descriptions) {
        return withArray((description, ethnicity) -> setField("description", description, ethnicity), descriptions);
    }

    public EthnicityBuilder withPriority(Integer... priorities) {
        return withArray((priority, ethnicity) -> setField("priority", priority, ethnicity), priorities);
    }

    public EthnicityBuilder withActive(Boolean... actives) {
        return withArray((active, ethnicity) -> setField("active", active, ethnicity), actives);
    }

    @Override
    protected Ethnicity createInitial() {
        return new Ethnicity();
    }
}
