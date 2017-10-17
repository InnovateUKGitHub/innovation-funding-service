IFS.projectSetup.spendProfile = (function () {
  'use strict'
  var s // private alias to settings
  return {
    settings: {
      totalElement: 'input[id*=row-total-],#spend-profile-total-total',
      message: 'Your %category% costs are higher than your eligible costs.'
    },
    init: function () {
      s = this.settings
      jQuery('body').on('change', s.totalElement, function () {
        IFS.projectSetup.spendProfile.checkSpend(this)
      })
      jQuery('body').on('focus', s.totalElement, function () {
        jQuery(this).closest('tr').find('input').first().focus()
      })
    },
    checkSpend: function (el) {
      var inst = jQuery(el)
      var totalTd = inst.closest('td')
      var currentTotal = parseInt(inst.attr('data-calculation-rawvalue'), 10)
      var eligibleTotal = parseInt(totalTd.next().find('input').attr('data-calculation-rawvalue'), 10)

      var categoryName = inst.closest('tr').find('th span:not(".error-message")').text().toLowerCase()
      var message = s.message
      message = message.replace('%category%', categoryName)

      if (currentTotal > eligibleTotal) {
        totalTd.addClass('cell-error')
        IFS.core.formValidation.setInvalid(inst, message, 'show')
      } else {
        totalTd.removeClass('cell-error')
        IFS.core.formValidation.setValid(inst, message, 'show')
      }
    }
  }
})()
