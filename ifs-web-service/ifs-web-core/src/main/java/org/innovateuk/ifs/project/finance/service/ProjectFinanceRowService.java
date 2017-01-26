package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

public interface ProjectFinanceRowService {
    ValidationMessages add(Long projectFinanceId, Long questionId, FinanceRowItem costItem);
    FinanceRowItem addWithoutPersisting(Long projectFinanceId, Long questionId);
}
