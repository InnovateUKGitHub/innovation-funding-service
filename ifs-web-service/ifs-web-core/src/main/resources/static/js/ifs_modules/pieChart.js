//Based upon Lea verou's SVG pie, adjusted with jquery for more legacy support
//http://www.smashingmagazine.com/2015/07/designing-simple-pie-charts-with-css/
IFS.core.pieChart = (function(){
  "use strict";
  var s; // private alias to settings

  return {
    settings : {
      pieElement : '.pie'
    },
    init : function(){
      s = this.settings;
      if (document.implementation.hasFeature("http://www.w3.org/TR/SVG11/feature#Image", "1.1")) {
        jQuery(s.pieElement).each(function() {
          IFS.core.pieChart.pieSVG(this);
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
      pie.setAttribute('aria-hidden', 'true');
      pie.appendChild(svg);
    }
  };
})();
