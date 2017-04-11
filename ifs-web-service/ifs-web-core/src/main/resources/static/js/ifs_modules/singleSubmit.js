IFS.core.singleSubmit = (function () {
  'use strict'
  return {
    init: function () {
      jQuery(document).on('submit', '[data-single-submit]', function (e) {
        var form = jQuery(this)
        if (form.is('[data-submitting]')) {
          e.preventDefault()
        } else {
          form.attr('data-submitting', '')
          var submitButton = form.find('button[data-content-on-submit]')
          submitButton.attr('disabled', 'disabled')
          submitButton.html(submitButton.attr('data-content-on-submit'))
        }
      })
    }
  }
})()
