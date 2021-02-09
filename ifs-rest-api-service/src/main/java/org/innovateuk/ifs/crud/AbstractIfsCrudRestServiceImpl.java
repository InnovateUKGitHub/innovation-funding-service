package org.innovateuk.ifs.crud;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

public abstract class AbstractIfsCrudRestServiceImpl<Resource, Id> extends BaseRestService implements IfsCrudRestService<Resource, Id> {

    @Override
    public RestResult<Resource> get(Id id) {
        return getWithRestResult(getBaseUrl() + "/" + id, getResourceClass());
    }

    @Override
    public RestResult<List<Resource>> get(List<Id> ids) {
        return getWithRestResult(getBaseUrl() + "?ids=" + joinIds(ids), getListTypeReference());
    }

    @Override
    public RestResult<Void> update(Id id, Resource resource) {
        return putWithRestResult(getBaseUrl() + "/" + id, resource, Void.class);
    }

    @Override
    public RestResult<Void> delete(Id id) {
        return deleteWithRestResult(getBaseUrl() + "/" + id);
    }

    @Override
    public RestResult<Resource> create(Resource resource) {
        return postWithRestResult(getBaseUrl(), resource, getResourceClass());
    }

    protected abstract String getBaseUrl();

    protected abstract Class<Resource> getResourceClass();

    protected abstract ParameterizedTypeReference<List<Resource>> getListTypeReference();

    private String joinIds(List<Id> ids) {
        return ids.stream().map(Objects::toString).collect(joining());
    }
}
