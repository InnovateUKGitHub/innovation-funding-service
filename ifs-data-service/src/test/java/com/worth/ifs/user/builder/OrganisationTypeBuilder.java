package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.resource.OrganisationTypeEnum;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for OrganisationResource entities.
 */
public class OrganisationTypeBuilder extends BaseBuilder<OrganisationType, OrganisationTypeBuilder> {

    private OrganisationTypeBuilder(List<BiConsumer<Integer, OrganisationType>> multiActions) {
        super(multiActions);
    }

    public static OrganisationTypeBuilder newOrganisationType() {
        return new OrganisationTypeBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedNames("OrganisationType "));
    }

    @Override
    protected OrganisationTypeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationType>> actions) {
        return new OrganisationTypeBuilder(actions);
    }

    @Override
    protected OrganisationType createInitial() {
        return new OrganisationType();
    }

    public OrganisationTypeBuilder withOrganisationType(OrganisationTypeEnum... organisationTypeEnums) {
        return withArray((organisationTypeEnum, organisationType) -> {
            setField("id", organisationTypeEnum.getOrganisationTypeId(), organisationType);
            setField("name", organisationTypeEnum.name(), organisationType);
        }, organisationTypeEnums);
    }
}
