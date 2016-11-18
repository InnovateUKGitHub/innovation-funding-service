package com.worth.ifs.user.builder;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.ProjectFinanceEmail;

public class ProjectFinanceEmailBuilder extends BaseBuilder<ProjectFinanceEmail, ProjectFinanceEmailBuilder> {
    public ProjectFinanceEmailBuilder(List<BiConsumer<Integer, ProjectFinanceEmail>> newActions) {
        super(newActions);
    }

    public static ProjectFinanceEmailBuilder newProjectFinanceEmail() {
        return new ProjectFinanceEmailBuilder( emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectFinanceEmailBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectFinanceEmail>> actions) {
        return new ProjectFinanceEmailBuilder(actions);
    }

    @Override
    protected ProjectFinanceEmail createInitial() {
        return new ProjectFinanceEmail();
    }

    public ProjectFinanceEmailBuilder withEmail(String... emails) {
        return withArray((email, projectFinanceEmail) -> projectFinanceEmail.setEmail(email), emails);
    }
}