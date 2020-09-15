// Conditional questions based on answers see: govuk-elements.herokuapp.com/form-elements/#form-toggle-content
//
// This code is a replacement for the GDS application.js code that was overly complex for what it did and didn't work on pageload
// All behaviours and html are the same as the GDS html so no need to refactor html
// Original logic: https://raw.githubusercontent.com/alphagov/govuk_elements/master/public/javascripts/application.js
IFS.core.conditionalForms = (function () {
  'use strict'
  return {
    init: function () {
      jQuery('[data-target]').each(function () {
        var $this = jQuery(this)
        var dataTargetContainer
        var dataTarget
        var inputEl
        if ($this.is('option')) {
          dataTargetContainer = $this.closest('select')
          dataTarget = $this.attr('data-target')
          inputEl = $this
        } else {
          dataTargetContainer = $this
          dataTarget = dataTargetContainer.attr('data-target')
          inputEl = dataTargetContainer.find('input[type="radio"],input[type="checkbox"]')
        }

        // for clearing the form elements in the hidden panel
        var targetClearForm = false
        if (dataTargetContainer.attr('data-target-clear-form')) {
          targetClearForm = true
        }
        // for clearing errors from the form elements in the hidden panel
        var targetClearValidation = false
        if (dataTargetContainer.attr('data-target-clear-validation')) {
          targetClearValidation = true
        }
        // for having inverted show/hide
        var isInverted = false
        if (dataTargetContainer.attr('data-target-inverted')) {
          isInverted = true
        }
        if (inputEl && dataTarget) {
          var groupName = inputEl.attr('name')
          // execute on pageload
          IFS.core.conditionalForms.toggleVisibility(inputEl, '#' + dataTarget, targetClearForm, targetClearValidation, isInverted)

          if ($this.is('option')) {
            jQuery(dataTargetContainer).on('change', function () {
              IFS.core.conditionalForms.toggleVisibility(inputEl, '#' + dataTarget, targetClearForm, targetClearValidation, isInverted)
            })
          } else {
            // execute on click
            jQuery('input[name="' + groupName + '"]').on('click', function () {
              IFS.core.conditionalForms.toggleVisibility(inputEl, '#' + dataTarget, targetClearForm, targetClearValidation, isInverted)
            })
          }
        }
      })
    },
    toggleVisibility: function (input, target, clearForm, clearValidation, isInverted) {
      target = jQuery(target)
      var status = false
      if (input.is(':radio')) {
        status = input.is(':checked')
        if (isInverted) {
          status = !status
        }
      }

      if (input.is(':checkbox')) {
        if (isInverted) {
          // Inverted none checked
          status = !input.is(':checked')
        } else {
          // All checked (is returns true if only one matches)
          status = !input.is(':not(:checked)')
        }
      }

      if (input.is('option')) {
        status = input.is(':selected')
      }

      if (status) {
        target.attr('aria-hidden', 'false').removeClass('js-hidden')
      } else {
        target.attr('aria-hidden', 'true')
        if (clearForm) {
          target.find('input[type=radio],input[type=checkbox]').prop('checked', false)
          target.find('select, textarea, input[type=text]').val('')
          target.find('.editor').html('')
        }
        if (clearForm || clearValidation) {
          // clear validation
          var formGroup = target.find('.govuk-form-group')
          formGroup.each(function () {
            var field = jQuery(this).find('input, textarea, select')
            var requiredAttribute = 'required'
            var displayValidationMessages = IFS.core.formValidation.getMessageDisplaySetting(field, requiredAttribute)
            var errorMessage = IFS.core.formValidation.getErrorMessage(field, requiredAttribute)
            IFS.core.formValidation.setValid(field, errorMessage, displayValidationMessages)
          })
        }
      }
    }
  }
})()
