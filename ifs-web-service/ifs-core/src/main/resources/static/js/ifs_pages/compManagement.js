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
          menu : jQuery('.info-area'),
          container : '.competition-data form',
          breakpoint : 1200 //px
        },
        init: function(){
            s = this.settings;
            IFS.competition_management.getWindowWidth();
            IFS.competition_management.getMenuHeight();
            IFS.competition_management.getContainerHeight();

            if(IFS.competition_management.stickyEnabled()){
                IFS.competition_management.getMenuWidth();
                IFS.competition_management.getMenuOffset();
                IFS.competition_management.getContainerOffset();
                IFS.competition_management.menuPxToPercentage();
            }

            jQuery(window).resize(function(){
              clearTimeout(resizeTimer);
              resizeTimer = setTimeout(function(){
                IFS.competition_management.getMenuHeight();
                IFS.competition_management.getMenuWidth();
                IFS.competition_management.getContainerHeight();
                IFS.competition_management.getWindowWidth();
                IFS.competition_management.menuPxToPercentage();
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
        getMenuOffset : function(){
          calculatedValues.menuOffsetTop = s.menu.offset().top;
        },
        getMenuWidth: function(){
          calculatedValues.menuWidth = s.menu.width();
        },
        getMenuHeight : function(){
          calculatedValues.menuHeight = s.menu.outerHeight();
        },
        getContainerHeight : function(){
          calculatedValues.containerHeight = jQuery(s.container).outerHeight(true);
        },
        getContainerOffset : function(){
          calculatedValues.containerOffsetTop = jQuery(s.container).offset().top;
        },
        stickyEnabled: function(){
          //not responsively disabled or heigher menu than container
          if((s.breakpoint < calculatedValues.windowWidth) && (calculatedValues.menuHeight < calculatedValues.containerHeight)){
            return true;
          }
          return false;
        },
        menuPxToPercentage : function(){
          calculatedValues.menuPercentage =  parseFloat((calculatedValues.menuWidth/calculatedValues.windowWidth)*100).toFixed(2);
        },
        stickyScroll : function(){
          if(IFS.competition_management.stickyEnabled()){
            var scroll = jQuery(document).scrollTop();
            if((calculatedValues.menuHeight+scroll) > (calculatedValues.containerHeight+calculatedValues.containerOffsetTop)){
                var top = (calculatedValues.containerHeight+calculatedValues.containerOffsetTop)-calculatedValues.menuHeight;
                s.menu.addClass('bottom').removeClass('sticky').css({'top':top+'px','width':calculatedValues.menuPercentage+'%'});
            }
            else if(calculatedValues.menuOffsetTop < scroll) {
               s.menu.addClass('sticky').css({'top':'0','width':calculatedValues.menuPercentage+'%'}).removeClass('bottom');
            }
            else {
              s.menu.removeClass('sticky bottom').removeAttr('style');
            }
          }
          else {
            s.menu.removeClass('sticky bottom').removeAttr('style');
          }
        }
    };
})();
