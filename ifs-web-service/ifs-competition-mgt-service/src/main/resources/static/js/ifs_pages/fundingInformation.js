IFS.competitionManagement.fundingInformation = (function () {
  'use strict'
  return {
    init: function () {
      jQuery('#generate-code').prop('type', 'button')
      jQuery(document).on('click', '#generate-code', function () {
        IFS.competitionManagement.fundingInformation.handleCompetitionCode(this)
      })
    },
    handleCompetitionCode: function (el) {
      var button = jQuery(el)
      var competitionId = button.val()
      var field = button.closest('.form-group').find('input')
      var url = window.location.protocol + '//' + window.location.host + '/management/competition/setup/' + competitionId + '/generateCompetitionCode'
      // todo ajax failure
      jQuery.ajaxProtected({
        type: 'GET',
        url: url
      }).done(function (data) {
        if (typeof (data) !== 'undefined') {
          if (data.success === 'true') {
            IFS.core.formValidation.setValid(field, IFS.core.formValidation.getErrorMessage(field, 'required'))
            field.val(data.message)
            jQuery('body').trigger('updateSerializedFormState')
          } else {
            IFS.core.formValidation.setInvalid(field, data.message)
          }
        }
      })
    }
  }
})()
