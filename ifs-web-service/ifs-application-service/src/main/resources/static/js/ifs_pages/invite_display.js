IFS.application.inviteDisplay = (function () {
  'use strict'
  return {
    settings: {
    },
    init: function () {
      jQuery('[data-js-modal="modal-submit-remove-collaborator"]').click(function () {
        IFS.application.inviteDisplay.assignInviteIdOnClickRemove(this)
      })
    },
    assignInviteIdOnClickRemove: function (element) {
      var inviteId = jQuery(element).data('invite-id')
      jQuery('input[name="applicationInviteId"]').val(inviteId)
    }
  }
})()
