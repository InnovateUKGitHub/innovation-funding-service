package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.category.resource.CategoryResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Formats the set of categories associated with a competition, for display purposes.
 */
@Service
public class CategoryFormatter {

	public String format(Set<Long> categoryIds, List<? extends CategoryResource> allCategories) {
		if(categoryIds == null) {
			return "";
		}
		return allCategories.stream()
				.filter(cat -> categoryIds.stream().anyMatch(id -> cat.getId().equals(id))).map(cat -> cat.getName())
				.collect(Collectors.joining(", "));
	}

}
