/* jshint strict: true, undef: true, unused: true */
/* globals  jQuery : false, setTimeout : false */

//simple javscript, waits x amount of seconds and removes a Css Class
//Animation and styling is done with css
//used for closing notifications but could be used for different things

 var ifs_closeCss = (function(){
    "use strict";
    var s; // private alias to settings 

    return {
        settings : {
            isOpenCssClass : 'is-open',
            timeOpen : 3000
        },
        init : function(){
            s = this.settings;
            ifs_closeCss.removeCssClass(s.isOpenCssClass,s.timeOpen);
        },
        removeCssClass : function(cssClass,seconds){
            var el = jQuery('.'+cssClass);
            if(el.length){
                setTimeout(function(){
                    el.removeClass(cssClass);
                },seconds);
            }
        }
    };
})();