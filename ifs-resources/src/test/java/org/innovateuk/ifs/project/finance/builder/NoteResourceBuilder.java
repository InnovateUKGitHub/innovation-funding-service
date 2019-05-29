package org.innovateuk.ifs.project.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class NoteResourceBuilder extends BaseBuilder<NoteResource, NoteResourceBuilder> {

    private NoteResourceBuilder(List<BiConsumer<Integer, NoteResource>> multiActions) {
        super(multiActions);
    }

    public static NoteResourceBuilder newNoteResource() {
        return new NoteResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected NoteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, NoteResource>> actions) {
        return new NoteResourceBuilder(actions);
    }

    @Override
    protected NoteResource createInitial() {
        return new NoteResource();
    }

    public final NoteResourceBuilder withId(Long... ids) {
        return withArray((id, note) -> setField("id", id, note), ids);
    }

    public final NoteResourceBuilder withContextClassPk(Long... pks) {
        return withArray((pk, note) -> setField("contextClassPk", pk, note), pks);
    }

    @SafeVarargs
    public final NoteResourceBuilder withPosts(List<PostResource>... posts) {
        return withArray((post, note) -> setField("posts", post, note), posts);
    }

    public final NoteResourceBuilder withTitle(String... titles) {
        return withArray((title, note) -> setField("title", title, note), titles);
    }

    public final NoteResourceBuilder withCreatedOn(ZonedDateTime... dates) {
        return withArray((date, note) -> setField("createdOn", date, note), dates);
    }

}
