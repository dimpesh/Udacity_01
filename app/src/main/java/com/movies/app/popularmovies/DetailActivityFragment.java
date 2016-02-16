package com.movies.app.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    String baseUrlImage="http://image.tmdb.org/t/p/w185/";
    TrailerAdapter myTrailerAdapter;
    TrailerObject[] trailerObjects;
    List<String> trailerKeyList;
    String LOG_TAG= DetailActivityFragment.class.getSimpleName();
    String MyAppString = "#SpotifyStreamer";
    String reviewStr;
    String trailer_string;
    int cnt=0;
    TextView reviewView;
    public DetailActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent=getActivity().getIntent();
        Bundle bundle= intent.getExtras();
        String API_KEY="api_key";
   //     trailerAdapter=new TrailerAdapter(getActivity(),R.layout.list_item_trailer,R.id.trailer_image,trailerKeyList);
        FloatingActionButton updateFavouriteButton= (FloatingActionButton) rootView.findViewById(R.id.favbtn);
        // Trailer Object Populate the Trailer ListView
        trailerObjects= new TrailerObject[]{new TrailerObject(), new TrailerObject()};

        List<TrailerObject> listmovie=new ArrayList<TrailerObject>(Arrays.asList(trailerObjects));
        myTrailerAdapter=new TrailerAdapter(getActivity(),R.layout.trailer_item_imageview,R.id.gridview_movies_imageview,listmovie);


        MovieObject movieRecieved= (MovieObject) bundle.getSerializable("MovieObjectSent");
        ImageView movieBackdrop= (ImageView) rootView.findViewById(R.id.movie_backdrop);
        ImageView moviePoster= (ImageView) rootView.findViewById(R.id.movie_poster);
        TextView movie_vote_average= (TextView) rootView.findViewById(R.id.movie_vote_average);
        TextView movie_release= (TextView) rootView.findViewById(R.id.movie_release);
        Picasso.with(getContext()).load(baseUrlImage+movieRecieved.backdrop_path).into(movieBackdrop);
        Picasso.with(getContext()).load(baseUrlImage+movieRecieved.poster_path).into(moviePoster);
        getActivity().setTitle(movieRecieved.title);
        new DataFetcher().execute(movieRecieved.id);
       // new TrailerFetcher().execute(movieRecieved.id);
   //     new TrailerFetcher().execute(movieRecieved.id);

        GridView trailerview= (GridView) rootView.findViewById(R.id.gridview_trailersview);
        trailerview.setAdapter(myTrailerAdapter);


        trailerview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TrailerObject trailerObject=myTrailerAdapter.getItem(position);
                String key=trailerObject.getKey();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key)));
            }
        });

        TextView overview= (TextView) rootView.findViewById(R.id.movie_overview);

        overview.setText(movieRecieved.overview);

        movie_release.setText(movieRecieved.release_date);
        movie_vote_average.setText(movieRecieved.vote_average);
        reviewView= (TextView) rootView.findViewById(R.id.movie_review);
      //  reviewView.setText(reviewStr);


        return rootView;


    }

    public class DataFetcher extends AsyncTask<String,Void,String>
    {
        String LOG_TAG= DataFetcher.class.getSimpleName();
        String API_KEY="api_key";
        String jsonStr;
        int cnt=1;
        String reviewStr="";
        String movie_id;
        BufferedReader reader=null;
        // String baseUrl="https://api.themoviedb.org/3/movie/286217/";
        HttpURLConnection urlConnection;

        @Override
        protected void onPostExecute(String str)
        {

            reviewView.setText(str);
            new TrailerFetcher().execute(movie_id);
        }

        @Override
        protected String doInBackground(String...strings) {
            //Uri buildUrl=Uri.parse(baseUrl+strings[0]).buildUpon().appendQueryParameter(API_KEY, getString(R.string.api_key)).build();
            try {
                movie_id=strings[0];
                URL url=new URL("https://api.themoviedb.org/3/movie/"+strings[0]+"/reviews?api_key=69323240f26aaa3f0ed513e2fd344a5f");
                Log.v(LOG_TAG+"VERBOSE",url.toString());
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream=urlConnection.getInputStream();
                StringBuffer buffer=new StringBuffer();
                if(inputStream==null)
                    jsonStr=null;

                reader=new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line=reader.readLine())!=null)
                {
                    buffer.append(line+"\n");
                }
                if(buffer.length()==0)
                {
                    jsonStr=null;
                }
                jsonStr=buffer.toString();
                JSONObject jsonObject=new JSONObject(jsonStr);
                JSONArray jsonArray =jsonObject.getJSONArray("results");
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject review = jsonArray.getJSONObject(i);
                    //JSONObject authorObject=review.getJSONObject("author");
                    String authorName=review.getString("author");
                    Log.v("\nauthor JSON",authorName);
                    //JSONObject contentObject=review.getJSONObject("content");
                    String content=review.getString("content").toString();
                    Log.v("\ncontent JSON ",authorName);
                    reviewStr=reviewStr+"\n \t\t:: REVIEW "+cnt+" ::\n\n"+authorName+"\n"+content+"\n";
                    cnt++;
                }
                Log.v(LOG_TAG,reviewStr);



            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return reviewStr;



        }
    }

    public class TrailerFetcher extends AsyncTask<String, Void, TrailerObject[]> {
        String LOG_TAG = TrailerFetcher.class.getSimpleName();
        String API_KEY = "api_key";
        String jsonStr;
        String myTrailer = "";
        //        String []trailer_key=null;
        BufferedReader reader = null;
        // String baseUrl="https://api.themoviedb.org/3/movie/286217/";
        HttpURLConnection urlConnection;

        @Override
        protected void onPostExecute(TrailerObject[] str) {

            if (str != null)
                myTrailerAdapter.clear();

            myTrailerAdapter.addAll(str);
            myTrailerAdapter.notifyDataSetChanged();
        }

        @Override
        protected TrailerObject[] doInBackground(String... strings) {
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/"+strings[0]+"/videos?api_key=69323240f26aaa3f0ed513e2fd344a5f");
                Log.v(LOG_TAG + "VERBOSE", url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                    jsonStr = null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    jsonStr = null;
                }
                jsonStr = buffer.toString();
                JSONObject trailerObject = new JSONObject(jsonStr);
                Log.v("VERBOSE",trailerObject.toString());
                JSONArray jsonArray = trailerObject.getJSONArray("results");
                trailerObjects = new TrailerObject[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    trailerObjects[i] = new TrailerObject();
                    JSONObject jsonArrayJSONObject = jsonArray.getJSONObject(i);
                    //JSONObject authorObject=review.getJSONObject("author");
                    trailerObjects[i].id = jsonArrayJSONObject.getString("id");
                    Log.v("ID VERBOSE",jsonArrayJSONObject.getString("key"));
                    trailerObjects[i].key = jsonArrayJSONObject.getString("key");
                    trailerObjects[i].name = jsonArrayJSONObject.getString("name");


                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return trailerObjects;
        }
    }



}

