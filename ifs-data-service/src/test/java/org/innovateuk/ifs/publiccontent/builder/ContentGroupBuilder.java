package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.publiccontent.domain.ContentGroup;
import org.innovateuk.ifs.publiccontent.domain.ContentSection;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ContentGroupBuilder extends BaseBuilder<ContentGroup, ContentGroupBuilder> {


    private ContentGroupBuilder(List<BiConsumer<Integer, ContentGroup>> newMultiActions) {
        super(newMultiActions);
    }

    public static ContentGroupBuilder newContentGroup() {
        return new ContentGroupBuilder(emptyList()).with(uniqueIds());
    }

    public ContentGroupBuilder withContentSection(ContentSection contentSection) {
        return with(group -> setField("contentSection", contentSection, group));
    }

    public ContentGroupBuilder withHeading(String heading) {
        return with(group -> setField("heading", heading, group));
    }

    public ContentGroupBuilder withContent(String content) {
        return with(group -> setField("content", content, group));
    }

    public ContentGroupBuilder withPriority(Integer priority) {
        return with(group -> setField("priority", priority, group));
    }

    public ContentGroupBuilder withFileEntry(FileEntry fileEntry) {
        return with(group -> setField("fileEntry", fileEntry, group));
    }


    @Override
    protected ContentGroupBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ContentGroup>> actions) {
        return new ContentGroupBuilder(actions);
    }

    @Override
    protected ContentGroup createInitial() {
        return new ContentGroup();
    }
}
