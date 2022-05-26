package org.innovateuk.ifs.application.forms.hecpcosts.form;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class HorizonEuropeGuaranteeCostsForm {

    private BigInteger labour;
    private BigInteger hecpIndirectCosts;
    private BigInteger equipment;
    private BigInteger otherGoods;
    private BigInteger subcontracting;
    private BigInteger travel;
    private BigInteger other;

    /* view logic. */
    public BigInteger getOrganisationFinanceTotal() {
        return labour
                .add(hecpIndirectCosts)
                .add(equipment)
                .add(otherGoods)
                .add(subcontracting)
                .add(travel)
                .add(other);
    }
}