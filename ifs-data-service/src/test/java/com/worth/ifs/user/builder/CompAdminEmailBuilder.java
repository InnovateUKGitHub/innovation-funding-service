package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.CompAdminEmail;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CompAdminEmailBuilder extends BaseBuilder<CompAdminEmail, CompAdminEmailBuilder> {
    public CompAdminEmailBuilder(List<BiConsumer<Integer, CompAdminEmail>> newActions) {
        super(newActions);
    }

    public static CompAdminEmailBuilder newCompAdminEmail() {
        return new CompAdminEmailBuilder( emptyList()).with(uniqueIds());
    }

    @Override
    protected CompAdminEmailBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompAdminEmail>> actions) {
        return new CompAdminEmailBuilder(actions);
    }

    @Override
    protected CompAdminEmail createInitial() {
        return new CompAdminEmail();
    }

    public CompAdminEmailBuilder withEmail(String... emails) {
        return withArray((email, compAdminEmail) -> compAdminEmail.setEmail(email), emails);
    }
}
