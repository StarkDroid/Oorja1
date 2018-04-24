

var map;
var mapOptions;
var latlng;
var marker;
var markerOptions;
var infoWindow;
var infoWindowOptions;
var marker1;
var markerOptions1;
var infoWindowOptions1;
var infoWindow1;
var autocomplete;
var place;
var marker2;
var markerOptions2;
var infoWindow2;
var infoWindowOptions2;
var markerlat;
var markerlng;
var str;
var ndata ;
var parsetxtraw;
var x;
var y;
var niweurl;

var latitude;
var longitude;
var state;
var district;
var taluk;
var ghi;
var dni;
var dhi;
var aep;
var cuf;

var t = 0.0;
var d;
var b = 0;
var i = 1;
var e = 0.0;
var cuf = 0.0 ;
var numerator = 0.0;
var denominator = 0.0;
var cuf_int = 0.0;
var lat_int;
var money = 0;
var rate = 8;
var money_int;
var total;
var power_plant_capacity;
var area_value;
var area;
var aep_value;
var aep_value_int;
var std;
var norminv;
var p;
var mu;
var sigma;
var P90;
var P70;
var panel_area;
var panels_required;
var P70_value;
var P90_value;


function initMap(){

    //creating a lat lng object
   latlng =  new google.maps.LatLng(12.956613,80.214293);

   // creating a map object
   mapOptions={
            center: latlng,
            zoom: 12
   };

  map = new google.maps.Map(document.getElementById('map'),mapOptions);

  // creating a marker object
  markerOptions = {
            position: latlng,
            map:map,
            draggable:true,
            animation: google.maps.Animation.DROP,
            title:"NIWE SEARCH"
  };
  marker = new  google.maps.Marker(markerOptions);

  // creating a info window
   infoWindowOptions = {
       content: 'NIWE!'
   };
   infoWindow = new google.maps.InfoWindow(infoWindowOptions);

   google.maps.event.addListener(marker,'click',function(){
       infoWindow.open(map,marker);
   });

// creating an autocomplete textbox

        autocomplete = new google.maps.places.Autocomplete(document.getElementById('autocomplete'));
        // creating autocomplete event listener
        google.maps.event.addListener(autocomplete, 'place_changed', function() {
            // close previous info in infowindow
            infoWindow.close();
            // creating the place variable
               place = autocomplete.getPlace();


            // creating a marker at the position of autocomplete
                    markerOptions2 = {
                    map:map,
                    draggable:true,
                    animation: google.maps.Animation.DROP,
                    title:"NIWE SEARCH"
                };
                marker.setOptions(markerOptions2);
                marker.setPosition(place.geometry.location);

            // creating a info window at the autocomplete location

                 // get lat lng value from marker position
                        markerlat = marker.getPosition().lat();
                        markerlng = marker.getPosition().lng();
                        //creating autocomplete infowindow
                        send();



            // setting map wndow bounds for the auto complete location
                if (place.geometry.viewport) {
                map.fitBounds(place.geometry.viewport);
                } else {
                map.setCenter(place.geometry.location);
                map.setZoom(17);
                }



        });

    // dynamic marker
    google.maps.event.addListener(map, 'click', function(event) {
    placeMarker(event.latLng);
  });

  //this is the drawable and geometry class used for area measurement
  var drawingManager = new google.maps.drawing.DrawingManager({
          drawingMode: google.maps.drawing.OverlayType.MARKER,
          drawingControl: true,
          drawingControlOptions: {
            position: google.maps.ControlPosition.BOTTOM_CENTER,
            drawingModes: ['polygon']
          },
          markerOptions: {
              draggable: true,
              editable: true
          },
         polygonOptions: {
            editable: true
         }
        });
        drawingManager.setMap(map);

         google.maps.event.addListener(drawingManager, 'overlaycomplete', function(e) {
    if (e.type !== google.maps.drawing.OverlayType.MARKER) {
      drawingManager.setDrawingMode(null);
      var newShape = e.overlay;
      newShape.type = e.type;
      google.maps.event.addListener(newShape, 'click', function() {
        setSelection(newShape);
      });
      area_value = google.maps.geometry.spherical.computeArea(newShape.getPath());
      area = parseInt(area_value.toString());
      document.getElementById("area").innerHTML = "Area = " + area+" sq.m";
      power_plant_capacity  = area/10;
      document.getElementById("capacity").innerHTML = "Power Plant Capacity = "+power_plant_capacity+" (kwh)";
      panels_required = power_plant_capacity / 0.250;
      panel_area = panels_required*1.64;
      aep_value = panel_area*0.184*ghi*0.77;
      aep_value_int =parseInt(aep_value.toString());
      document.getElementById("aep").innerHTML = "AEP = "+aep_value_int+" (kwh/sq.m/year)";
      d = 0.05;
      e = aep_value_int;
      for(i=1; i<=25; i++){
          b = d*e;
          e = e - b;
          t = t + e;
      }
      total = parseInt(t.toString());
      document.getElementById("total").innerHTML = "Total Energy Generated in 25 YEARS = "+total+" (kwh)";
      money = aep_value_int * rate;
      money_int =  parseInt(money.toString());
      document.getElementById("money").innerHTML = "Financial Savings = ₹"+money_int;
      numerator = aep_value_int * 100;
      denominator = power_plant_capacity * 8760;
      cuf = numerator / denominator;
      cuf_int = parseInt(cuf.toString());
      document.getElementById("cuf").innerHTML = " Capacity Utilization Factor = "+cuf_int+" %";
      lat_int = parseInt(markerlat.toString());
      document.getElementById("position").innerHTML = " Panel Position : South Facing with Tilt of "+lat_int+" degrees";
      var carbon;
      carbon = aep_value_int;
      document.getElementById("carbon").innerHTML = " CO2 level mitigated : "+carbon+" kg";
      exceedence();
      setSelection(newShape);
    }
  });

}
// end of init function

//
// calling dynamic marker outside of init function
function placeMarker(location) {
     // close previous info in infowindow
            infoWindow.close();
    // creating dynamic marker
  markerOptions1 = {
    position: location,
    draggable:true,
    animation: google.maps.Animation.DROP,
    map: map
  };
  marker.setOptions(markerOptions1);



            // get lat lng value from marker position
            markerlat = marker.getPosition().lat();
            markerlng = marker.getPosition().lng();
            //creating dynamic infowindow
            send();

}



// fetching NIWE values


function send(){
 y = markerlat;
 x = markerlng;

  niweurl="http://14.139.172.6:6080/arcgis/rest/services/Solar_Radiation_Map_of_India/MapServer/5/query?where=&text=&objectIds=&time=&geometry=x%3D"+x+"%2Cy%3D"+y+"&geometryType=esriGeometryPoint&inSR=4326&spatialRel=esriSpatialRelIntersects&relationParam=&outFields=*&returnGeometry=false&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVersion=&returnDistinctValues=false&resultOffset=&resultRecordCount=&f=pjson";
  $(function(){
     $.ajax({
        type: 'GET',
        url: niweurl ,
        success: function(data){
      ndata =('',data);
      output(ndata);
        }
     });
 });

 }



  // output from NIWE
function output(b){

     var parsetxtraw = JSON.parse(b);


                latitude = parsetxtraw.features[0].attributes.Latitude;
                longitude =parsetxtraw.features[0].attributes.Longitude;
                state=parsetxtraw.features[0].attributes.STATE;
                district =parsetxtraw.features[0].attributes.DISTRICT;
                taluk=parsetxtraw.features[0].attributes.TALUK;
                ghi=parsetxtraw.features[0].attributes.GHI;
                dni=parsetxtraw.features[0].attributes.DNI;
                dhi=parsetxtraw.features[0].attributes.DHI;
                aep=parsetxtraw.features[0].attributes.AEP;
                cuf=parsetxtraw.features[0].attributes.CUF;
            str = '<html>'+'<body>'+'<table cellspacing="5">'+'<tr>'+'<td>'+'Latitude :'+'</td>'+'<td>'+latitude+'</td>'+'</tr>'+'<tr>'+'<td>'+'Longitude :'+'</td>'+'<td>'+longitude+'</td>'+'</tr>'+'<tr>'+'<td>'+'State :'+'</td>'+'<td>'+state+'</td>'+'</tr>'+'<tr>'+'<td>'+'District :'+'</td>'+'<td>'+district+'</td>'+'</tr>'+'<tr>'+'<td>'+'Taluk :'+'</td>'+'<td>'+taluk+'</td>'+'</tr>'+'<tr>'+'<td>'+'GHI :'+'</td>'+'<td>'+ghi+'</td>'+'</tr>'+'<tr>'+'<td>'+'DHI :'+'</td>'+'<td>'+dhi+'</td>'+'</tr>'+'<tr>'+'<td>'+'DNI :'+'</td>'+'<td>'+dni+'</td>'+'</tr>'+'<tr>'+'<td>'+'AEP :'+'</td>'+'<td>'+aep+'</td>'+'</tr>'+'<tr>'+'<td>'+'CUF :'+'</td>'+'<td>'+cuf+'</td>'+'</tr>'+'</table>'+'</body>'+'</html>' ;
           infoWindow.setContent(str);
}

function normsInv(p, mu, sigma)
{
    if (p < 0 || p > 1)
    {
        throw "The probality p must be bigger than 0 and smaller than 1";
    }
    if (sigma < 0)
    {
        throw "The standard deviation sigma must be positive";
    }

    if (p == 0)
    {
        return -Infinity;
    }
    if (p == 1)
    {
        return Infinity;
    }
    if (sigma == 0)
    {
        return mu;
    }

    var q, r, val;

    q = p - 0.5;

    /*-- use AS 241 --- */
    /* double ppnd16_(double *p, long *ifault)*/
    /*      ALGORITHM AS241  APPL. STATIST. (1988) VOL. 37, NO. 3
            Produces the normal deviate Z corresponding to a given lower
            tail area of P; Z is accurate to about 1 part in 10**16.
    */
    if (Math.abs(q) <= .425)
    {/* 0.075 <= p <= 0.925 */
        r = .180625 - q * q;
        val =
               q * (((((((r * 2509.0809287301226727 +
                          33430.575583588128105) * r + 67265.770927008700853) * r +
                        45921.953931549871457) * r + 13731.693765509461125) * r +
                      1971.5909503065514427) * r + 133.14166789178437745) * r +
                    3.387132872796366608)
               / (((((((r * 5226.495278852854561 +
                        28729.085735721942674) * r + 39307.89580009271061) * r +
                      21213.794301586595867) * r + 5394.1960214247511077) * r +
                    687.1870074920579083) * r + 42.313330701600911252) * r + 1);
    }
    else
    { /* closer than 0.075 from {0,1} boundary */

        /* r = min(p, 1-p) < 0.075 */
        if (q > 0)
            r = 1 - p;
        else
            r = p;

        r = Math.Sqrt(-Math.log(r));
        /* r = sqrt(-log(r))  <==>  min(p, 1-p) = exp( - r^2 ) */

        if (r <= 5)
        { /* <==> min(p,1-p) >= exp(-25) ~= 1.3888e-11 */
            r += -1.6;
            val = (((((((r * 7.7454501427834140764e-4 +
                       .0227238449892691845833) * r + .24178072517745061177) *
                     r + 1.27045825245236838258) * r +
                    3.64784832476320460504) * r + 5.7694972214606914055) *
                  r + 4.6303378461565452959) * r +
                 1.42343711074968357734)
                / (((((((r *
                         1.05075007164441684324e-9 + 5.475938084995344946e-4) *
                        r + .0151986665636164571966) * r +
                       .14810397642748007459) * r + .68976733498510000455) *
                     r + 1.6763848301838038494) * r +
                    2.05319162663775882187) * r + 1);
        }
        else
        { /* very close to  0 or 1 */
            r += -5;
            val = (((((((r * 2.01033439929228813265e-7 +
                       2.71155556874348757815e-5) * r +
                      .0012426609473880784386) * r + .026532189526576123093) *
                    r + .29656057182850489123) * r +
                   1.7848265399172913358) * r + 5.4637849111641143699) *
                 r + 6.6579046435011037772)
                / (((((((r *
                         2.04426310338993978564e-15 + 1.4215117583164458887e-7) *
                        r + 1.8463183175100546818e-5) * r +
                       7.868691311456132591e-4) * r + .0148753612908506148525)
                     * r + .13692988092273580531) * r +
                    .59983220655588793769) * r + 1);
        }

        if (q < 0.0)
        {
            val = -val;
        }
    }
    norminv = mu + sigma * val;
    return norminv;
}

  function  exceedence(){
      std = aep_value_int * 0.025;
      P90 = normsInv(0.1,aep_value_int,std);
      P90_value = parseInt(P90.toString());
      P70 = normsInv(0.3,aep_value_int,std);
      P70_value = parseInt(P70.toString());
      document.getElementById("exceedence").innerHTML = "  <br> Probability exceedence <br><br> P50 =   "+aep_value_int+"  (kwh/sq.m/year)  <br> P90 =   "+P90_value+"   (kwh/sq.m/year) <br> P70 =   "+P70_value+"  (kwh/sq.m/year) <br>";
  }





















