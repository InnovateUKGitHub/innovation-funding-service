IFS.core.disableSubmitUntilChecked = (function () {
  'use strict'
  var s
  return {
    settings: {
      checkBoxesAttribute: 'data-switches-button-status',
      checkBoxRevealPanel: 'data-target',
      checkRequiredInputs: '[aria-hidden="false"] input[type="text"][required][data-switches-button-status]'
    },
    init: function () {
      s = this.settings
      jQuery('body').on('change', '[' + s.checkBoxesAttribute + ']', function () {
        IFS.core.disableSubmitUntilChecked.checkButtonStates(this)
      })

      // Check input value when panel revealed
      jQuery('body').on('change', '[' + s.checkBoxRevealPanel + ']', function () {
        IFS.core.disableSubmitUntilChecked.checkButtonStates(s.checkRequiredInputs)
      })

      jQuery('[' + s.checkBoxesAttribute + ']').each(function () {
        IFS.core.disableSubmitUntilChecked.checkButtonStates(this)
      })

      // Checking that a required text input contains text when updating
      jQuery('body').on('change keyup', s.checkRequiredInputs, function (e) {
        if (e.type === 'keyup') {
          // wait until the user stops typing
          IFS.core.disableSubmitUntilChecked.checkButtonStates(this)
        }
      })
    },
    checkButtonStates: function (el) {
      var buttonSelector = jQuery(el).attr(s.checkBoxesAttribute)
      var button = jQuery(buttonSelector)
      if (button.length) {
        var condition = button.is('[data-enable-button-when]') ? button.attr('data-enable-button-when') : 'all-checked'
        var inputStatuses = IFS.core.disableSubmitUntilChecked.getInputStatus(buttonSelector)

        switch (condition) {
          case 'all-checked':
            // when all of the inputs are true
            var allChecked = inputStatuses.indexOf(false) === -1
            IFS.core.disableSubmitUntilChecked.updateButton(button, allChecked)
            break
          case 'one-checked':
            // when at least one of the inputs is true
            var oneChecked = inputStatuses.indexOf(true) !== -1
            IFS.core.disableSubmitUntilChecked.updateButton(button, oneChecked)
            break
        }
      }
    },
    getInputStatus: function (submitButton) {
      // we loop over all checkboxes which have the same attribute,
      // if all if them are checked it is true
      var buttonStates = []
      jQuery('[' + s.checkBoxesAttribute + '="' + submitButton + '"]').each(function () {
        var inst = jQuery(this)
        var state
        if (inst.is('[type="checkbox"]') || inst.is('[type="radio"]')) {
          state = inst.prop('checked')
        } else if (inst.is('select')) {
          state = inst.val() !== 'UNSET'
        } else if (inst.is('input[type="text"][required]')) {
          state = inst.val().trim().length > 0
        }
        if (typeof (state) !== 'undefined') {
          buttonStates.push(state)
        }
      })
      return buttonStates
    },
    updateButton: function (button, state) {
      if (state === true) {
        button.removeAttr('aria-disabled').removeClass('disabled').prop('disabled', false)
      } else {
        button.attr({'aria-disabled': 'true'}).addClass('disabled').prop('disabled', true)
      }
    }
  }
})()
