package models;

/**
 * Created by mike on 12 Mar 2018.
 */

public class BillModel {
String listname;
   public BillModel(){

   }
    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String status;
    String url;
}
