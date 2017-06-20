package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.publiccontent.domain.ContentEvent;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ContentEventBuilder extends BaseBuilder<ContentEvent, ContentEventBuilder> {


    private ContentEventBuilder(List<BiConsumer<Integer, ContentEvent>> newMultiActions) {
        super(newMultiActions);
    }

    public static ContentEventBuilder newContentEvent() {
        return new ContentEventBuilder(emptyList()).with(uniqueIds());
    }

    public ContentEventBuilder withId(Long id) {
        return with(contentEvent -> setField("id", id, contentEvent));
    }

    public ContentEventBuilder withDate(ZonedDateTime date) {
        return with(contentEvent -> setField("date", date, contentEvent));
    }

    public ContentEventBuilder withContent(String content) {
        return with(contentEvent -> setField("content", content, contentEvent));
    }

    public ContentEventBuilder withPublicContent(PublicContent publicContent) {
        return with(contentEvent -> setField("publicContent", publicContent, contentEvent));
    }

    @Override
    protected ContentEventBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ContentEvent>> actions) {
        return new ContentEventBuilder(actions);
    }

    @Override
    protected ContentEvent createInitial() {
        return new ContentEvent();
    }
}
