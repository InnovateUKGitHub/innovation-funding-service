package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

/**
 * Builder for {@link ProfileSkillsResource}s.
 */
public class ProfileSkillsResourceBuilder extends ProfileSkillsBaseResourceBuilder<ProfileSkillsResource, ProfileSkillsResourceBuilder> {

    private ProfileSkillsResourceBuilder(List<BiConsumer<Integer, ProfileSkillsResource>> multiActions) {
        super(multiActions);
    }

    public static ProfileSkillsResourceBuilder newProfileSkillsResource() {
        return new ProfileSkillsResourceBuilder(emptyList());
    }

    @Override
    protected ProfileSkillsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProfileSkillsResource>> actions) {
        return new ProfileSkillsResourceBuilder(actions);
    }

    @Override
    protected ProfileSkillsResource createInitial() {
        return createDefault(ProfileSkillsResource.class);
    }

    public ProfileSkillsResourceBuilder withInnovationAreas(List<InnovationAreaResource>... innovationAreaLists) {
        return withArray((innovationAreaList, profileSkillsResource) -> setField("innovationAreas", innovationAreaList,
                profileSkillsResource), innovationAreaLists);
    }
}
