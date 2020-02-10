// Innovation Funding Services javascript for calculating the finance fields
IFS.core.finance = (function () {
  'use strict'
  return {
    MathOperation: {
      '+': function (x, y) { return x + y },
      '-': function (x, y) { return x - y },
      '*': function (x, y) { return x * y },
      '/': function (x, y) {
        if (y === 0) {
          return 0
        }
        return x / y
      },
      'negativeRoundUp': function (x, y) { return x < 0 ? 0 : x } // jshint ignore:line
    },
    init: function () {
      IFS.core.finance.bindCalculationActionToFields() // Bind calculations
    },
    bindCalculationActionToFields: function () {
      // this is not binding as it is doing a one off calculation on pageload
      var pageLoadFinances = jQuery('[data-calculation-fields][data-calculation-on-pageload]')
      pageLoadFinances.each(function () {
        var element = jQuery(this)
        var fields = element.attr('data-calculation-fields')
        IFS.core.finance.doMath(element, fields.split(','))
      })

      // we watch for changes in inputs as a calculation always starts with an input
      jQuery('body').on('change', 'input', function () {
        // if the calculation field is not binded we bind this field to the selector that is defined in the field
        if (jQuery('[data-calculation-fields]:not([data-calculation-binded],[data-inactive-overhead-total])').length) {
          jQuery('[data-calculation-fields]:not([data-calculation-binded],[data-inactive-overhead-total])').each(function () {
            var element = jQuery(this)
            var fields = element.attr('data-calculation-fields')

            jQuery(document).on('change updateFinances', fields, function () {
              IFS.core.finance.doMath(element, fields.split(','))
            })
            // we only want to bind a field once
            element.attr('data-calculation-binded', '')
          })
        }
      })
      // force recalculate, only used for removal of finances
      jQuery('body').on('recalculateAllFinances', function () {
        if (jQuery('[data-calculation-fields]:not([data-inactive-overhead-total])').length) {
          jQuery('[data-calculation-fields]:not([data-inactive-overhead-total])').each(function () {
            var element = jQuery(this)
            var fields = element.attr('data-calculation-fields')
            IFS.core.finance.doMath(element, fields.split(','))
          })
        }
      })
    },
    getElementValue: function (element) {
      var rawValue = jQuery(element).attr('data-calculation-rawvalue')

      // would be better to force all fields to have a raw value at the start rather than these fallback cases
      if (typeof (rawValue) !== 'undefined') {
        return parseFloat(rawValue)
      } else {
        if ((typeof (jQuery(element).val()) !== 'undefined') && (jQuery(element).val().length)) {
          var displayValue = jQuery(element).val().replace(',', '')
          var parsed = displayValue.indexOf('£') === 0 ? displayValue.substring(1) : displayValue
          return parseFloat(parsed)
        }
      }
      return parseFloat(0)
    },
    doMath: function (element, calcFields) {
      var operation = element.attr('data-calculation-operations').split(',')
      var values = []
      jQuery.each(calcFields, function (index, field) {
        if (jQuery.isNumeric(field)) {
          // we use a static number not a selector to another field
          values.push(parseFloat(field))
        } else if (jQuery(field).length > 1) {
          // we use a selector with multiple inputs and get the value
          jQuery.each(jQuery(field), function (index, field2) {
            values.push(IFS.core.finance.getElementValue(field2))
          })
        } else {
          // we use a selector with one input
          values.push(IFS.core.finance.getElementValue(field))
        }
      })
      var calculatedValue
      if (values.length === 1) {
        calculatedValue = values[0]
      } else {
        calculatedValue = IFS.core.finance.MathOperation[operation[0]](values[0], values[1])
      }

      // one operation and more values, all get the same operation
      if ((operation.length === 1) && (values.length > 2)) {
        for (var i = 2; i < values.length; i++) {
          calculatedValue = IFS.core.finance.MathOperation[operation[0]](calculatedValue, values[i])
        }
      }
      // multiple operations and multiple values
      if ((operation.length > 1) && (values.length > 2)) {
        for (var j = 1; j < operation.length; j++) {
          calculatedValue = IFS.core.finance.MathOperation[operation[j]](calculatedValue, values[j + 1])
        }
      }
      element.attr('data-calculation-rawvalue', calculatedValue)

      var format = element.is('[data-calculation-format]') ? element.attr('data-calculation-format') : 'currency'
      var formattedNumber = ''

      // Check for valid number
      if (!jQuery.isNumeric(calculatedValue)) {
        calculatedValue = 0
      }

      if (format === 'percentage') {
        formattedNumber = IFS.core.finance.formatPercentage(calculatedValue)
      } else if (format === 'currency') {
        formattedNumber = IFS.core.finance.formatCurrency(calculatedValue)
      } else if (format === 'decimal-percentage') {
        formattedNumber = IFS.core.finance.formatDecimalPercentage(calculatedValue)
      }

      if (element.is('input')) {
        element.val(formattedNumber)
      } else {
        element.text(formattedNumber)
      }
      element.trigger('change')
    },
    formatCurrency: function (total) {
      total = total.toFixed()
      total = total.replace(/(\d)(?=(\d{3})+\b)/g, '$1,')
      return '£' + total
    },
    formatPercentage: function (total) {
      total = total.toFixed()
      return total + '%'
    },
    formatDecimalPercentage: function (total) {
      total = total.toFixed(2)
      return total + '%'
    }
  }
})()
