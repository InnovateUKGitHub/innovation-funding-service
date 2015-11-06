//Based upon Lea verou's SVG pie, adjusted with jquery and modernizr for more legacy support
//http://www.smashingmagazine.com/2015/07/designing-simple-pie-charts-with-css/

var ifs_pieChart = (function(){
    "use strict";
     var s; // private alias to settings 

    return {
        settings : {
            pieElement : '.pie'
        },
        init : function(){
            s = this.settings;
            if(Modernizr.svg && Modernizr.inlinesvg){
                jQuery(s.pieElement).each(function() {
                		ifs_pieChart.pieSVG(this);	
                });
            }
        },
        pieSVG : function(pie){
            var p = parseFloat(pie.textContent);
            var NS = "http://www.w3.org/2000/svg";
            var svg = document.createElementNS(NS, "svg");
            var circle = document.createElementNS(NS, "circle");
            var title = document.createElementNS(NS, "title");

            circle.setAttribute("r", 16);
            circle.setAttribute("cx", 16);
            circle.setAttribute("cy", 16);
            circle.setAttribute("stroke-dasharray", p + " 100");

            svg.setAttribute("viewBox", "0 0 32 32");
            title.textContent = pie.textContent;
            pie.textContent = '';
            svg.appendChild(title);
            svg.appendChild(circle);
            pie.appendChild(svg);
        }
    };
})();
