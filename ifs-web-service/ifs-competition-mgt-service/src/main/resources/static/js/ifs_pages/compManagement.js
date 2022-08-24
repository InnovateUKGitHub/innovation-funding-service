// TODO: divide over multiple files with purposes
IFS.competitionManagement.various = (function () {
  'use strict'
  var s
  return {
    settings: {
      decisionSelects: '.funding-decision',
      assessorFeedbackButton: '#publish-assessor-feedback',
      noJsAssessorFeedbackButton: '#publish-assessor-feedback-no-js',
      submitDecisionButton: '#publish-funding-decision',
      noJsSubmitDecisionButton: '#no-js-notify-applicants',
      noJsSaveDecisionButton: '#no-js-save-funding-decision',
      decisionForm: '#submit-funding-decision-form'
    },
    init: function () {
      s = this.settings

      jQuery(document).on('change', s.decisionSelects, IFS.competitionManagement.various.handleDecisionEnableOrDisable)
      IFS.competitionManagement.various.handleDecisionEnableOrDisable()
      IFS.competitionManagement.various.alterSubmitDecisionFormAction()
    },
    disableFundingDecisonButton: function () {
      var button = jQuery(s.submitDecisionButton)
      var modal = button.attr('data-js-modal')
      // remove the modal action and add aria-disabled and disabled styling
      button.on('click', function (event) { event.preventDefault() })
      button.removeAttr('data-js-modal').attr({ 'data-js-modal-disabled': modal, 'aria-disabled': 'true' }).addClass('disabled')
    },
    enableDecisionButton: function () {
      var button = jQuery(s.submitDecisionButton)
      var modal = button.attr('data-js-modal-disabled')
      button.off('click').removeAttr('data-js-modal-disabled aria-disabled').attr('data-js-modal', modal).removeClass('disabled')
    },
    alterSubmitDecisionFormAction: function () {
      var form = jQuery(s.decisionForm)
      var action = form.attr('action')
      form.attr('action', action + 'submit')
    },
    allSelectsDecided: function () {
      var selects = jQuery(s.decisionSelects)
      if (selects === null || selects === undefined || selects.length === 0) {
        return false
      }

      var allDecided = true
      selects.each(function () {
        var value = jQuery(this).val()
        var decided = value === 'Y' || value === 'N'
        if (!decided) {
          allDecided = false
        }
      })

      return allDecided
    },
    handleDecisionEnableOrDisable: function () {
      if (IFS.competitionManagement.various.allSelectsDecided()) {
        IFS.competitionManagement.various.enableDecisionButton()
      } else {
        IFS.competitionManagement.various.disableFundingDecisonButton()
      }
    }
  }
})()
