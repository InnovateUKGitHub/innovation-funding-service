package org.innovateuk.ifs.project.queries.viewmodel;

import org.innovateuk.ifs.notesandqueries.resource.thread.ThreadResource;

import java.util.List;

public class FinanceChecksQueriesQueryViewModel extends ThreadResource{
    public List<FinanceChecksQueriesPostViewModel> getViewModelPosts() {
        return viewModelPosts;
    }

    public void setViewModelPosts(List<FinanceChecksQueriesPostViewModel> posts) {
        this.viewModelPosts = posts;
    }

    private List<FinanceChecksQueriesPostViewModel> viewModelPosts;
}
