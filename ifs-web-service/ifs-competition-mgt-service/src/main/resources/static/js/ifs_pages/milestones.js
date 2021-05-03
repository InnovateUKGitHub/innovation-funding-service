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
      var fieldErrors = jQuery(s.milestonesForm + ' .govuk-input--error')
      var emptyInputs = jQuery(s.milestonesForm + ' input').filter(function () { return !this.value })
      if (fieldErrors.length === 0 && emptyInputs.length === 0) {
        jQuery(s.milestonesForm + ' .error-summary').attr('aria-hidden', 'true')
      }
    },
    mileStoneValidateOnPageLoad: function () {
      jQuery(s.milestonesForm + ' .day input').each(function (index, value) {
        var field = jQuery(value)
        if (index === 0) {
          IFS.core.formValidation.checkDate(field)
        }
        IFS.competitionManagement.milestones.milestonesSetFutureDate(field)
      })
    },
    milestonesSetFutureDate: function (field) {
      setTimeout(function () {
        var currentRow = field.closest('tr')
        if (!currentRow.hasClass('not-required-in-sequence')) {
          var nextRow = currentRow.next('tr')
          var date = field.attr('data-date')
          var time
          if (currentRow.find('.time select').length > 0) {
            time = currentRow.find('.time option:selected').attr('data-time')
          } else {
            time = currentRow.find('.time [data-time]').attr('data-time')
          }
          if (nextRow.length) {
            nextRow.attr({'data-future-date': date + (time !== undefined ? '-' + time : '')})
            if (jQuery.trim(date.length) !== 0) {
              var input = nextRow.find('.day input')
              IFS.core.formValidation.checkDate(input)
            }
          }
        }
      }, 0)
    }
  }
})()
