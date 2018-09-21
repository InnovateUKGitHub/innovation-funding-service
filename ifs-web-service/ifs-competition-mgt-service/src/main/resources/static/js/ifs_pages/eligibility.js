IFS.competitionManagement.eligibility = (function () {
  'use strict'
  return {
    init: function () {
      IFS.competitionManagement.eligibility.setOverrideFundingRulesVisibility()
      IFS.competitionManagement.eligibility.setFundingLevelRCVisibility()
      jQuery(document).on('change', 'input[name="researchCategoriesApplicable"]', function () {
        IFS.competitionManagement.eligibility.handleResearchCategoriesApplicableChange(this)
      })
      jQuery(document).on('change', 'input[name="overrideFundingRules"]', function () {
        IFS.competitionManagement.eligibility.handleOverrideFundingRulesChange(this)
      })
    },
    handleResearchCategoriesApplicableChange: function (el) {
      var researchCategoriesApplicable = jQuery(el)
      var status = researchCategoriesApplicable.val() === 'true'

      IFS.competitionManagement.eligibility.setOverrideFundingRulesVisibility()
      IFS.competitionManagement.eligibility.setFundingLevelRCVisibility()

      if (status) {
        jQuery('#funding-level').find('select').val('')
      } else {
        jQuery('#override-funding-rules').find('input[name="overrideFundingRules"]').prop('checked', false)
      }
    },
    handleOverrideFundingRulesChange: function (el) {
      var handleOverrideFundingRules = jQuery(el)
      var status = handleOverrideFundingRules.val() === 'true'

      var fundingLevelContainer = jQuery('#funding-level')

      if (status) {
        fundingLevelContainer.attr('aria-hidden', 'false')
      } else {
        fundingLevelContainer.attr('aria-hidden', 'true')
        fundingLevelContainer.find('select').val('')
      }
    },
    setOverrideFundingRulesVisibility: function () {
      var researchCategoriesFalseIsChecked = jQuery('input[name="researchCategoriesApplicable"][value="false"]').is(':checked')
      var overrideFundingRulesTrueIsChecked = jQuery('input[name="overrideFundingRules"][value="true"]').is(':checked')
      var overrideFundingRulesContainer = jQuery('#override-funding-rules')
      var fundingLevelContainer = jQuery('#funding-level')

      if (researchCategoriesFalseIsChecked) {
        overrideFundingRulesContainer.attr('aria-hidden', 'true')
      } else {
        overrideFundingRulesContainer.attr('aria-hidden', 'false')
      }

      if (researchCategoriesFalseIsChecked || overrideFundingRulesTrueIsChecked) {
        fundingLevelContainer.attr('aria-hidden', 'false')
      } else {
        fundingLevelContainer.attr('aria-hidden', 'true')
      }
    },
    setFundingLevelRCVisibility: function () {
      var researchCategoriesFalseIsChecked = jQuery('input[name="researchCategoriesApplicable"][value="false"]').is(':checked')
      var fundingLevelContainer = jQuery('#funding-level-rc')

      if (researchCategoriesFalseIsChecked) {
        fundingLevelContainer.attr('aria-hidden', 'false')
      } else {
        fundingLevelContainer.attr('aria-hidden', 'true')
      }
    }
  }
})()
