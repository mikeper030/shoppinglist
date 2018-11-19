package com.ultimatetoolsil.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import models.AdvancedImageView;
import models.PicassoClient;

/**
 * Created by mike on 16 Mar 2018.
 */

public class imageViewerFragment extends Fragment {

    @Override
    public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AdvancedImageView imageView= (AdvancedImageView) view.findViewById(R.id.img);

        Bundle args = getArguments();
        if (args != null) {
            Log.d("file",args.getString("link"));
            PicassoClient.DownloadSingleImage(getActivity(),args.get("link").toString(),imageView,0);
        }



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_viewer,
                container, false);
        return view;
    }

}
