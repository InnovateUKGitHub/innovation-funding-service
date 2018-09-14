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
        // only raise/hide errors if all fields are filled in.
        if (jQuery('#endDateYear').val() > jQuery('#startDateYear').val() ||
        (jQuery('#endDateYear').val() === jQuery('#startDateYear').val() &&
            jQuery('#endDateMonth').val() > jQuery('#startDateMonth').val())) {
          // hide error as start is before end
          IFS.core.formValidation.setValid(jQuery('#endDateMonth'), 'End date must be after end date.', 'show')
        } else {
          // show error as start is after end
          IFS.core.formValidation.setInvalid(jQuery('#endDateMonth'), 'End date must be after end date.', 'show')
        }
      }
    }
  }
})()
