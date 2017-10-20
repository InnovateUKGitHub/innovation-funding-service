package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * This service manages submitted fields that are not previously persisted.
 * It removes empty fields and groups them for associating each group as a cost.
 */
@Service
public class UnsavedFieldsManager {

    public List<List<FinanceFormField>> separateFields(List<FinanceFormField> fields) {
    	
    	 Map<String,List<FinanceFormField>> grouped = fields.stream()
    			 .filter(f -> !(StringUtils.isEmpty(f.getValue())))
    			 .collect(Collectors.groupingBy(f -> f.getFieldName()));
    	 
    	 int largest = largestValue(grouped);
    	 
    	 List<List<FinanceFormField>> result = new ArrayList<>();
    	 
    	 for(int i = 0; i < largest; i++) {
    		 
    		 List<FinanceFormField> resultEntry = new ArrayList<>();
    		 for(Entry<String, List<FinanceFormField>> entry: grouped.entrySet()) {
    			 if(i < entry.getValue().size()) {
    				 resultEntry.add(entry.getValue().get(i));
    			 }
    		 }
    		 if(!resultEntry.isEmpty()) {
    			 result.add(resultEntry);
    		 }
    	 }
    	 return result;
	}

	private int largestValue(Map<String, List<FinanceFormField>> grouped) {
		int largest = 0;
		for(Entry<String, List<FinanceFormField>> entry: grouped.entrySet()) {
			if(entry.getValue().size() > largest) {
				largest = entry.getValue().size();
			}
		}
		return largest;
	}

	public Map<String,List<FinanceFormField>> separateGroups(List<FinanceFormField> fields) {

		return fields.stream()
				.filter(f -> !(StringUtils.isEmpty(f.getValue())))
				.collect(Collectors.groupingBy(f -> f.getFieldName(), LinkedHashMap::new, Collectors.toList()));
	}
}
