package com.perusdajepara.jeparamart.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.perusdajepara.jeparamart.R;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class BannerDetailFragment extends Fragment {


    public BannerDetailFragment() {
        // Required empty public constructor
    }

    View rootView;
    ImageView bannerImg;
    TextView bannerTitle, bannerDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_banner_detail, container, false);

        bannerImg = rootView.findViewById(R.id.banner_img);
        bannerTitle = rootView.findViewById(R.id.banner_title);
        bannerDesc = rootView.findViewById(R.id.banner_desc);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String img = getArguments().getString("imgBanner");
        String desc = getArguments().getString("descBanner");
        String title = getArguments().getString("titleBanner");

        Picasso.with(getContext()).load(img).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(bannerImg);
        bannerTitle.setText(title);
        bannerDesc.setText(desc);
    }
}
