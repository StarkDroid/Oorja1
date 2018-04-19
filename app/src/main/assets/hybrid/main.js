    var map;
    var hybridlayer;
    var str;
    var lat;
    var lng;
    var x;
    var y;
    var hlatitude;
    var hlongitude;
    var hstate;
    var hdistrict;
    var htaluk;
    var hghi1;
    var hghi2;
    var hgti;
    var haep;
    var hcufs;
    var hcufw;
    var hcufsw;
    var hwpd;
    var hwindspeed;

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

        hybridlayer = new ArcGISDynamicMapServiceLayer("http://14.139.172.6:6080/arcgis/rest/services/Solar_Wind_hybrid/MapServer", {
          "opacity" : 1
        });
        map.addLayer(hybridlayer);
        
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
        map.infoWindow.setTitle("NIWE Hybrid Map Data");
        map.infoWindow.show(evt.mapPoint, map.getInfoWindowAnchor(evt.screenPoint));
        }
      });
      
      
function sendh(){
     y = lat;
     x = lng;

      niweurl="http://14.139.172.6:6080/arcgis/rest/services/Solar_Wind_hybrid/MapServer/1/query?where=1%3D1&text=&objectIds=&time=&geometry="+x+"%2C+"+y+"&geometryType=esriGeometryPoint&inSR=&spatialRel=esriSpatialRelIntersects&relationParam=&outFields=*&returnGeometry=true&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVersion=&returnDistinctValues=false&resultOffset=&resultRecordCount=&f=pjson";
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
                hlatitude = parsetxtraw.features[0].attributes.Latitude_1;
                hlongitude =parsetxtraw.features[0].attributes.Longitud_1;
                hstate=parsetxtraw.features[0].attributes.STATE_1;
                hdistrict =parsetxtraw.features[0].attributes.DISTRICT;
                htaluk=parsetxtraw.features[0].attributes.TALUK;
                hghi1=parsetxtraw.features[0].attributes.GHI_kWh;
                hghi2=parsetxtraw.features[0].attributes.GHI_W_Sq_m;
                hgti=parsetxtraw.features[0].attributes.GTI;
                haep=parsetxtraw.features[0].attributes.AEP_kWh;
                hcufs=parsetxtraw.features[0].attributes.CUF_1;
                hcufw = parsetxtraw.features[0].attributes.CUF__Wind_;
                hcufsw = parsetxtraw.features[0].attributes.CUF_SW;
                hwpd = parsetxtraw.features[0].attributes.wpd1;
                hwindspeed = parsetxtraw.features[0].attributes.WS;
            str = '<html>'+
                '<body>'+
                '<table cellspacing="5">'+
                '<tr>'+'<th>'+'LOCATION DATA'+'</th>'+'<th>'+'</th>'+'</tr>'+
                '<tr>'+'<td>'+'Latitude :'+'</td>'+'<td>'+hlatitude+'</td>'+'</tr>'+
                '<tr>'+'<td>'+'Longitude :'+'</td>'+'<td>'+hlongitude+'</td>'+
                '</tr>'+'<tr>'+'<td>'+'State :'+'</td>'+'<td>'+hstate+'</td>'+
                '</tr>'+'<tr>'+'<td>'+'District :'+'</td>'+'<td>'+hdistrict+'</td>'+'</tr>'+
                '</tr>'+'<tr>'+'<td>'+'Taluk :'+'</td>'+'<td>'+htaluk+'</td>'+'</tr>'+
                '<tr>'+'<th>'+'SOLAR DATA'+'</th>'+'<th>'+'</th>'+'</tr>'+
                '<tr>'+'<td>'+'GHI (kWh):'+'</td>'+'<td>'+hghi1+'</td>'+'</tr>'+
                '<tr>'+'<td>'+'GHI (W/sq.m/year):'+'</td>'+'<td>'+hghi2+'</td>'+'</tr>'+
                '<tr>'+'<td>'+'GTI'+'</td>'+'<td>'+hgti+'</td>'+'</tr>'+
                '<tr>'+'<td>'+'AEP (MW) :'+'</td>'+'<td>'+haep+'</td>'+'</tr>'+
                '<tr>'+'<td>'+'CUF(%) :'+'</td>'+'<td>'+hcufs+'</td>'+'</tr>'+
                '<tr>'+'<th>'+'WIND DATA'+'</th>'+'<th>'+'</th>'+'</tr>'+
                '<tr>'+'<td>'+'CUF(%):'+'</td>'+'<td>'+hcufw+'</td>'+'</tr>'+
                '<tr>'+'<td>'+'WPD (w/sq.m):'+'</td>'+'<td>'+hwpd+'</td>'+'</tr>'+
                '<tr>'+'<td>'+'Wind Speed (m/s):'+'</td>'+'<td>'+hwindspeed+'</td>'+'</tr>'+
                '<tr>'+'<th>'+'HYBRID MAP DATA'+'</th>'+'<th>'+'</th>'+'</tr>'+
                '<tr>'+'<td>'+' SOLAR WIND CUF(%):'+'</td>'+'<td>'+hcufsw+'</td>'+'</tr>'+
                '</table>'+
                '</body>'+
                '</html>' ;
            map.infoWindow.setContent(str);
            }
}

