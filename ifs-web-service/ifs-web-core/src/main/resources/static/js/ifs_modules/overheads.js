IFS.core.overheads = (function () {
  'use strict'
  var s
  return {
    settings: {
      radios: '.overhead-radio',
      target: '#section-total-overhead',
      totals: {
        'NONE': '#total-overhead-none',
        'DEFAULT_PERCENTAGE': '#total-overhead-default',
        'TOTAL': '#total-overhead-calculated'
      }
    },
    init: function () {
      s = this.settings

      var el = jQuery(s.radios + ':checked')
      if (el.length) {
        IFS.core.overheads.setActiveTotal(el)
      }

      jQuery(document).on('change', s.radios, function () {
        IFS.core.overheads.setActiveTotal(this)
      })
    },
    setActiveTotal: function (element) {
      var val = jQuery(element).val()
      var currentTotalSelector = typeof (s.totals[val]) !== 'undefined' ? s.totals[val] : false
      var allOtherTotalsSelectors = jQuery.map(s.totals, function (total) {
        if (total !== currentTotal) {
          return total
        }
      }).join(',')

      if (currentTotalSelector) {
        var currentTotal = jQuery(currentTotalSelector)
        var allOtherTotals = jQuery(allOtherTotalsSelectors)
        allOtherTotals.removeAttr('data-current-overhead-total').val('£0').attr({ 'data-calculation-rawvalue': 0, 'data-inactive-overhead-total': '' })
        currentTotal.removeAttr('data-inactive-overhead-total').attr('data-current-overhead-total', '')
        jQuery(s.target).attr({
          'data-calculation-fields': currentTotalSelector + ',1',
          'data-calculation-operations': '*'
        }).removeAttr('data-calculation-binded')
        // make sure the finance calculation gets recalculated
        if (currentTotal.is('[data-calculation-fields]')) {
          var fields = currentTotal.attr('data-calculation-fields')
          IFS.core.finance.doMath(currentTotal, fields.split(','))
        } else {
          currentTotal.trigger('change')
        }
      }
    }
  }
})()
