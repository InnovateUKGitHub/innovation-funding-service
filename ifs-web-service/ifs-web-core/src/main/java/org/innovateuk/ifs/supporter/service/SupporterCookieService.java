package org.innovateuk.ifs.supporter.service;

import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.util.CompressedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;

@Service
public class SupporterCookieService {

    public static final String SUPPORTER_PREVIOUS_RESPONSE = "supporterPreviousResponse";

    @Autowired
    private CompressedCookieService cookieUtil;

    public void saveToSupporterPreviousResponseCookie(SupporterAssignmentResource supporterAssignmentResource, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, SUPPORTER_PREVIOUS_RESPONSE, JsonUtil.getSerializedObject(supporterAssignmentResource));
    }

    public Optional<SupporterAssignmentResource> getSupporterPreviousResponseCookie(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, SUPPORTER_PREVIOUS_RESPONSE), SupporterAssignmentResource.class));
    }

    public void removeSupporterPreviousResponseCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, SUPPORTER_PREVIOUS_RESPONSE);
    }
}
