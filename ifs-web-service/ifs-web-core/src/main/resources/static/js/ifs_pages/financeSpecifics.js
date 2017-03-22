IFS.core.financeSpecifics = (function () {
  'use strict'
  var s
  return {
    settings: {
      adminSupportCostsSelect: '[name*="overheads-type"]',
      adminSupportCostsSelectTotals: {
        'NONE': '[id^="section-total-"][id$="-cost-none"]',
        'DEFAULT_PERCENTAGE': '[id^="section-total-"][id$="-default"]',
        'CUSTOM_RATE': '[id^="section-total-"][id$="-custom"]',
        'TOTAL': '[id^="section-total-"][id$="-calculate-formatted"]'
      }
    },
    init: function () {
      s = this.settings
      // console.log(s.allTotals)
      IFS.core.financeSpecifics.initOverheadActiveTotalChanges()
      IFS.core.financeSpecifics.initOtherFunding()
    },
    initOverheadActiveTotalChanges: function () {
      IFS.core.financeSpecifics.setOverheadSectionElementValue()

      var el = jQuery(s.adminSupportCostsSelect + ':checked')
      if (el.length) {
        IFS.core.financeSpecifics.makeRightOverheadTotalActive(el)
      }

      jQuery(document).on('change', s.adminSupportCostsSelect, function () {
        IFS.core.financeSpecifics.makeRightOverheadTotalActive(this)
      })
    },
    makeRightOverheadTotalActive: function (element) {
      var val = jQuery(element).val()
      var currentTotal = typeof (s.adminSupportCostsSelectTotals[val]) !== 'undefined' ? s.adminSupportCostsSelectTotals[val] : false
      var allOtherTotals = jQuery.map(s.adminSupportCostsSelectTotals, function (total) {
        if (total !== currentTotal) {
          return total
        }
      }).join(',')

      if (currentTotal) {
        currentTotal = jQuery(currentTotal)
        allOtherTotals = jQuery(allOtherTotals)
        allOtherTotals.removeAttr('data-current-overhead-total').val('£ 0').attr('data-calculation-rawvalue', 0)
        currentTotal.attr('data-current-overhead-total', '')
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
      var totalElements = jQuery(s.adminSupportCostsSelect).closest('section.collapsible').find('[data-mirror]')
      totalElements.each(function () {
        var totalEl = jQuery(this)
        if (totalEl.is('[data-calculation-format="percentage"]')) {
          // percentage total in section header update the calculation
          totalEl.attr('data-calculation-fields', '100, #total-cost, [data-current-overhead-total]')
        } else {
          // total in section header we mirror the total directly
          IFS.core.mirrorElements.updateElement(totalEl, '[data-current-overhead-total]')
          IFS.core.mirrorElements.bindMirrorElement(totalEl, '[data-current-overhead-total]')
        }
      })
    },
    initOtherFunding: function () {
      // make sure that the total gets updated on pressing yes, for more info INFUND-3196
      jQuery('#otherFundingShowHideToggle').on('change', '[data-target] input[type="radio"]', function () {
        setTimeout(function () {
          jQuery('[id*=fundingAmount]').trigger('updateFinances')
        }, 0)
      })
    }
  }
})()
