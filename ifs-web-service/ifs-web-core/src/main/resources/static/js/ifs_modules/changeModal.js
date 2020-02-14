// Using radio buttons changes the model destination for a button with id = modal-button.
// radio buttons must have modal-link = "destination" for this to work
IFS.core.modal = (function () {
  'use strict'
  return {
    init: function () {
      jQuery('.govuk-radios__item').on('click', function (e) {
        var button = jQuery('#modal-button')
        var name = jQuery(e.target).attr('modal-link')
        button.attr('data-js-modal', 'modal-' + name.toString().toLowerCase())
      })
    }
  }
})()
