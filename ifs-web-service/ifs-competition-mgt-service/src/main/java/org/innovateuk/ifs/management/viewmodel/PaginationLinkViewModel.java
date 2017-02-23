package org.innovateuk.ifs.management.viewmodel;

import org.springframework.web.util.UriComponentsBuilder;

public class PaginationLinkViewModel {
    private String path;
    private String title;

    public PaginationLinkViewModel(int page, int pageSize, long totalElements, String rootPath) {
        this.title = ((page * pageSize) + 1) + " to " + Math.min((page + 1) * pageSize, totalElements);
        this.path = UriComponentsBuilder.fromUriString(rootPath).replaceQueryParam("page", page).toUriString();
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }
}
