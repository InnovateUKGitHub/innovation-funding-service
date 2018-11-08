IFS.core.overheads = (function () {
  'use strict'
  var s
  return {
    settings: {
      radios: '.overhead-radio',
      target: '#section-total-overhead',
      totals: {
        'NONE': '#section-total-overhead-none',
        'DEFAULT_PERCENTAGE': '#section-total-overhead-default',
        'TOTAL': '#section-total-overhead-calculated'
      }
    },
    init: function () {
      s = this.settings
      IFS.core.overheads.setOverheadSectionElementValue()
      jQuery(document).on('change', s.radios, function () {
        IFS.core.overheads.setActiveTotal(this)
      })
    },
    setActiveTotal: function (element) {
      var val = jQuery(element).val()
      var currentTotal = typeof (s.totals[val]) !== 'undefined' ? s.totals[val] : false
      var allOtherTotals = jQuery.map(s.totals, function (total) {
        if (total !== currentTotal) {
          return total
        }
      }).join(',')

      if (currentTotal) {
        currentTotal = jQuery(currentTotal)
        allOtherTotals = jQuery(allOtherTotals)
        allOtherTotals.removeAttr('data-current-overhead-total').val('£0').attr({ 'data-calculation-rawvalue': 0, 'data-inactive-overhead-total': '' })
        currentTotal.removeAttr('data-inactive-overhead-total').attr('data-current-overhead-total', '')
        // make sure the finance calculation gets recalculated
        if (currentTotal.is('[data-calculation-fields]')) {
          var fields = currentTotal.attr('data-calculation-fields')
          IFS.core.finance.doMath(currentTotal, fields.split(','))
        } else {
          currentTotal.trigger('change')
        }
      }
    },
    setOverheadSectionElementValue: function () {
      var totalElements = jQuery(s.radios).closest('section.collapsible').find('[data-mirror]')
      totalElements.each(function () {
        var totalEl = jQuery(this)
        // total in section header we mirror the total directly
        IFS.core.mirrorElements.updateElement(totalEl, '[data-current-overhead-total]')
        IFS.core.mirrorElements.bindMirrorElement(totalEl, '[data-current-overhead-total]')
      })
    }
  }
})()
