package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ContentGroupResourceBuilder extends BaseBuilder<ContentGroupResource, ContentGroupResourceBuilder> {

    private ContentGroupResourceBuilder(List<BiConsumer<Integer, ContentGroupResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static ContentGroupResourceBuilder newContentGroupResource() {
        return new ContentGroupResourceBuilder(emptyList()).with(uniqueIds());
    }

    public ContentGroupResourceBuilder withHeading(String heading) {
        return with(group -> BaseBuilderAmendFunctions.setField("heading", heading, group));
    }

    public ContentGroupResourceBuilder withContent(String content) {
        return with(group -> setField("content", content, group));
    }

    public ContentGroupResourceBuilder withPriority(Integer priority) {
        return with(group -> setField("priority", priority, group));
    }

    @Override
    protected ContentGroupResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ContentGroupResource>> actions) {
        return new ContentGroupResourceBuilder(actions);
    }

    @Override
    protected ContentGroupResource createInitial() {
        return new ContentGroupResource();
    }
}
