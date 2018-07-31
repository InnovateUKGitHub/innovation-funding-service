IFS.core.singleSubmit = (function () {
  'use strict'
  return {
    init: function () {
      jQuery(document).on('submit', 'form', function (e) {
        var form = jQuery(this)
        if (form.is('[data-submitting]')) {
          e.preventDefault()
        } else {
          form.attr('data-submitting', '')
          setTimeout(function () {
            IFS.core.singleSubmit.disableButton(form, e)
          }, 0)
        }
      })
    },
    disableButton: function (form, e) {
      if (e.isDefaultPrevented()) {
        // another event handler has stopped the submit event.
        form.removeAttr('data-submitting')
      } else {
        var submitButton = form.find(':button')
        submitButton.attr('disabled', 'disabled')
        submitButton.filter('.button:not(.button-secondary)').html('Please wait...')
      }
    }
  }
})()
