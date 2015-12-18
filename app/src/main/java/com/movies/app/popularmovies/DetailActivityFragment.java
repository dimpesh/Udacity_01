package com.movies.app.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    String baseUrlImage="http://image.tmdb.org/t/p/w185/";
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent=getActivity().getIntent();
        Bundle bundle= intent.getExtras();

        MovieObject movieRecieved= (MovieObject) bundle.getSerializable("MovieObjectSent");

        TextView overview= (TextView) rootView.findViewById(R.id.movie_overview);
        TextView title= (TextView) rootView.findViewById(R.id.movie_title);
        ImageView movieImage= (ImageView) rootView.findViewById(R.id.movie_poster);
        TextView movie_vote_average= (TextView) rootView.findViewById(R.id.movie_vote_average);
        TextView movie_release_date= (TextView) rootView.findViewById(R.id.movie_release_date);
        Picasso.with(getContext()).load(baseUrlImage+movieRecieved.poster_path).into(movieImage);
        title.setText(movieRecieved.title);
        movie_release_date.setText(movieRecieved.release_date);
        movie_vote_average.setText(movieRecieved.vote_average+"/10");
        overview.setText(movieRecieved.overview);
        return rootView;
    }
}
