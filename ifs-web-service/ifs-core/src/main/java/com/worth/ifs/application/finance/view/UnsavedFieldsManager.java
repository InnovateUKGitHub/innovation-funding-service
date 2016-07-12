package com.worth.ifs.application.finance.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.worth.ifs.application.finance.model.FinanceFormField;

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
    	 
    	 List<List<FinanceFormField>> result = new ArrayList<>();
    	 
    	 for(int i = 0; i < Arrays.asList(grouped.entrySet()).get(0).size(); i++) {
    		 
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
}
