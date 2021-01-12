/* jshint strict: false, undef: true, unused: true */

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
IFS.competitionManagement = {}
IFS.competitionManagement.loadOrder = {
  common: {
    init: function () {
      IFS.competitionManagement.various.init()
    },
    finalize: function () {
      IFS.competitionManagement.multipageSelect.init()
    }
  },
  'competition-management': {
    init: function () {
      IFS.competitionManagement.repeatableRows.init()
    }
  },
  'competition-setup': {
    init: function () {
      IFS.competitionManagement.initialDetails.init()
      IFS.competitionManagement.milestones.init()
      IFS.competitionManagement.fundingInformation.init()
      IFS.competitionManagement.repeater.init()
      IFS.core.finance.init()
    }
  },
  'resend-applicant-invite': {
    init: function () {
      IFS.competitionManagement.resendApplicantInvite.init()
    }
  },
  'eu-notified': {
    init: function () {
      IFS.competitionManagement.select.init()
    }
  }

}
