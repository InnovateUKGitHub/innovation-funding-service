package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;
import static org.innovateuk.ifs.util.JsonUtil.getSerializedObject;

/**
 * Base controller for the multipage select controllers.
 */
@Component
public abstract class CompetitionManagementCookieController<T> {
    @Autowired
    protected CookieUtil cookieUtil;

    public static final int SELECTION_LIMIT = 500;

    protected abstract String getCookieName();
    protected abstract Class<T> getFormType();

    protected List<Long> limitList(List<Long> allIds) {
        if (allIds.size() > SELECTION_LIMIT) {
            return allIds.subList(0, SELECTION_LIMIT);
        } else {
            return allIds;
        }
    }

    protected boolean limitIsExceeded(long amountOfIds) {
        return amountOfIds > SELECTION_LIMIT;
    }

    protected ObjectNode createFailureResponse() {
        return createJsonObjectNode(-1, false, false);
    }

    protected ObjectNode createSuccessfulResponseWithSelectionStatus(int selectionCount, boolean allSelected, boolean limitExceeded) {
        return createJsonObjectNode(selectionCount, allSelected, limitExceeded);
    }

    protected ObjectNode createJsonObjectNode(int selectionCount, boolean allSelected, boolean limitExceeded) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("selectionCount", selectionCount);
        node.put("allSelected", allSelected);
        node.put("limitExceeded", limitExceeded);

        return node;
    }

    protected Optional<T> getSelectionFormFromCookie(HttpServletRequest request, long competitionId) {
        String selectionCookieJson = cookieUtil.getCompressedCookieValue(request, format("%s_comp_%s", getCookieName(), competitionId));
        if (isNotBlank(selectionCookieJson)) {
            return Optional.ofNullable(getObjectFromJson(selectionCookieJson, getFormType()));
        } else {
            return Optional.empty();
        }
    }

    protected void saveFormToCookie(HttpServletResponse response, long competitionId, T selectionForm) {
        cookieUtil.saveToCompressedCookie(response, format("%s_comp_%s", getCookieName(), competitionId), getSerializedObject(selectionForm));
    }

    protected void removeCookie(HttpServletResponse response, long competitionId) {
        cookieUtil.removeCookie(response, format("%s_comp_%s", getCookieName(), competitionId));
    }
}
