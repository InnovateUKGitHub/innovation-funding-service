package org.innovateuk.ifs.category.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;

/**
 * DTO for {@code InnovationArea}s
 */
public class InnovationAreaResource extends CategoryResource {

    private Long sector;
    private String sectorName;

    @Override
    public CategoryType getType() {
        return INNOVATION_AREA;
    }

    public Long getSector() {
        return sector;
    }

    public void setSector(Long sector) {
        this.sector = sector;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public boolean isNotNone() {
        return !"None".equals(getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InnovationAreaResource that = (InnovationAreaResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(sector, that.sector)
                .append(sectorName, that.sectorName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(sector)
                .append(sectorName)
                .toHashCode();
    }
}
