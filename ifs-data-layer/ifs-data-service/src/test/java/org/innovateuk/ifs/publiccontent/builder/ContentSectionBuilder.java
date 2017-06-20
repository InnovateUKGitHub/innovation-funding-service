package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;
import org.innovateuk.ifs.publiccontent.domain.ContentGroup;
import org.innovateuk.ifs.publiccontent.domain.ContentSection;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ContentSectionBuilder extends BaseBuilder<ContentSection, ContentSectionBuilder> {


    private ContentSectionBuilder(List<BiConsumer<Integer, ContentSection>> newMultiActions) {
        super(newMultiActions);
    }

    public static ContentSectionBuilder newContentSection() {
        return new ContentSectionBuilder(emptyList()).with(uniqueIds());
    }

    public ContentSectionBuilder withPublicContent(PublicContent publicContent) {
        return with(section -> setField("publicContent", publicContent, section));
    }

    public ContentSectionBuilder withType(PublicContentSectionType type) {
        return with(section -> setField("type", type, section));
    }

    public ContentSectionBuilder withStatus(PublicContentStatus status) {
        return with(section -> setField("status", status, section));
    }


    public ContentSectionBuilder withContentGroups(List<ContentGroup> contentGroups) {
        return with(section -> setField("contentGroups", contentGroups, section));
    }

    @Override
    protected ContentSectionBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ContentSection>> actions) {
        return new ContentSectionBuilder(actions);
    }

    @Override
    protected ContentSection createInitial() {
        return new ContentSection();
    }
}
