IFS.core.accordion = (function () {
  'use strict'
  var accordions = []
  return {
    add: function (accordion) {
      accordions.push(accordion)
    },
    get: function ($accordion) {
      var found = ''
      jQuery.each(accordions, function (index, accordion) {
        if (accordion.$module === $accordion) {
          found = accordion
          return false
        }
      })
      return found
    }

  }
})()

IFS.core.accordion.oldInit = window.GOVUKFrontend.Accordion.prototype.init

window.GOVUKFrontend.Accordion.prototype.init = function () {
  IFS.core.accordion.oldInit.call(this)
  IFS.core.accordion.add(this)
}

window.GOVUKFrontend.Accordion.prototype.sectionChange = function () {
  var accordion = this
  var $newSections = accordion.$module.querySelectorAll('.govuk-accordion__section')
  var $oldSections = accordion.$sections
  accordion.$sections = $newSections

  jQuery.each($newSections, function (i, el) {
    var index = jQuery.inArray(el, $oldSections)
    if (index === -1) {
      accordion.addSection(el, index)
    }
  })
  jQuery.each($oldSections, function (i, el) {
    if (jQuery.inArray(el, $newSections) === -1) {
      accordion.removeSection(el)
    }
  })
}

// This code is copied from the GDS framework, in the initSectionHeaders method
window.GOVUKFrontend.Accordion.prototype.addSection = function ($section, index) {
  // Set header attributes
  var header = $section.querySelector('.' + this.sectionHeaderClass)
  this.initHeaderAttributes(header, index)

  this.setExpanded(this.isExpanded($section), $section)

  // Handle events
  header.addEventListener('click', this.onSectionToggle.bind(this, $section))

  // See if there is any state stored in sessionStorage and set the sections to
  // open or closed.
  this.setInitialState($section)
  jQuery('.govuk-accordion__icon').attr('aria-hidden', 'false')
}

window.GOVUKFrontend.Accordion.prototype.removeSection = function ($section) {
  // Nothing needed to remove section.
}
