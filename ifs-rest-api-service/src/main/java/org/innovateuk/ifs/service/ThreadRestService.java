package org.innovateuk.ifs.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

public abstract class ThreadRestService<T> extends BaseRestService {
    private final String baseURL;
    private final ParameterizedTypeReference<List<T>> listType;
    private final Class<T> type;

    public ThreadRestService(String baseURL, Class<T> type, ParameterizedTypeReference<List<T>> listType) {
        this.baseURL = baseURL;
        this.type = type;
        this.listType = listType;
    }

    public RestResult<List<T>> findAll(final Long contextClassId) {
        return getWithRestResult(baseURL + "/all/" + contextClassId, listType);

    }

    public RestResult<T> findOne(final Long id) {
        return getWithRestResult(baseURL + "/" + id, type);
    }

    public RestResult<Long> create(T thread) {
        return postWithRestResult(baseURL, thread, Long.class);
    }

    public RestResult<Void> addPost(PostResource post, Long threadId) {
        return postWithRestResult(baseURL + "/" + threadId + "/post", post, Void.class);
    }

    public RestResult<Void> close(Long threadId) {
        return postWithRestResult((baseURL + "/close/thread/" + threadId));
    }

}
