package org.innovateuk.ifs.publiccontent.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * Rest service implementation for getting public content which get wrapped in {@link org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource}
 */
@Service
public class PublicContentItemRestServiceImpl extends BaseRestService implements PublicContentItemRestService {

    private static final Log LOG = LogFactory.getLog(PublicContentItemRestServiceImpl.class);
    private static final String PUBLIC_CONTENT_ITEM_REST_URL = "/public-content/items/";

    @Override
    public RestResult<PublicContentItemPageResource> getByFilterValues(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Long> pageNumber, Optional<Long> pageSize) {
        String searchStringEncoded = null;
        try {
            searchStringEncoded = UriUtils.encode(searchString.orElse(null), "UTF8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("searchString can not be encoded");
        }

        String url = PUBLIC_CONTENT_ITEM_REST_URL + "find-by-filter?";
        url = addParamToURL(url, "innovationAreaId", String.valueOf(innovationAreaId.orElse(null)));
        url = addParamToURL(url, "searchString", searchStringEncoded);
        url = addParamToURL(url, "pageNumber", String.valueOf(pageNumber.orElse(null)));
        url = addParamToURL(url, "pageSize", String.valueOf(pageSize.orElse(null)));

        return getWithRestResult( url, PublicContentItemPageResource.class);
    }

    @Override
    public RestResult<PublicContentItemResource> getItemByCompetitionId(Long id) {
        return getWithRestResult(PUBLIC_CONTENT_ITEM_REST_URL + "all-by-competition-id/" + id, PublicContentItemResource.class);
    }

    public String addParamToURL(String url, String paramName, String paramValue) {
        if(StringUtils.isBlank(paramValue) || paramValue.equals("null")) {
            return url;
        }

        if(!url.endsWith("?")) {
            url = url + "&";
        }
        return url + paramName + "=" + paramValue;
    }
}
