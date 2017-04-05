IFS.competitionManagement.milestones = (function () {
  'use strict'
  var s
  return {
    settings: {
      milestonesForm: '[data-section="milestones"]'
    },
    init: function () {
      s = this.settings

      jQuery(s.milestonesForm).on('change', 'input[data-date]', function () {
        IFS.competitionManagement.milestones.milestonesExtraValidation()
        IFS.competitionManagement.milestones.milestonesSetFutureDate(jQuery(this))
      })
      IFS.competitionManagement.milestones.mileStoneValidateOnPageLoad()
    },
    milestonesExtraValidation: function () {
      // some extra javascript to hide the server side messages when the field is valid
      var fieldErrors = jQuery(s.milestonesForm + ' .field-error')
      var emptyInputs = jQuery(s.milestonesForm + ' input').filter(function () { return !this.value })
      if (fieldErrors.length === 0 && emptyInputs.length === 0) {
        jQuery(s.milestonesForm + ' .error-summary').attr('aria-hidden', 'true')
      }
    },
    mileStoneValidateOnPageLoad: function () {
      jQuery(s.milestonesForm + ' .day input').each(function (index, value) {
        var field = jQuery(value)
        if (index === 0) {
          IFS.core.formValidation.checkDate(field, true)
        }
        IFS.competitionManagement.milestones.milestonesSetFutureDate(field)
      })
    },
    milestonesSetFutureDate: function (field) {
      setTimeout(function () {
        var nextRow = field.closest('tr').next('tr')
        var date = field.attr('data-date')
        var time
        if (field.closest('tr').find('.time select').length > 0) {
          time = field.closest('tr').find('.time option:selected').data('time')
        } else {
          time = field.closest('tr').find('.time [data-time]').data('time')
        }
        if (nextRow.length) {
          nextRow.attr({'data-future-date': date + (time !== undefined ? '-' + time : '')})
          if (jQuery.trim(date.length) !== 0) {
            var input = nextRow.find('.day input')
            IFS.core.formValidation.checkDate(input, true)
          }
        }
      }, 0)
    }
  }
})()
