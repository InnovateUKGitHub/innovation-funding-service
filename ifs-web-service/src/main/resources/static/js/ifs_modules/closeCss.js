/* jshint strict: true, undef: true, unused: true */
/* globals  jQuery : false, setTimeout : false, ifs_closeCss  */

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
        	var el = jQuery('.'+s.isOpenCssClass);
        	if(el.length){
		        setTimeout(function(){
	            	el.removeClass(s.isOpenCssClass);
	        	},s.timeOpen);
        	}
        }
	};
})();