package org.innovateuk.ifs.application.populator.finance.view;

import org.innovateuk.ifs.application.populator.finance.model.FinanceFormField;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * This service manages submitted fields that are not previously persisted.
 * It removes empty fields and groups them for associating each group as a cost.
 */
@Service
public class UnsavedFieldsManager {

    public Map<String, List<FinanceFormField>> separateGroups(List<FinanceFormField> fields) {

        return fields.stream()
                .filter(f -> !(StringUtils.isEmpty(f.getValue())))
                .collect(Collectors.groupingBy(FinanceFormField::getFieldName, LinkedHashMap::new, toList()));
    }
}
