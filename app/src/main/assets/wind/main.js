    var map;
    var windlayer;
    var str;
    var lat;
    var lng;
    var x;
    var y;

      require([
        "esri/map",
        "esri/layers/ArcGISDynamicMapServiceLayer"
      ], function (
        Map, ArcGISDynamicMapServiceLayer) {

        map = new Map("mapDiv", {
	  basemap:"satellite",
	  zoom:4,
	  center:[77, 23],
          sliderOrientation : "horizontal"
        });

        windlayer = new ArcGISDynamicMapServiceLayer("http://14.139.172.6:6080/arcgis/rest/services/100_CUF_Mast/MapServer", {
          "opacity" : 1
        });
        map.addLayer(windlayer);
        
        map.on("update-start", bufferstart);
        map.on("update-end", bufferend);
        
        function bufferstart(){
        document.getElementById("buffer").style.visibility = "visible";  
        }
        
        function bufferend(){
        document.getElementById("buffer").style.visibility = "hidden";    
        }
        
        map.on("click", addPoint);

        function addPoint(evt) {
        lat = evt.mapPoint.getLatitude();
        lng = evt.mapPoint.getLongitude();
        sendh();
        map.infoWindow.setTitle("NIWE Wind Data");
        map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
        }
      });
      
      
function sendh(){
     y = lat;
     x = lng;

      niweurl="http://14.139.172.6:6080/arcgis/rest/services/100_CUF_Mast/MapServer/8/query?where=&text=&objectIds=&time=&geometry="+x+"%2C"+y+"&geometryType=esriGeometryPoint&inSR=4326&spatialRel=esriSpatialRelIntersects&relationParam=&outFields=*&returnGeometry=true&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVersion=&returnDistinctValues=false&resultOffset=&resultRecordCount=&f=pjson";
      $(function(){ 
         $.ajax({
            type: 'GET',
            url: niweurl ,
            success: function(data){
          ndata =('',data);  
          outputh(ndata);       
            }
         });
     });

     }

  // output from NIWE 
function outputh(data){
    
     var parsetxtraw = JSON.parse(data);
     var test = parsetxtraw.features[0];
     if(test === undefined){ 
     str = "Please select an location within India";
     map.infoWindow.setContent(str);
     }else{   

     	var ut = parsetxtraw.features[0].attributes.UT;
     	var state = parsetxtraw.features[0].attributes.State;
     	var cuf = parsetxtraw.features[0].attributes.CUF;
     	var wpd = parsetxtraw.features[0].attributes.WPD;
     	var windspeed = parsetxtraw.features[0].attributes.WindSpeed;
   	str = '<html>'+'<body>'+'<table cellspacing="5">'+'<tr>'+'<td>'+'UT :'+'</td>'+'<td>'+ut+'</td>'+'</tr>'+'<tr>'+'<td>'+'STATE :'+'</td>'+'<td>'+state+'</td>'+'</tr>'+'<tr>'+'<td>'+'CUF(%):'+'</td>'+'<td>'+cuf+'</td>'+'</tr>'+'<tr>'+'<td>'+'WPD (w/sq.m):'+'</td>'+'<td>'+wpd+'</td>'+'</tr>'+'<tr>'+'<td>'+'Wind Speed (m/s):'+'</td>'+'<td>'+windspeed+'</td>'+'</tr>'+'</table>'+'</body>'+'</html>';
        	map.infoWindow.setContent(str);
            }
}

