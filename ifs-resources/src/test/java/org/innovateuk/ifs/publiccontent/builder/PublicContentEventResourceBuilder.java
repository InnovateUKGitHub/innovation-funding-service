package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentEventResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class PublicContentEventResourceBuilder extends BaseBuilder<PublicContentEventResource, PublicContentEventResourceBuilder> {

    private PublicContentEventResourceBuilder(List<BiConsumer<Integer, PublicContentEventResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static PublicContentEventResourceBuilder newPublicContentEventResource() {
        return new PublicContentEventResourceBuilder(emptyList()).with(uniqueIds());
    }

    public PublicContentEventResourceBuilder withId(Long id) {
        return with(event -> setField("id", id, event));
    }

    public PublicContentEventResourceBuilder withPublicContent(Long publicContent) {
        return with(event -> setField("publicContent", publicContent, event));
    }

    public PublicContentEventResourceBuilder withDate(LocalDateTime date) {
        return with(event -> setField("date", date, event));
    }

    public PublicContentEventResourceBuilder withContent(String content) {
        return with(event -> setField("content", content, event));
    }

    @Override
    protected PublicContentEventResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PublicContentEventResource>> actions) {
        return new PublicContentEventResourceBuilder(actions);
    }

    @Override
    protected PublicContentEventResource createInitial() {
        return new PublicContentEventResource();
    }
}
