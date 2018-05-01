package org.innovateuk.ifs.review.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.review.resource.ReviewState;

import javax.persistence.Converter;

/**
 * JPA {@link Converter} for {@link ReviewState} enums.
 */
@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class ReviewStateConverter extends IdentifiableEnumConverter<ReviewState> {

    public ReviewStateConverter() {
        super(ReviewState.class);
    }

    @Override
    public Long convertToDatabaseColumn(ReviewState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public ReviewState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}