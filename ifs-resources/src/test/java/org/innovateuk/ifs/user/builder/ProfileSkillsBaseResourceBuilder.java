package org.innovateuk.ifs.user.builder;


import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.resource.BusinessType;
import org.innovateuk.ifs.user.resource.ProfileSkillsBaseResource;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class ProfileSkillsBaseResourceBuilder<T extends ProfileSkillsBaseResource, S extends ProfileSkillsBaseResourceBuilder> extends BaseBuilder<T, S> {

    protected ProfileSkillsBaseResourceBuilder(List<BiConsumer<Integer, T>> newActions) {
        super(newActions);
    }

    public S withUser(Long... users) {
        return withArraySetFieldByReflection("user", users);
    }

    public S withSkillsAreas(String... skillsAreas) {
        return withArraySetFieldByReflection("skillsAreas", skillsAreas);
    }

    public S withBusinessType(BusinessType... businessTypes) {
        return withArraySetFieldByReflection("businessType", businessTypes);
    }

}