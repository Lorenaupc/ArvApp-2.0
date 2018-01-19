package arvapp.navigation;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



/*I've made this: http://stackoverflow.com/questions/31350158/android-app-is-crashing-tabs-and-google-maps
because the problem is that you try to inflate a map when it's created, so you have to check what happens
when is the first time you inflate the layout and when it's not. It was hard to handle, so finally took this
solution where it creates the map when it's not inflated and updates it when it exists.
 */


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback{

    private static GoogleMap mMap;
    public MapFragment(){

    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        SupportMapFragment mapFragmentaux = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragmentaux == null) {
            View rootview = inflater.inflate(R.layout.map_layout, container, false);
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            return rootview;
        }
        else if (mapFragmentaux != null){
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }*/
}