IFS.core.passwordToggle = (function () {
  'use strict'
  return {
    init: function () {
      jQuery('.password-toggle button').on('click', function (e) {
        e.preventDefault()
        var button = jQuery(this)
        var passwordInput = jQuery(this).prev()
        var inputType = passwordInput.attr('type')
        if (inputType === 'password') {
          passwordInput.prop('type', 'text')
          button.text('Hide')
          button.attr('aria-checked', true)
        } else {
          passwordInput.prop('type', 'password')
          button.text('Show')
          button.attr('aria-checked', false)
        }
      })
    }
  }
})()
