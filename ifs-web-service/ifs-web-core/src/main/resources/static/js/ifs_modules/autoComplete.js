IFS.core.autoComplete = (function () {
  'use strict'
  var s // private alias to settings
  return {
    settings: {
      autoCompleteWrapper: '.autocomplete__wrapper',
      autoCompleteElement: '[data-auto-complete]',
      autoCompleteSubmitElement: '[data-auto-complete-submit]',
      menuLimit: 20,
      autoCompletePlugin: accessibleAutocomplete // eslint-disable-line
    },
    init: function () {
      s = this.settings
      var autoCompleteElement = jQuery(s.autoCompleteElement)
      if (autoCompleteElement.length > 0) {
        autoCompleteElement.each(function () {
          IFS.core.autoComplete.initAutoCompletePlugin(jQuery(this))
        })
        autoCompleteElement.closest('form').submit(function () {
          autoCompleteElement.each(function () {
            var autoComplete = jQuery(this).parent().find('.autocomplete__input')
            if (autoComplete.val() === '') {
              jQuery(this).val('')
            }
          })
        })
      }
    },
    initAutoCompletePlugin: function (element) {
      var autoCompleteSubmitElement = jQuery(s.autoCompleteSubmitElement)
      if (element.length > 0) {
        autoCompleteSubmitElement.prop('disabled', true)
        jQuery(document).on('keydown', s.autoCompleteWrapper, function (e) {
          if (e.which !== 13 && e.which !== 32) {
            autoCompleteSubmitElement.prop('disabled', true)
          }
        })
        var showAllValues = element.children('option').length <= s.menuLimit
        var required = element.data('required-errormessage')
        s.autoCompletePlugin.enhanceSelectElement({
          autoselect: false,
          selectElement: element[0],
          showAllValues: showAllValues,
          defaultValue: '',
          confirmOnBlur: true,
          displayMenu: 'overlay',
          required: required,
          onConfirm: function (confirmed) {
            if (confirmed) {
              var selectedUserId = element.children('option').filter(function () {
                return jQuery(this).text() === confirmed
              }).val()
              element.val(selectedUserId)
              autoCompleteSubmitElement.prop('disabled', false)
            } else if (element.parent().find('.autocomplete__input').val() === '') {
              element.val('')
              autoCompleteSubmitElement.prop('disabled', true)
            }
            if (required) {
              setInterval(function () { IFS.core.formValidation.checkRequired(element.parent().find('.autocomplete__input')) }, 1)
            }
          }
        })
        if (required) {
          element.parent().find('.autocomplete__input').attr('data-required-errormessage', required)
        }
        if (element.hasClass('govuk-input--error')) {
          element.parent().find('.autocomplete__input').addClass('govuk-input--error')
        }
      }
    }
  }
})()
