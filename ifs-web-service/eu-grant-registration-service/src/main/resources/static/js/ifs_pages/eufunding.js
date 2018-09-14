IFS.euGrantRegistration.eufunding = (function () {
  'use strict'
  var s
  return {
    settings: {
      eufundingForm: 'form'
    },
    init: function () {
      s = this.settings

      jQuery(s.eufundingForm).on('change', 'input[data-date]', function () {
        IFS.euGrantRegistration.eufunding.eufundingDateValidation()
      })
    },
    eufundingDateValidation: function () {
      if (jQuery('#endDateYear').val() &&
         jQuery('#endDateMonth').val() &&
         jQuery('#startDateYear').val() &&
         jQuery('#startDateMonth').val()) {
        if (jQuery('#endDateYear').val() > jQuery('#startDateYear').val() ||
        (jQuery('#endDateYear').val() === jQuery('#startDateYear').val() &&
            jQuery('#endDateMonth').val() > jQuery('#startDateMonth').val())) {
          // good
          IFS.core.formValidation.setValid(jQuery('#endDateMonth'), 'End date must be after end date.', 'show')
        } else {
          // bad
          IFS.core.formValidation.setInvalid(jQuery('#endDateMonth'), 'End date must be after end date.', 'show')
        }
      }
    }
  }
})()
