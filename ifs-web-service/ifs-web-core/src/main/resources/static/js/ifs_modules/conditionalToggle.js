// Assign two numeric values data-value-left and data-value-right and apply an operator e.g. data-operator="<".
// Append an element with data-value-change. if that element changes value then it will apply the condition
// and toggle the visibility of the target which is identified by data-toggle-target.
IFS.core.conditionalToggle = (function () {
  'use strict'
  var s // private alias to settings

  return {
    settings: {
      leftValue: '[data-value-left]',
      rightValue: '[data-value-right]'
    },
    operation: {
      '>': function (x, y) { return x > y },
      '>=': function (x, y) { return x >= y },
      '<=': function (x, y) { return x <= y },
      '<': function (x, y) { return x < y },
      '=': function (x, y) { return x === y }
    },
    init: function () {
      s = this.settings
      jQuery('body').on('DOMSubtreeModified', '[data-value-change]', function (e) {
        var firstValue = IFS.core.conditionalToggle.parseValue(jQuery(s.leftValue).text())
        var secondValue = IFS.core.conditionalToggle.parseValue(jQuery(s.rightValue).text())
        var operatorValue = jQuery('[data-value-change]').attr('data-operator')
        var target = jQuery('[data-toggle-target]')
        if (IFS.core.conditionalToggle.operation[operatorValue](firstValue, secondValue)) {
          target.attr('aria-hidden', 'true')
        } else {
          target.attr('aria-hidden', 'false')
        }
      })
    },
    parseValue: function (value) {
      var replaced = value.replace(/[£,]+/g, '')
      return parseInt(replaced)
    }
  }
})()
