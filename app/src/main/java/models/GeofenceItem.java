package models;

import com.google.android.gms.location.places.Place;

/**
 * Created by mike on 8 Mar 2018.
 */

public class GeofenceItem {
String  listname;
Place place;
public GeofenceItem(){

}
    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }
}
