package com.kiriloff.max.loyaltyplant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kiriloff.max.loyaltyplant.Interfaces.ApiInterface;
import com.kiriloff.max.loyaltyplant.Interfaces.MyClick;
import com.kiriloff.max.loyaltyplant.adapters.MoviesAdapter;
import com.kiriloff.max.loyaltyplant.api.ApiClient;
import com.kiriloff.max.loyaltyplant.model.Movie;
import com.kiriloff.max.loyaltyplant.model.MoviesResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public final static String API_KEY = "72b56103e43843412a992a8d64bf96e9";
    public final static String EXTRA_MOVIE = MainActivity.class.getName() + "MovieExtra";
    public final static String BUNDLE_MOVIE = MainActivity.class.getName() + "MovieArray";
    public final static String BUNDLE_CURRENT_PAGE = MainActivity.class.getName() + "currentPage";
    public final static String BUNDLE_SEARCH_RESULT_LIST = MainActivity.class.getName() + "searchListMovies;";
    public final static String BUNDLE_TOTAL_PAGE = MainActivity.class.getName() + "totalPage";
    public final static String BUNDLE_SEARCH_MODE = MainActivity.class.getName() + "searchMode";
    private final static String TAG = MainActivity.class.getSimpleName();

    private int totalPage;
    private int currentPage;
    private boolean searchMode = false;
    private List<Movie> movies;
    private List<Movie> searchListMovies;
    private RecyclerView recyclerView;
    private MyClick click;
    private Context context;
    private MoviesAdapter moviesAdapter;
    private ApiInterface apiService;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
            initToolbar();
            initService();
            initClick();
            initRecyclerView();
        } else {
            initToolbar();
            initService();
            initClick();
            initData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchMode = true;
                        if (movies != null) {
                            if (!newText.equals("")) {
                                searchListMovies = new ArrayList<>();
                                for (Movie temp : movies) {
                                    if (temp.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                                        searchListMovies.add(temp);
                                    }
                                }
                                moviesAdapter.setMovies(searchListMovies);
                                moviesAdapter.notifyDataSetChanged();
                                Snackbar.make(findViewById(R.id.activity_main), "Find Movies: " + searchListMovies.size(), Snackbar.LENGTH_SHORT).show();
                            } else {
                                moviesAdapter.setMovies(movies);
                                moviesAdapter.notifyDataSetChanged();
                                Snackbar.make(findViewById(R.id.activity_main), "All: " + movies.size(), Snackbar.LENGTH_SHORT).show();
                                searchMode = false;
                            }
                        }
                        return false;
                    }
                });
        return true;
    }

    private void initService() {
        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_MOVIE, (Serializable) movies);
        outState.putSerializable(BUNDLE_SEARCH_RESULT_LIST, (Serializable) searchListMovies);
        outState.putInt(BUNDLE_CURRENT_PAGE, currentPage);
        outState.putInt(BUNDLE_TOTAL_PAGE, totalPage);
        outState.putBoolean(BUNDLE_SEARCH_MODE, searchMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movies = (List<Movie>) savedInstanceState.getSerializable(BUNDLE_MOVIE);
        searchListMovies = (List<Movie>) savedInstanceState.getSerializable(BUNDLE_SEARCH_RESULT_LIST);
        currentPage = savedInstanceState.getInt(BUNDLE_CURRENT_PAGE, 2);
        totalPage = savedInstanceState.getInt(BUNDLE_TOTAL_PAGE, 3);
        searchMode = savedInstanceState.getBoolean(BUNDLE_SEARCH_MODE);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void initData() {
        Call<MoviesResponse> firstCall = apiService.getTopRatedMovies(API_KEY);
        firstCall.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    movies = response.body().getResults();
                    currentPage = response.body().getPage();
                    totalPage = response.body().getTotalPages();
                    initRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initData();
                            }
                        });
                snackbar.show();
                Log.e(TAG, "initData" + t.toString());
            }
        });
    }

    private void loadNextPage() {
        if (!searchMode) {
            if (currentPage <= totalPage) {
                Call<MoviesResponse> nextPageCall = apiService.getTopRatedMoviesNextPage(API_KEY, ++currentPage);
                nextPageCall.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                        if (response.isSuccessful()) {
                            movies.addAll(response.body().getResults());
                            moviesAdapter.notifyDataSetChanged();
                            totalPage = response.body().getTotalPages();
                            currentPage = response.body().getPage();
                        }
                    }

                    @Override
                    public void onFailure(Call<MoviesResponse> call, Throwable t) {
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main), "I Can Not Load the Next Page", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Try Again", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadNextPage();
                                    }
                                });
                        snackbar.show();
                        Log.e(TAG, "loadNextPage" + t.toString());
                    }
                });
            }
        }
    }

    private void initClick() {
        click = new MyClick() {
            @Override
            public void onClick(Movie movie) {
                Intent intent = new Intent(context, FullViewActivity.class);
                intent.putExtra(EXTRA_MOVIE, movie);
                startActivity(intent);
            }
        };
    }

    private void initRecyclerView() {

        ArrayList<Movie> result;
        if (searchListMovies != null) {
            result = (ArrayList<Movie>) searchListMovies;
        } else {
            result = (ArrayList<Movie>) movies;
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        moviesAdapter = new MoviesAdapter(result, click, context);
        recyclerView.setAdapter(moviesAdapter);
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItems = gridLayoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + firstVisibleItems) + 20 >= totalItemCount) {
                    loadNextPage();
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        moviesAdapter.notifyDataSetChanged();
    }
}