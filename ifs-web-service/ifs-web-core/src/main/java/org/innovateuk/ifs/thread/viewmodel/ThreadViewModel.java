package org.innovateuk.ifs.thread.viewmodel;

import org.innovateuk.ifs.notesandqueries.resource.thread.ThreadResource;

import java.util.List;

public class ThreadViewModel extends ThreadResource {
    public List<ThreadPostViewModel> getViewModelPosts() {
        return viewModelPosts;
    }

    public void setViewModelPosts(List<ThreadPostViewModel> posts) {
        this.viewModelPosts = posts;
    }

    private List<ThreadPostViewModel> viewModelPosts;
}
