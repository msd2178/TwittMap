<%@page import="TweetHelp.QueryDB"%>
<%@ page import="TweetHelp.QueryDB.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map"%>
<%@ page import="java.lang.InterruptedException" %>
<%@ page import="com.amazonaws.services.dynamodbv2.model.AttributeValue"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>
<html>
  <head>
    <style type="text/css">
      html, body, #map-canvas { height: 100%; margin: 0; padding: 0;}
    </style>
    <script type="text/javascript"
      src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCcsrwleinPdgBpSfsScSLcY-AjRE7qMJA">
    </script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&libraries=visualization"></script>
    <script type="text/javascript">
    
    <%String keyWord = request.getParameter("key");%>
    
    var taxiData = [
    <%
    QueryDB query = new QueryDB();
    List<Map<String, AttributeValue>> locations = query.getKeyLocations(keyWord);
    String latitude = "100",longitude = "10";
    for (Map<String, AttributeValue> item : locations) {
    	for (Map.Entry<String, AttributeValue> i : item.entrySet()) {
    		String attribute = i.getKey();
    		if(attribute.equals("Latitude"))
    		{
    			latitude = i.getValue().getN();
    		}
    		if(attribute.equals("Longitude"))
    		{
    			longitude = i.getValue().getN();
    		}
    	}
    %>
    	new google.maps.LatLng(<%out.println(latitude);%>,<%out.println(longitude);%>),
    	
   <%}%>];
    
    
    var markers = [];
    var map;
    
    function initialize() {
        var mapOptions = {
          center: { lat: -0, lng: 0},
          zoom: 2
        };
        map = new google.maps.Map(document.getElementById('map-canvas'),
            mapOptions);
        
        // Map all the markers 
        // Get the data from dynamo 
        // Plot the markers
        for(var i=0;i < taxiData.length;i++)
        {
        	addMarker(taxiData[i]);
        }
        
        var pointArray = new google.maps.MVCArray(taxiData);

        heatmap = new google.maps.visualization.HeatmapLayer({
          data: pointArray
        });

        heatmap.setMap(null);
        
        
      }
    
    function addMarker(location) {
    	  var marker = new google.maps.Marker({
    	    position: location,
    	    map: map
    	  });
    	  markers.push(marker);
    }
    
    
    
    function setAllMap(map) {
    	  for (var i = 0; i < markers.length; i++) {
    	    markers[i].setMap(map);
    	  }
    }
    
    function clearMarkers() {
    	  setAllMap(null);
    }
    
    function showMarkers() {
    	  setAllMap(map);
    }
    
    function toggleHeatmap() {
    	  heatmap.setMap(heatmap.getMap() ? null : map);
    	  setAllMap(heatmap.getMap() ? null : map);
    }

    function changeGradient() {
    	  var gradient = [
    	    'rgba(0, 255, 255, 0)',
    	    'rgba(0, 255, 255, 1)',
    	    'rgba(0, 191, 255, 1)',
    	    'rgba(0, 127, 255, 1)',
    	    'rgba(0, 63, 255, 1)',
    	    'rgba(0, 0, 255, 1)',
    	    'rgba(0, 0, 223, 1)',
    	    'rgba(0, 0, 191, 1)',
    	    'rgba(0, 0, 159, 1)',
    	    'rgba(0, 0, 127, 1)',
    	    'rgba(63, 0, 91, 1)',
    	    'rgba(127, 0, 63, 1)',
    	    'rgba(191, 0, 31, 1)',
    	    'rgba(255, 0, 0, 1)'
    	  ]
    	  heatmap.set('gradient', heatmap.get('gradient') ? null : gradient);
    }

    function changeRadius() {
    	  heatmap.set('radius', heatmap.get('radius') ? null : 20);
    }

    function changeOpacity() {
    	 heatmap.set('opacity', heatmap.get('opacity') ? null : 0.2);
    }

     google.maps.event.addDomListener(window, 'load', initialize);
      
      
    </script>
  </head>
  <body>
  	<div id="panel">
      <button onclick="toggleHeatmap();" type=button>Toggle Heatmap</button>
      <button onclick="changeGradient();" type=button>Change gradient</button>
      <button onclick="changeRadius();" type=button>Change radius</button>
      <button onclick="changeOpacity();" type=button>Change opacity</button>
      <input onclick="clearMarkers();" type=button value="Hide Markers">
      <input onclick="showMarkers();" type=button value="Show All Markers">
      <form action="key.jsp" method="POST">
      <input type="text" id="key" name="key" />
      <input type="submit" value="Go" />
    </form>
    </div>
	<div id="map-canvas"></div>
  </body>
</html>