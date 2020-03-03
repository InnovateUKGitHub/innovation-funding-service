// Assign two numeric values data-value-one and data-value-one and apply an operator e.g. data-operator="<".
// Append an element with data-value-change. if that element changes value then it will apply the condition
// and toggle the visibility of the target which is identified by data-toggle-target.
IFS.core.conditionalToggle = (function () {
  'use strict'
  var s // private alias to settings

  return {
    settings: {
      value1: '[data-value-one]',
      value2: '[data-value-two]'
    },
    Operation: {
      '>': function (x, y) { return x > y },
      '<': function (x, y) { return x < y },
      '=': function (x, y) { return x === y }
    },
    init: function () {
      s = this.settings
      jQuery('[data-value-change]').on('DOMSubtreeModified', function (e) {
        var firstValue = jQuery(s.value1).text()
        var secondValue = jQuery(s.value2).text()
        var operatorValue = jQuery('[data-value-change]').attr('data-operator')
        var target = jQuery('[data-toggle-target]')
        if (IFS.core.conditionalToggle.Operation[operatorValue](firstValue, secondValue)) {
          target.attr('aria-hidden', 'true')
        } else {
          target.attr('aria-hidden', 'false')
        }
      })
    }
  }
})()
