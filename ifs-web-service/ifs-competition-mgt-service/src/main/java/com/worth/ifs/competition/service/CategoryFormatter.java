package com.worth.ifs.competition.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.worth.ifs.category.resource.CategoryResource;

/**
 * Formats the set of categories associated with a competition, for display purposes.
 */
@Service
public class CategoryFormatter {

	public String format(Set<Long> categoryIds, List<CategoryResource> allCategories) {
		if(categoryIds == null) {
			return "";
		}
		return allCategories.stream()
				.filter(cat -> categoryIds.stream().anyMatch(id -> cat.getId().equals(id))).map(cat -> cat.getName())
				.collect(Collectors.joining(", "));
	}

}
