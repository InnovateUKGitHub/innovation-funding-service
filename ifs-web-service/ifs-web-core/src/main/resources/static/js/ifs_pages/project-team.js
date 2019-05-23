IFS.core.projectTeam = (function () {
  'use strict'
  var s // private alias to settings
  return {
    settings: {
      showForm: '[data-show-form]',
      hideForm: '[data-hide-form]'
    },
    init: function () {
      s = this.settings
      jQuery('body').on('click', s.showForm, function (e) {
        e.preventDefault()
        var formId = jQuery(this).data('show-form')
        var form = jQuery('#' + formId)
        // hide the other buttons as a safety net
        jQuery(s.showForm).not(jQuery(this)).hide()
        IFS.core.projectTeam.toggleForm(jQuery(this), form)
        form.find('input').attr('disabled', false)
      })
      jQuery('body').on('click', s.hideForm, function (e) {
        e.preventDefault()
        var formId = jQuery(this).data('hide-form')
        var form = jQuery('#' + formId)
        // show the other buttons
        jQuery(s.showForm).not(jQuery('[data-show-form=' + formId + ']')).show()
        IFS.core.projectTeam.toggleForm(jQuery('[data-show-form=' + formId + ']'), form)
        form.find('input').attr('disabled', true)
        IFS.core.projectTeam.clearErrors()
      })
    },
    toggleForm: function (button, form) {
      button.toggle()
      form.toggle()
    },
    clearErrors: function () {
      jQuery('.govuk-error-summary').remove()
      jQuery('.govuk-error-message').remove()
      jQuery('.govuk-form-group--error').removeClass('govuk-form-group--error')
      jQuery('.govuk-input--error').removeClass('govuk-input--error')
    }
  }
})()
