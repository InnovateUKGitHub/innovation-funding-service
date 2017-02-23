package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class KeywordBuilder extends BaseBuilder<Keyword, KeywordBuilder> {


    private KeywordBuilder(List<BiConsumer<Integer, Keyword>> newMultiActions) {
        super(newMultiActions);
    }

    public static KeywordBuilder newKeyword() {
        return new KeywordBuilder(emptyList()).with(uniqueIds());
    }

    public KeywordBuilder withId(Long id) {
        return with(keyword -> setField("id", id, keyword));
    }

    public KeywordBuilder withPublicContent(PublicContent publicContent) {
        return with(keyword -> setField("publicContent", publicContent, keyword));
    }

    public KeywordBuilder withKeyword(String keywordString) {
        return with(keyword -> setField("keyword", keywordString, keyword));
    }

    @Override
    protected KeywordBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Keyword>> actions) {
        return new KeywordBuilder(actions);
    }

    @Override
    protected Keyword createInitial() {
        return new Keyword();
    }
}
