IFS.core.passwordToggle = (function () {
  'use strict'
  var fieldName
  return {
    init: function () {
      var button = jQuery('.password-toggle button')
      var passwordInput = button.prev()
      var form = button.closest('form')
      var submitButton = form.find('[type=submit]')
      fieldName = passwordInput.prop('name')
      button.on('click', function (e) {
        e.preventDefault()
        var inputType = passwordInput.prop('type')
        if (inputType === 'password') {
          IFS.core.passwordToggle.showPassword(button, passwordInput, submitButton, form)
        } else {
          IFS.core.passwordToggle.hidePassword(button, passwordInput)
        }
      })
    },
    showPassword: function (button, passwordInput, submitButton, form) {
      form.off('submit')
      passwordInput.prop('type', 'text')
      passwordInput.prop('name', Math.random().toString(36).replace(/[^a-z]+/g, ''))
      passwordInput.focus()
      button.text('Hide')
      button.attr('aria-checked', true)
      submitButton.on('click', function () {
        passwordInput.prop('name', fieldName)
        passwordInput.prop('type', 'password')
      })
    },
    hidePassword: function (button, passwordInput) {
      passwordInput.prop('type', 'password')
      passwordInput.prop('name', fieldName)
      passwordInput.focus()
      button.text('Show')
      button.attr('aria-checked', false)
    }
  }
})()
