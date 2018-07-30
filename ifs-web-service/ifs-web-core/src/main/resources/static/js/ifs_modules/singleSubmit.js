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
            IFS.core.singleSubmit.disableButton(form)
          }, 0)
        }
      })
    },
    disableButton: function (form) {
      var submitButton = form.find(':button')
      submitButton.attr('disabled', 'disabled')
      submitButton.each(function () {
        if (jQuery(this).is('[data-content-on-submit]')) {
          jQuery(this).html(jQuery(this).attr('data-content-on-submit'))
        }
      })
    }
  }
})()
