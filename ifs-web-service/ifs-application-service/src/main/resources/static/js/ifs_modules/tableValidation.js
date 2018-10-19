IFS.application.tableValidation = (function () {
  'use strict'
  return {
    settings: {
    },
    init: function () {
      IFS.application.tableValidation.checkForErrors()
    },
    checkForErrors: function () {
      var formInTable = jQuery('.form-in-table').length > 0
      var formInTableErrors = jQuery('.form-in-table').find('.govuk-input--error').length

      if (formInTable && formInTableErrors > 0) {
        jQuery('.form-in-table').addClass('govuk-form-group--error')
      }
    }
  }
})()
