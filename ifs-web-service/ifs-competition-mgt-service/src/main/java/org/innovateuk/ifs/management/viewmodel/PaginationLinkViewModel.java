package org.innovateuk.ifs.management.viewmodel;

import org.springframework.web.util.UriComponentsBuilder;

public class PaginationLinkViewModel {
    private String path;
    private String title;

    public PaginationLinkViewModel(int page, int pageSize, long totalElements, String rootPath) {
        this.title = (1 + (page * pageSize)) + " to " + Math.min((1 + page) * pageSize, totalElements);
        this.path = UriComponentsBuilder.fromUriString(rootPath).replaceQueryParam("page", page).toUriString();
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }
}
