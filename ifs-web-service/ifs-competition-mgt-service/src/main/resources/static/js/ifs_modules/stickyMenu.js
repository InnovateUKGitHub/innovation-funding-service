IFS.competitionManagement.stickyMenu = (function() {
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
    init: function() {
      s = this.settings;
      IFS.competitionManagement.stickyMenu.getWindowWidth();
      IFS.competitionManagement.stickyMenu.getMenuHeight();
      IFS.competitionManagement.stickyMenu.getContainerHeight();

      if(IFS.competitionManagement.stickyMenu.stickyEnabled()){
        IFS.competitionManagement.stickyMenu.getMenuWidth();
        IFS.competitionManagement.stickyMenu.getMenuOffset();
        IFS.competitionManagement.stickyMenu.getContainerOffset();
        IFS.competitionManagement.stickyMenu.menuPxToPercentage();
      }

      jQuery(window).resize(function() {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function() {
          IFS.competitionManagement.stickyMenu.getMenuHeight();
          IFS.competitionManagement.stickyMenu.getMenuWidth();
          IFS.competitionManagement.stickyMenu.getContainerHeight();
          IFS.competitionManagement.stickyMenu.getWindowWidth();
          IFS.competitionManagement.stickyMenu.menuPxToPercentage();
          IFS.competitionManagement.stickyMenu.stickyScroll();
        }, 250);
      });
      jQuery(document).scroll(function() {
        IFS.competitionManagement.stickyMenu.stickyScroll();
      });
    },
    getWindowWidth: function() {
      calculatedValues.windowWidth = jQuery(window).width();
    },
    getMenuOffset : function() {
      calculatedValues.menuOffsetTop = s.menu.offset().top;
    },
    getMenuWidth: function() {
      calculatedValues.menuWidth = s.menu.width();
    },
    getMenuHeight : function() {
      calculatedValues.menuHeight = s.menu.outerHeight();
    },
    getContainerHeight : function() {
      calculatedValues.containerHeight = jQuery(s.container).outerHeight(true);
    },
    getContainerOffset : function() {
      calculatedValues.containerOffsetTop = jQuery(s.container).offset().top;
    },
    stickyEnabled: function() {
      //not responsively disabled or higher menu than container
      if((s.breakpoint < calculatedValues.windowWidth) && (calculatedValues.menuHeight < calculatedValues.containerHeight)){
        return true;
      }
      return false;
    },
    menuPxToPercentage : function() {
      calculatedValues.menuPercentage =  parseFloat((calculatedValues.menuWidth/calculatedValues.windowWidth)*100).toFixed(2);
    },
    stickyScroll : function() {
      if(IFS.competitionManagement.stickyMenu.stickyEnabled()){
        var scroll = jQuery(document).scrollTop();
        if((calculatedValues.menuHeight+scroll) > (calculatedValues.containerHeight+calculatedValues.containerOffsetTop)){
          var top = (calculatedValues.containerHeight+calculatedValues.containerOffsetTop)-calculatedValues.menuHeight;
          s.menu.addClass('bottom').removeClass('sticky').css({'top':top+'px', 'width':calculatedValues.menuPercentage+'%'});
        }
        else if(calculatedValues.menuOffsetTop < scroll) {
          s.menu.addClass('sticky').css({'top':'0', 'width':calculatedValues.menuPercentage+'%'}).removeClass('bottom');
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
