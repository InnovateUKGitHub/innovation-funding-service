package org.innovateuk.ifs.publiccontent.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Rest service implementation for getting public content which get wrapped in {@link org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource}
 */
@Service
public class PublicContentItemRestServiceImpl extends BaseRestService implements PublicContentItemRestService {

    private static final Log LOG = LogFactory.getLog(PublicContentItemRestServiceImpl.class);
    private static final String PUBLIC_CONTENT_ITEM_REST_URL = "/public-content/items/";

    @Override
    public RestResult<PublicContentItemPageResource> getByFilterValues(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Integer> pageNumber, Integer pageSize) {

        try {
            UriUtils.encode(searchString.orElse(null), "UTF8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("searchString can not be encoded", e);
        }

        String url = PUBLIC_CONTENT_ITEM_REST_URL + "find-by-filter";
        URIBuilder builder = new URIBuilder();
        builder.setPath(url);
        innovationAreaId.ifPresent(value -> addParamToURL(builder, "innovationAreaId", String.valueOf(value)));
        searchString.ifPresent(value -> addParamToURL(builder, "searchString", value));
        pageNumber.ifPresent(value -> addParamToURL(builder, "pageNumber", String.valueOf(value)));
        addParamToURL(builder, "pageSize", String.valueOf(pageSize));

        String uriString = null;

        try {
            URI uri = builder.build();
            uriString = uri.toString();
        } catch (URISyntaxException e) {
            LOG.error("URI cannot be built", e);
        }

        return getWithRestResultAnonymous( uriString, PublicContentItemPageResource.class);
    }

    @Override
    public RestResult<PublicContentItemResource> getItemByCompetitionId(Long id) {
        return getWithRestResultAnonymous(PUBLIC_CONTENT_ITEM_REST_URL + "by-competition-id/" + id, PublicContentItemResource.class);
    }

    public void addParamToURL(URIBuilder builder, String paramName, String paramValue) {
        if(!(StringUtils.isBlank(paramValue) || paramValue.equals("null"))) {
            builder.addParameter(paramName, paramValue);
        }
    }
}
