// Conditional questions based on answers see: govuk-elements.herokuapp.com/form-elements/#form-toggle-content
//
// This code is a replacement for the GDS application.js code that was overly complex for what it did and didn't work on pageload
// All behaviours and html are the same as the GDS html so no need to refactor html
// Original logic: https://raw.githubusercontent.com/alphagov/govuk_elements/master/public/javascripts/application.js
IFS.core.conditionalForms = (function () {
  'use strict'
  return {
    init: function () {
      jQuery('label[data-target]:not([data-target-hide-error-messages])').each(function () {
        var label = jQuery(this)
        var dataTarget = label.attr('data-target')
        var inputEl = label.find('input[type="radio"],input[type="checkbox"]')

        // for having inverted show/hide
        var isInverted = false
        if (label.attr('data-target-inverted')) {
          isInverted = true
        }
        if (inputEl && dataTarget) {
          var groupName = inputEl.attr('name')
          // inputEl.attr('aria-controls', dataTarget)
          // execute on pageload
          IFS.core.conditionalForms.toggleVisibility(inputEl, '#' + dataTarget, isInverted)

          // execute on click
          jQuery('input[name="' + groupName + '"]').on('click', function () {
            IFS.core.conditionalForms.toggleVisibility(inputEl, '#' + dataTarget, isInverted)
          })
        }
      })
      jQuery('label[data-target][data-target-hide-error-messages]').each(function () {
        var label = jQuery(this)
        var dataTarget = label.attr('data-target')
        var inputEl = label.find('input[type="radio"],input[type="checkbox"]')

        // for having inverted show/hide
        var isInverted = false
        if (label.attr('data-target-inverted')) {
          isInverted = true
        }
        if (inputEl && dataTarget) {
          var groupName = inputEl.attr('name')
          // execute on pageload
          IFS.core.conditionalForms.toggleVisibilityHideErrors(inputEl, '#' + dataTarget, isInverted)

          // execute on click
          jQuery('input[name="' + groupName + '"]').on('click', function () {
            IFS.core.conditionalForms.toggleVisibilityHideErrors(inputEl, '#' + dataTarget, isInverted)
          })
        }
      })
    },
    toggleVisibility: function (input, target, isInverted) {
      target = jQuery(target)
      var radioStatus = input.is(':checked')
      if (isInverted) {
        radioStatus = !radioStatus
      }

      if (radioStatus) {
        // input.attr('aria-expanded', 'true')
        target.attr('aria-hidden', 'false').removeClass('js-hidden')
      } else {
        // input.attr('aria-expanded', 'false')
        target.attr('aria-hidden', 'true')
      }
    },
    toggleVisibilityHideErrors: function (input, target, isInverted) {
      target = jQuery(target)
      var radioStatus = input.is(':checked')
      if (isInverted) {
        radioStatus = !radioStatus
      }

      var form = input.closest('.form-group')

      if (radioStatus) {
        // input.attr('aria-expanded', 'true')
        target.attr('aria-hidden', 'false').removeClass('js-hidden')
        // show all error-messages from the data-target that were hidden - relies on validated field having a name that is unique on the page
        var visibleErrorMessageCount = 0
        if (input.closest('[data-target-hide-error-messages="true"][data-target]')) {
          jQuery(target).find('input').each(function (index, validatedInput) {
            jQuery(form).find('[aria-hidden="true"][data-errorfield="' + jQuery(validatedInput).prop('name') + '"]').each(function (index, errorMessage) {
              jQuery(errorMessage).attr('aria-hidden', 'false').removeClass('js-hidden')
              jQuery(errorMessage).addClass('error-message')
              visibleErrorMessageCount += 1
            })
          })
        }
        var hiddenForm = input.closest('.form-group-hide-errors')
        if (hiddenForm && visibleErrorMessageCount > 0) {
          // convert hidden error-group back to a visible error-group if any error-messages from inside the data-target hve been unhidden
          hiddenForm.removeClass('form-group-hide-errors')
          hiddenForm.addClass('error')
        }
      } else {
        // input.attr('aria-expanded', 'false')
        target.attr('aria-hidden', 'true')
        // hide all error-messages that come from within the data-target - relies on validated field having a name unique on page
        var numberOfHiddenErrorMessages = 0
        var controllingElement = input.closest('[data-target-hide-error-messages="true"][data-target]')
        var numberOfErrors = form.find('.error-message').length
        if (controllingElement) {
          jQuery(target).find('input').each(function (index, validatedInput) {
            jQuery(form).find('.error-message[data-errorfield="' + jQuery(validatedInput).prop('name') + '"]').each(function (index, errorMessage) {
              jQuery(errorMessage).removeClass('error-message')
              jQuery(errorMessage).attr('aria-hidden', 'true')
              numberOfHiddenErrorMessages += 1
            })
          })
        }
        // if fields inside data-target have provided all the error-messages hide the error-group
        if (numberOfHiddenErrorMessages === numberOfErrors) {
          form.removeClass('error')
          form.addClass('form-group-hide-errors')
        }
      }
    }
  }
})()
