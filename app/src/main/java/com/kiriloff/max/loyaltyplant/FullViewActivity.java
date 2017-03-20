package com.kiriloff.max.loyaltyplant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiriloff.max.loyaltyplant.Interfaces.ApiInterface;
import com.kiriloff.max.loyaltyplant.Interfaces.MyCallBack;
import com.kiriloff.max.loyaltyplant.api.ApiClient;
import com.kiriloff.max.loyaltyplant.custom_tabs_client.CustomTabActivityHelper;
import com.kiriloff.max.loyaltyplant.model.FullMovie;
import com.kiriloff.max.loyaltyplant.model.Movie;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FullViewActivity extends AppCompatActivity {

    private final static String TAG = FullViewActivity.class.getSimpleName();
    private final static String BUNDLE_MOVIE = FullViewActivity.class.getSimpleName() + "MOVIE";
    private final static String BUNDLE_FULL_MOVIE = FullViewActivity.class.getSimpleName() + "FULLMOVIE";
    private final static String IMAGE_KEY = "https://image.tmdb.org/t/p/w500";

    private ImageView backdrop_path;
    private TextView title;
    private TextView tagline;
    private TextView overview;
    private TextView genres_data;
    private TextView tv_vl_rating;
    private TextView production_countries_data;
    private FullMovie fullMovie;
    private Movie movie;
    private Context context;
    private Button go_to_homepage_button;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private AppBarLayout appBarLayout;
    private NestedScrollView nestedScrollView;

    private MyCallBack myCallBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullview);
        contentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar_full_view);
        context = getApplicationContext();
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
            bindView();
            initToolbar();
            setData();
            initClick();
            showView();
        } else {
            contentLoadingProgressBar.show();
            if (intent != null) {
                movie = (Movie) intent.getSerializableExtra(MainActivity.EXTRA_MOVIE);
            }
            bindView();
            initToolbar();
            getData();
            initClick();
            myCallBack = new MyCallBack() {
                @Override
                public void callingBack() {
                    setData();
                    showView();
                }

                @Override
                public void onFailure() {
                }
            };
        }
    }

    private void showView() {
        if (contentLoadingProgressBar.isShown()) {
            contentLoadingProgressBar.hide();
        }
        appBarLayout.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_MOVIE, (Serializable) movie);
        outState.putSerializable(BUNDLE_FULL_MOVIE, (Serializable) fullMovie);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movie = (Movie) savedInstanceState.getSerializable(BUNDLE_MOVIE);
        fullMovie = (FullMovie) savedInstanceState.getSerializable(BUNDLE_FULL_MOVIE);

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void bindView() {
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        nestedScrollView = (NestedScrollView) findViewById(R.id.scroll);
        backdrop_path = (ImageView) findViewById(R.id.iv_vl_backdrop_path);
        title = (TextView) findViewById(R.id.tv_vl_title);
        tv_vl_rating = (TextView) findViewById(R.id.tv_vl_rating);
        tagline = (TextView) findViewById(R.id.tv_vl_tagline);
        overview = (TextView) findViewById(R.id.tv_vl_overview);
        genres_data = (TextView) findViewById(R.id.tv_vl_genres_data);
        production_countries_data = (TextView) findViewById(R.id.tv_vl_production_countries_data);
        go_to_homepage_button = (Button) findViewById(R.id.go_to_homepage);
    }

    public void getData() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<FullMovie> call = apiService.getMovieDetails(movie.getId(), MainActivity.API_KEY);
        call.enqueue(new Callback<FullMovie>() {
            @Override
            public void onResponse(Call<FullMovie> call, Response<FullMovie> response) {
                fullMovie = response.body();
                myCallBack.callingBack();
            }

            @Override
            public void onFailure(Call<FullMovie> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_full_view_layout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getData();
                            }
                        });
                snackbar.show();
                Log.e(TAG, "getData" + t.toString());
                myCallBack.onFailure();

            }
        });
    }

    private void setData() {
        Picasso.with(context)
                .load(IMAGE_KEY + movie.getBackdropPath())
                .into(backdrop_path);
        title.setText(movie.getTitle());
        overview.setText(movie.getOverview());
        tagline.setText(fullMovie.getTagline());
        tv_vl_rating.setText(movie.getVoteAverage().toString());
        genres_data.setText(fullMovie.printGenre());
        production_countries_data.setText(fullMovie.printProductionCountries());
        if (!fullMovie.getHomepage().equals("")) {
            go_to_homepage_button.setVisibility(View.VISIBLE);
            go_to_homepage_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startCustomChromeTab(fullMovie.getHomepage());
                }
            });
        }
    }

    private void initClick() {

    }

    private void startCustomChromeTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, Uri.parse(url),
                new CustomTabActivityHelper.CustomTabFallback() {
                    @Override
                    public void openUri(Activity activity, Uri uri) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
    }
}





