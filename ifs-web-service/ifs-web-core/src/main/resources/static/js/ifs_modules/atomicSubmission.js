IFS.core.atomicSubmission = function (submittedForm) {
  'use strict'
  submittedForm.submitButton.disabled = true
  submittedForm.submitButton.value = 'Please wait...'
}
