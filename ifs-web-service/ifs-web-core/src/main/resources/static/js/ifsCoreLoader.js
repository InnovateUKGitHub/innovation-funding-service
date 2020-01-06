/* jshint strict: false, undef: true, unused: true  */

// Dom based routing
// ------------------
// Based on Paul Irish' code, please read blogpost
// http://www.paulirish.com/2009/markup-based-unobtrusive-comprehensive-dom-ready-execution///
// Does 2 jobs:
//    * Page dependend execution of functions
//    * Gives a more fine-grained control in which order stuff is executed
//
// Adding a page dependend function:
//    1. add class to body <body class="superPage">
//    2. add functions to the IFSLoader object IFSLoader = { superPage : init : function() {}};
//
// For now this will suffice, if complexity increases we might look at a more complex loader like requireJs.
// Please think before adding javascript, this project should work without any of this scripts.

if (typeof (IFS) === 'undefined') { var IFS = {} } // eslint-disable-line
IFS.core = {}
IFS.core.loadOrder = {
  common: {
    init: function () {
      IFS.core.editor.init()
      IFS.core.autoSave.init()
      IFS.core.formValidation.init()
      IFS.core.conditionalForms.init()
      IFS.core.wordCount.init()
      IFS.core.disableSubmitUntilChecked.init()
      IFS.core.finance.init()
      IFS.core.progressiveGroupSelect.init()
      IFS.core.singleSubmit.init()
      IFS.core.submitNotification.init()
      IFS.core.passwordToggle.init()
      IFS.core.preventInputRegex.init()
      IFS.core.autoComplete.init()
      IFS.core.exampleDate.init()
      IFS.core.pageHistory.init()
    },
    finalize: function () {
      IFS.core.modal.init()
      IFS.core.upload.init()
      IFS.core.autoSubmitForm.init()
      IFS.core.unsavedChanges.init()
      IFS.core.mirrorElements.init()
      IFS.core.debug.init()
      IFS.core.sortingErrors.init()
      IFS.core.tableSorter.init()
    }
  },
  'finance': {
    init: function () {
      IFS.core.repeatableFinanceRows.init()
    }
  },
  'finance-row-form': {
    init: function () {
      IFS.core.financeRowForm.init()
    }
  },
  'overheads': {
    finalize: function () {
      IFS.core.overheads.init()
    }
  },
  'team': {
    init: function () {
      IFS.core.team.init()
    }
  }
}
