IFS.core.submitNotification = (function () {
  'use strict'
  var s // private alias to settings
  return {
    settings: {
      element: '[data-submitted-text]'
    },
    init: function () {
      s = this.settings
      var submitButton = jQuery(s.element)
      var form = submitButton.closest('form')
      form.submit(function () {
        var submittedText = submitButton.data('submittedText')
        submitButton.text(submittedText)
      })
    }
  }
})()
