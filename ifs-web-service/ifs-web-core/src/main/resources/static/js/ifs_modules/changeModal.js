// Using a form to change the modal that will open on clicking a button.
// Use the data-change-modal wrapping the form elements that will change the button. The value of this attribute
// should be a selector for the button.
// The form elements that can change must have data-modal-link = "destination" for this to work
IFS.core.changeModel = (function () {
  'use strict'
  var s // private alias to settings

  return {
    settings: {
      changeModal: 'data-change-modal',
      modalLink: 'data-modal-link'
    },
    init: function () {
      s = this.settings
      jQuery('body').on('change', '[' + s.changeModal + ']', function (e) {
        var container = jQuery(this)
        var button = jQuery(container.attr(s.changeModal))
        var name = jQuery(e.target).attr(s.modalLink)
        button.attr('data-js-modal', 'modal-' + name.toLowerCase())
      })
    }
  }
})()
