package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.commons.builder.PageResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class PublicContentItemPageResourceBuilder extends PageResourceBuilder<PublicContentItemPageResource, PublicContentItemPageResourceBuilder, PublicContentItemResource> {

    private PublicContentItemPageResourceBuilder(List<BiConsumer<Integer, PublicContentItemPageResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static PublicContentItemPageResourceBuilder newPublicContentItemPageResource() {
        return new PublicContentItemPageResourceBuilder(emptyList());
    }

    @Override
    protected PublicContentItemPageResource createInitial() {
        return new PublicContentItemPageResource();
    }

    @Override
    protected PublicContentItemPageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PublicContentItemPageResource>> actions) {
        return new PublicContentItemPageResourceBuilder(actions);
    }
}
