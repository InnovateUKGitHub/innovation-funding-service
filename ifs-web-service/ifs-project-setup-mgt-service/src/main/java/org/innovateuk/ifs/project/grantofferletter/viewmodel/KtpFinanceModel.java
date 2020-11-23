package org.innovateuk.ifs.project.grantofferletter.viewmodel;


import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class KtpFinanceModel {

    private static final int kbPartnerOverheadsRate = 46;

    private final KtpFinanceRowModel associateEmployment;
    private final KtpFinanceRowModel associateDevelopment;
    private final KtpFinanceRowModel travelAndSubsistence;
    private final KtpFinanceRowModel consumables;
    private final KtpFinanceRowModel knowledgeBaseSupervisor;
    private final KtpFinanceRowModel associateEstateCosts;
    private final KtpFinanceRowModel otherCosts;
    private final KtpFinanceRowModel additionalSupportCosts;
    private final KtpFinanceRowModel academicAndSecretarialSupport;

    private final Integer claimPercentage;

    public KtpFinanceModel(KtpFinanceRowModel associateEmployment, KtpFinanceRowModel associateDevelopment, KtpFinanceRowModel travelAndSubsistence, KtpFinanceRowModel consumables, KtpFinanceRowModel knowledgeBaseSupervisor, KtpFinanceRowModel associateEstateCosts, KtpFinanceRowModel otherCosts, KtpFinanceRowModel additionalSupportCosts, KtpFinanceRowModel academicAndSecretarialSupport, Integer claimPercentage) {
        this.associateEmployment = associateEmployment;
        this.associateDevelopment = associateDevelopment;
        this.travelAndSubsistence = travelAndSubsistence;
        this.consumables = consumables;
        this.knowledgeBaseSupervisor = knowledgeBaseSupervisor;
        this.associateEstateCosts = associateEstateCosts;
        this.otherCosts = otherCosts;
        this.additionalSupportCosts = additionalSupportCosts;
        this.academicAndSecretarialSupport = academicAndSecretarialSupport;
        this.claimPercentage = claimPercentage;
    }

    public KtpFinanceRowModel getAssociateEmployment() {
        return associateEmployment;
    }

    public KtpFinanceRowModel getAssociateDevelopment() {
        return associateDevelopment;
    }

    public KtpFinanceRowModel getTravelAndSubsistence() {
        return travelAndSubsistence;
    }

    public KtpFinanceRowModel getConsumables() {
        return consumables;
    }

    public KtpFinanceRowModel getKnowledgeBaseSupervisor() {
        return knowledgeBaseSupervisor;
    }

    public KtpFinanceRowModel getAssociateEstateCosts() {
        return associateEstateCosts;
    }

    public KtpFinanceRowModel getOtherCosts() {
        return otherCosts;
    }

    public KtpFinanceRowModel getAdditionalSupportCosts() {
        return additionalSupportCosts;
    }

    public KtpFinanceRowModel getAcademicAndSecretarialSupport() {
        return academicAndSecretarialSupport;
    }

    public Integer getClaimPercentage() {
        return claimPercentage;
    }

    public Integer getTable1TotalCost() {
        return table1Rows().stream().map(KtpFinanceRowModel::getCost).reduce(0, Integer::sum);
    }

    public Integer getTable1TotalFunding() {
        return table1Rows().stream().map(KtpFinanceRowModel::getFunding).reduce(0, Integer::sum);
    }

    public Integer getTable1TotalContribution() {
        return table1Rows().stream().map(KtpFinanceRowModel::getContribution).reduce(0, Integer::sum);
    }

    public Integer getTable2TotalCost() {
        return table2Rows().stream().map(KtpFinanceRowModel::getCost).reduce(0, Integer::sum);
    }

    public Integer getTable2TotalFunding() {
        return table2Rows().stream().map(KtpFinanceRowModel::getFunding).reduce(0, Integer::sum);
    }

    public Integer getTable2TotalContribution() {
        return table2Rows().stream().map(KtpFinanceRowModel::getContribution).reduce(0, Integer::sum);
    }

    private List<KtpFinanceRowModel> table1Rows() {
        return newArrayList(associateEmployment, associateDevelopment, travelAndSubsistence, consumables,
                knowledgeBaseSupervisor, associateEstateCosts, otherCosts, additionalSupportCosts);
    }

    private List<KtpFinanceRowModel> table2Rows() {
        return newArrayList(associateEmployment, academicAndSecretarialSupport, associateDevelopment, travelAndSubsistence,
                consumables, otherCosts);
    }

    public int getContributionToKbPartnerOverheads() {
        return (associateEmployment.getFunding() + academicAndSecretarialSupport.getFunding()) * kbPartnerOverheadsRate / 100;
    }

    public int getMaximumAmountOfGovtGrant() {
        return getTable2TotalFunding() + getContributionToKbPartnerOverheads();
    }

    public static final class KtpFinanceModelBuilder {
        private KtpFinanceRowModel associateEmployment;
        private KtpFinanceRowModel associateDevelopment;
        private KtpFinanceRowModel travelAndSubsistence;
        private KtpFinanceRowModel consumables;
        private KtpFinanceRowModel knowledgeBaseSupervisor;
        private KtpFinanceRowModel associateEstateCosts;
        private KtpFinanceRowModel otherCosts;
        private KtpFinanceRowModel additionalSupportCosts;
        private KtpFinanceRowModel academicAndSecretarialSupport;
        private Integer claimPercentage;

        private KtpFinanceModelBuilder() {
        }

        public static KtpFinanceModelBuilder aKtpFinanceModel() {
            return new KtpFinanceModelBuilder();
        }

        public KtpFinanceModelBuilder withAssociateEmployment(KtpFinanceRowModel associateEmployment) {
            this.associateEmployment = associateEmployment;
            return this;
        }

        public KtpFinanceModelBuilder withAssociateDevelopment(KtpFinanceRowModel associateDevelopment) {
            this.associateDevelopment = associateDevelopment;
            return this;
        }

        public KtpFinanceModelBuilder withTravelAndSubsistence(KtpFinanceRowModel travelAndSubsistence) {
            this.travelAndSubsistence = travelAndSubsistence;
            return this;
        }

        public KtpFinanceModelBuilder withConsumables(KtpFinanceRowModel consumables) {
            this.consumables = consumables;
            return this;
        }

        public KtpFinanceModelBuilder withKnowledgeBaseSupervisor(KtpFinanceRowModel knowledgeBaseSupervisor) {
            this.knowledgeBaseSupervisor = knowledgeBaseSupervisor;
            return this;
        }

        public KtpFinanceModelBuilder withAssociateEstateCosts(KtpFinanceRowModel associateEstateCosts) {
            this.associateEstateCosts = associateEstateCosts;
            return this;
        }

        public KtpFinanceModelBuilder withOtherCosts(KtpFinanceRowModel otherCosts) {
            this.otherCosts = otherCosts;
            return this;
        }

        public KtpFinanceModelBuilder withAdditionalSupportCosts(KtpFinanceRowModel additionalSupportCosts) {
            this.additionalSupportCosts = additionalSupportCosts;
            return this;
        }

        public KtpFinanceModelBuilder withAcademicAndSecretarialSupport(KtpFinanceRowModel academicAndSecretarialSupport) {
            this.academicAndSecretarialSupport = academicAndSecretarialSupport;
            return this;
        }

        public KtpFinanceModelBuilder withClaimPercentage(Integer claimPercentage) {
            this.claimPercentage = claimPercentage;
            return this;
        }

        public KtpFinanceModel build() {
            return new KtpFinanceModel(associateEmployment, associateDevelopment, travelAndSubsistence, consumables, knowledgeBaseSupervisor, associateEstateCosts, otherCosts, additionalSupportCosts, academicAndSecretarialSupport, claimPercentage);
        }
    }
}
