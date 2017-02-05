package org.innovateuk.thread.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.threads.resource.PostResource;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

public abstract class ThreadRestService<T> extends BaseRestService {
    private final String baseURL;
    private final ParameterizedTypeReference<List<T>> type;

    public ThreadRestService(String baseURL, ParameterizedTypeReference<List<T>> type) {
        this.baseURL = baseURL;
        this.type = type;
    }

    public RestResult<List<T>> allThreads(final Long projectFinanceId) {
        return getWithRestResult(baseURL + "/" + projectFinanceId, type);

    }

    public RestResult<Void> createThread(T thread) {
        return postWithRestResult(baseURL, thread, Void.class);
    }

    public RestResult<Void> addPost(PostResource post, Long threadId) {
        return postWithRestResult(baseURL + "/" + threadId + "/post", post, Void.class);
    }

}
