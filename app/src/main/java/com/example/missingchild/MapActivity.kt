package com.example.missingchild



import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.data.ServiceFeatureTable
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.layers.FeatureLayer
import com.arcgismaps.mapping.symbology.PictureMarkerSymbol
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbol
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbolStyle
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.mapping.view.MapView
import java.io.File


private lateinit var mapView:MapView

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_layout)
        mapView=findViewById(R.id.mapView)


        var latitude:Double= intent.getDoubleExtra("lat",0.0)
        var longitude:Double=intent.getDoubleExtra("lon",0.0)
//        lifecycle.addObserver(mapView)
        println("MapActivity"+latitude.toString()+" "+longitude.toString())




        ArcGISEnvironment.apiKey = ApiKey.create("AAPKb7e4c56e969046c6a79b808627ecfe60cNmqi-kRTHAHNqQDB8tMpfMApkplS6devp1q0tRVIRjUAAHL0iJ3MRpoK9qTxaUj")
        // Create a map with a basemap

        val map = ArcGISMap(BasemapStyle.ArcGISImagery)

        val trailheadsLayer = FeatureLayer.createWithFeatureTable(
            ServiceFeatureTable("https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Trailheads_Styled/FeatureServer/0")
        )
        val openStreetMap=FeatureLayer.createWithFeatureTable(
            ServiceFeatureTable("https://services3.arcgis.com/GVgbJbqm8hXASVYi/ArcGIS/rest/services/OpenStreetMap___Points_of_interest_(b%c3%a8ta)___Punten_Density/FeatureServer/0")
        )


//
//        val trailsLayer = FeatureLayer.createWithFeatureTable(
//            ServiceFeatureTable("https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Trails_Styled/FeatureServer/0"))
//
        val openSpacesLayer = FeatureLayer.createWithFeatureTable(
            ServiceFeatureTable("https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Parks_and_Open_Space_Styled/FeatureServer/0"))
//


        // Apply the marker symbol to the feature layer
        val symbol =
            SimpleMarkerSymbol(SimpleMarkerSymbolStyle.Diamond, com.arcgismaps.Color.red, 20.0f)
//        val resourceId = R.drawable.ic_menu_mylocation // Replace with your location icon resource
        val image =
//            File(System.getProperty("data.dir"), "E://Missingchild//app//src//main//res//drawable//map_1.png")
//        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_mylocation)
//        val pictureMarkerSymbol =  PictureMarkerSymbol(image.absolutePath);
//        pictureMarkerSymbol.height = 36f // Set the height of the symbol
//        pictureMarkerSymbol.width = 36f

//        mapView.renderer = renderer
        map.operationalLayers.addAll(listOf(openSpacesLayer, openStreetMap, trailheadsLayer))


//        val buildingsLayer = OpenStreetMapLayer(OpenStreetMapLayer.Type.BUILDINGS)
//        map.operationalLayers.add(buildingsLayer)


        // set the map to be displayed in the map view
        mapView.map = map

        // Add the mapview to the lifecycle scope.
        lifecycle.addObserver(mapView)




        // Create a point using latitude and longitude coordinates
//        val latitude = 15.519742
//        val longitude = 80.0378755
        val point = Point(longitude, latitude, SpatialReference.wgs84())

        val graphicsOverlay = GraphicsOverlay()
        mapView.graphicsOverlays.add(graphicsOverlay)

        // Set the initial viewpoint to the point
        val po = Viewpoint(point, 10000.0)
        val graphic = Graphic(point, symbol)

        graphicsOverlay.graphics.add(graphic)

        mapView.setViewpoint(po)
        // Set the map to the MapView

    }




}
