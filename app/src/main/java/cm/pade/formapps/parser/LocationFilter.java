package cm.pade.formapps.parser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.List;

public class LocationFilter {

    Context context;
    Location bestLocation;
    Activity activity;

    public LocationFilter() {
    }

    public LocationFilter(Context context, Location location) {
        this.context = context;
        this.setBestLocation(this.getLastKnownLocation(location));
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Location getBestLocation() {
        return bestLocation;
    }

    public void setBestLocation(Location bestLocation) {
        this.bestLocation = bestLocation;
    }

    public Location getLastKnownLocation(Location location){
        if( ActivityCompat.checkSelfPermission( this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ){
            return null;
        }
        LocationManager locationManager =
                (LocationManager) this.getContext().getSystemService( this.getContext().LOCATION_SERVICE );
        List<String> providers = locationManager.getProviders( true );
        for( String provider : providers ){
            Location l = locationManager.getLastKnownLocation( provider );
            if( l == null ){
                continue;
            }
            if( location == null || l.getAccuracy() < location.getAccuracy() ){
                location = l; // Found best last known location;
            }
        }
        return location;
    }

}
