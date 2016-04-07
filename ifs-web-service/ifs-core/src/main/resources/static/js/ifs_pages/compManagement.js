//Brent: Did some investigation what this does as it wasn't clear to me
//Does Hide and show textarea and some basic data validation
//Hide and show of form elements are already within the gov.uk js and client side validation is nice but we should cover that more generic.
IFS.competition_management = (function(){
    "use strict";
    var calculatedValues = {};
    var resizeTimer;
    var s;
    return {
        settings: {
          box : '.info-area',
          container : '.competition-data',
          breakpoint : 1200 //px
        },
        init: function(){
            s = this.settings;
            IFS.competition_management.getBoxOffset();
            IFS.competition_management.getWindowWidth();

            jQuery(window).resize(function(){
              clearTimeout(resizeTimer);
              resizeTimer = setTimeout(function(){
                IFS.competition_management.getWindowWidth();
                IFS.competition_management.stickyScroll();
              },250);
            });
            jQuery(document).scroll(function(){
                IFS.competition_management.stickyScroll();
            });
        },
        getWindowWidth: function(){
           calculatedValues.windowWidth = jQuery(window).width();
        },
        getBoxOffset : function(){
          calculatedValues.top = parseInt(jQuery(s.box).offset().top,10);
        },
        stickyScroll : function(){
          var box = jQuery(s.box);
          if(s.breakpoint < calculatedValues.windowWidth){
            var scroll = jQuery(document).scrollTop();
            if(scroll> calculatedValues.top) {
               box.addClass('sticky');
            }
            else {
                box.removeClass('sticky');
            }
          }
          else {
              box.removeClass('sticky');
          }
        }
    };
})();
