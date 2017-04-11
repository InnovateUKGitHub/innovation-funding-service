IFS.core.singleSubmit = (function () {
  'use strict'
  return {
    init: function () {
      jQuery(document).on('submit', '[data-single-submit]', function (e) {
        var form = jQuery(this)
        if (form.is('[data-submitting]')) {
          e.preventDefault()
        } else {
          var submitButton = form.attr('data-submitting', '').find('button[data-content-on-submit]')
          submitButton.prop('disabled', true)
          submitButton.html(submitButton.attr('data-content-on-submit'))
        }
      })
    }
  }
})()
