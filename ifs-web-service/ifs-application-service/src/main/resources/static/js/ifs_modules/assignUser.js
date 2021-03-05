IFS.application.progressiveSelect = (function () {
  'use strict'
  var s
  var index = 0
  return {
    settings: {
      selectEl: 'select.prog-menu',
      dropdownContainer: '.assign-container .assign-button'
    },
    init: function () {
      s = this.settings
      IFS.application.progressiveSelect.initDomChanges()
      IFS.application.progressiveSelect.initEventHandlers()
    },
    initEventHandlers: function () {
      jQuery(document).on('click', function () {
        IFS.application.progressiveSelect.hideAll()
      })
      jQuery('body').on('click', '[data-add-row-man]', function (e) {
        e.preventDefault()
        IFS.manuallyEnter.handleAddRowMan(this)
      })
      jQuery('body').on('click', '[data-remove-row-man]', function (e) {
        e.preventDefault()
        IFS.manuallyEnter.handleRemoveRowMan(this)
      })
      jQuery('body').on('click', '.assign-button [aria-controls]', function (e) {
        e.stopPropagation()
        IFS.application.progressiveSelect.toggleDropdown(this)
      })
    },
    initDomChanges: function () {
      jQuery(s.dropdownContainer).each(function () {
        IFS.application.progressiveSelect.initDropDownHTML(this)
      })
      jQuery(s.selectEl).each(function () {
        IFS.application.progressiveSelect.selectToListHTML(this)
      })
    },
    selectToListHTML: function (el) {
      el = jQuery(el)
      el.prev('label').remove()
      el.next('button').remove()
      var children = el.children('option')
      var name = el.attr('name')
      var html = '<ul class="govuk-list govuk-list--bullet">'
      children.each(function () {
        var inst = jQuery(this)
        var content = inst.html()
        var value = inst.attr('value')

        if (inst.is('[disabled]')) {
          html += '<li>' + content + '</li>'
        } else {
          html += '<li><button id="assign-' + value + '" value="' + value + '" name="' + name + '" class="button-clear" aria-label="Reassign task to different team member"><span class="govuk-visually-hidden">Reassign this to </span>' + content + '</button></li>'
        }
      })
      html += '</ul>'
      el.after(html).remove()
      jQuery('body').trigger('updateSerializedFormState')
    },
    initDropDownHTML: function (el) {
      var inst = jQuery(el)
      var id = 'dropdown-' + index // create unique id for a11y relationship
      // wrap the content and make it focusable
      inst.next().wrapAll('<div id="' + id + '" aria-hidden="true">')
      // Add the button inside the <h2> so both the heading and button semanics are read
      inst.wrapInner('<button aria-expanded="false" aria-controls="' + id + '" type="button">')
      index++
    },
    toggleDropdown: function (el) {
      var inst = jQuery(el)
      var dropdown = jQuery('#' + inst.attr('aria-controls'))
      var state = inst.attr('aria-expanded') === 'false'

      IFS.application.progressiveSelect.hideAll()
      if (state) {
        inst.attr('aria-expanded', 'true')
        dropdown.attr('aria-hidden', 'false')
      }
    },
    hideAll: function () {
      jQuery('[aria-controls*="dropdown-"][aria-expanded="true"]').attr('aria-expanded', 'false')
      jQuery('[id^="dropdown-"][aria-hidden="false"]').attr('aria-hidden', 'true')
    }

  }
})()
