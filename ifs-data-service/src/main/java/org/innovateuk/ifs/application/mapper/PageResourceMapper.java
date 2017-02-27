package org.innovateuk.ifs.application.mapper;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import org.innovateuk.ifs.commons.resource.PageResource;

public abstract class PageResourceMapper<V, T> {

	protected <U extends PageResource<T>> U mapFields(Page<V> source, U result) {
		result.setNumber(source.getNumber());
		result.setSize(source.getSize());
		result.setTotalElements(source.getTotalElements());
		result.setTotalPages(source.getTotalPages());
		result.setContent(source.getContent().stream().map(contentElementConverter()).collect(Collectors.toList()));
		return result;
	}
	
	protected abstract Function<V, T> contentElementConverter();
	
}
