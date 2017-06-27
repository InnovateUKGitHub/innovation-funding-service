package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * Base controller for the multipage select controllers.
 */
public abstract class CompetitionManagementCookieController {
    public static final int SELECTION_LIMIT = 1;

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
}
