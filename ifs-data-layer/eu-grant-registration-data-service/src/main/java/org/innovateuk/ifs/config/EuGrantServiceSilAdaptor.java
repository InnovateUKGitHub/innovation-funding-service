package org.innovateuk.ifs.config;

import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.service.HttpHeadersUtils.getJSONHeaders;

@Component("sil_adaptor")
public class EuGrantServiceSilAdaptor extends AbstractRestTemplateAdaptor {
    @Override
    public HttpHeaders getHeaders() {
        return getJSONHeaders();
    }
}
