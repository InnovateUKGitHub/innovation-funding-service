package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ContentEventResourceBuilder extends BaseBuilder<ContentEventResource, ContentEventResourceBuilder> {

    private ContentEventResourceBuilder(List<BiConsumer<Integer, ContentEventResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static ContentEventResourceBuilder newContentEventResource() {
        return new ContentEventResourceBuilder(emptyList()).with(uniqueIds());
    }

    public ContentEventResourceBuilder withId(Long id) {
        return with(event -> setField("id", id, event));
    }

    public ContentEventResourceBuilder withPublicContent(Long publicContent) {
        return with(event -> setField("publicContent", publicContent, event));
    }

    public ContentEventResourceBuilder withDate(ZonedDateTime date) {
        return with(event -> setField("date", date, event));
    }

    public ContentEventResourceBuilder withContent(String content) {
        return with(event -> setField("content", content, event));
    }

    @Override
    protected ContentEventResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ContentEventResource>> actions) {
        return new ContentEventResourceBuilder(actions);
    }

    @Override
    protected ContentEventResource createInitial() {
        return new ContentEventResource();
    }
}
