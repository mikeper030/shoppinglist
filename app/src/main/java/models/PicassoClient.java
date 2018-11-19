package models;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.ultimatetoolsil.list.R;

/**
 * Created by mike on 13 Mar 2018.
 */

public class PicassoClient {
    public  static  void DownloadSingleImage(Context c, String url, ImageView img, int degrees)
    {
        if (url!=null && url.length()>0)
        {
            Log.d("picasso","downloading...");
            Picasso.with(c).load(url).placeholder(R.drawable.no_image).rotate(degrees).fit().into(img);

        }else
        {
            Picasso.with(c).load(R.drawable.no_image).into(img);
        }
    }

}
