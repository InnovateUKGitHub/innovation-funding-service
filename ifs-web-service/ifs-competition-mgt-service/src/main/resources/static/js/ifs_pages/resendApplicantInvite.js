IFS.competitionManagement.resendApplicantInvite = (function () {
  'use strict'
  var s
  return {
    settings: {
      uploadButton: '#upload-applicant-resend-file',
      removeButton: '#remove-applicant-resend-file',
      fileInput: '#feedback',
      toggleClass: '.file-toggle',
      fileNameLabel: '#file-name'
    },
    init: function () {
      s = this.settings
      jQuery(document).on('click', s.uploadButton, function (evt) {
        IFS.competitionManagement.resendApplicantInvite.handleUploadClick(evt)
      })
      jQuery(document).on('click', s.removeButton, function (evt) {
        IFS.competitionManagement.resendApplicantInvite.handleRemoveClick(evt)
      })
    },
    handleUploadClick: function (evt) {
      evt.preventDefault()
      jQuery(IFS.competitionManagement.resendApplicantInvite.settings.toggleClass).toggle()
      var filePath = jQuery(IFS.competitionManagement.resendApplicantInvite.settings.fileInput).val()
      var fileName = filePath.split(/(\\|\/)/g).pop()
      jQuery(IFS.competitionManagement.resendApplicantInvite.settings.fileNameLabel)
        .text(fileName)
    },
    handleRemoveClick: function (evt) {
      evt.preventDefault()
      jQuery(IFS.competitionManagement.resendApplicantInvite.settings.toggleClass).toggle()
      IFS.competitionManagement.resendApplicantInvite.clearFileInput()
    },
    clearFileInput: function () {
      var fileInput = jQuery(IFS.competitionManagement.resendApplicantInvite.settings.fileInput)
      fileInput.wrap('<form>').closest('form').get(0).reset()
      fileInput.unwrap()
    }
  }
})()
