// Toggle a form submit disabled state using checkboxes
IFS.assessment.formCheckboxToggleSubmit = (function () {
  'use strict'
  var s // private alias to settings

  return {
    settings: {
      element: '[data-toggle-submit]'
    },
    init: function () {
      // initialise the form submit state and bind event handlers
      s = this.settings
      var checkboxes = jQuery(s.element)

      // run on init to check for any initials states of checkboxes
      IFS.assessment.formCheckboxToggleSubmit.toggleState(checkboxes)

      jQuery('body').on('click', checkboxes, function (e) {
        IFS.assessment.formCheckboxToggleSubmit.toggleState(checkboxes)
      })
    },
    toggleState: function (checkboxes) {
      // check the state of checkboxes and toggle disabled button
      var submitButton
      var submitDisabled = true

      for (var i = 0; i < checkboxes.length; i++) {
        submitButton = jQuery(checkboxes[i]).data('toggle-submit')

        if (checkboxes[i].checked) {
          submitDisabled = false
        }
      }

      if (submitDisabled) {
        // disable button
        jQuery(submitButton).addClass('disabled').prop('disabled', true)
      } else {
        // enable button
        jQuery(submitButton).removeClass('disabled').removeAttr('disabled')
      }
    }
  }
})()
