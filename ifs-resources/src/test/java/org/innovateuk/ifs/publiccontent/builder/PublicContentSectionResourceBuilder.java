package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class PublicContentSectionResourceBuilder extends BaseBuilder<PublicContentSectionResource, PublicContentSectionResourceBuilder> {

    private PublicContentSectionResourceBuilder(List<BiConsumer<Integer, PublicContentSectionResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static PublicContentSectionResourceBuilder newPublicContentSectionResource() {
        return new PublicContentSectionResourceBuilder(emptyList()).with(uniqueIds());
    }

    public PublicContentSectionResourceBuilder withPublicContent(Long publicContent) {
        return with(section -> setField("publicContent", publicContent, section));
    }

    public PublicContentSectionResourceBuilder withType(PublicContentSectionType type) {
        return with(section -> setField("type", type, section));
    }

    public PublicContentSectionResourceBuilder withStatus(PublicContentStatus status) {
        return with(section -> setField("status", status, section));
    }

    public PublicContentSectionResourceBuilder withContentGroups(List<ContentGroupResource> contentGroups) {
        return with(section -> setField("contentGroups", contentGroups, section));
    }

    @Override
    protected PublicContentSectionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PublicContentSectionResource>> actions) {
        return new PublicContentSectionResourceBuilder(actions);
    }

    @Override
    protected PublicContentSectionResource createInitial() {
        return new PublicContentSectionResource();
    }
}
