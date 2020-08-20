package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.stringsListType;

@Service
public class KnowledgeBaseRestServiceImpl extends BaseRestService implements KnowledgeBaseRestService {
    private static final String ORGANISATION_BASE_URL = "/organisation/knowledge-base";

    @Override
    public RestResult<List<String>> getKnowledgeBases() {
        return getWithRestResultAnonymous(ORGANISATION_BASE_URL, stringsListType());
    }

}