package com.kiriloff.max.loyaltyplant.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiriloff.max.loyaltyplant.Interfaces.MyClick;
import com.kiriloff.max.loyaltyplant.R;
import com.kiriloff.max.loyaltyplant.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private List<Movie> movies;
    private MyClick click;
    private Context context;

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public MoviesAdapter(List<Movie> movies, MyClick click, Context context) {
        this.movies = movies;
        this.click = click;
        this.context = context;
    }

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView backdrop_path;
        TextView title;
        TextView tagline;
        CardView cardView;
        TextView tv_cv_rating;
        TextView year;

        private final static String IMAGE_KEY = "https://image.tmdb.org/t/p/w500";

        public ViewHolder(View itemView) {
            super(itemView);
            backdrop_path = (ImageView) itemView.findViewById(R.id.iv_cv_backdrop_path);
            title = (TextView) itemView.findViewById(R.id.tv_cv_title);
            tagline = (TextView) itemView.findViewById(R.id.tv_cv_tagline);
            tv_cv_rating = (TextView) itemView.findViewById(R.id.tv_cv_rating);
            year = (TextView) itemView.findViewById(R.id.tv_cv_data);
            cardView = (CardView) itemView.findViewById(R.id.card_view_post);

            cardView.setOnClickListener(this);
        }

        public void bind(Movie movie) {
            title.setText(movie.getTitle());
            tagline.setText(movie.getOverview());
            tv_cv_rating.setText(movie.getVoteAverage().toString());
            year.setText(movie.getReleaseDate());

            Picasso.with(context)
                    .load(IMAGE_KEY + movie.getBackdropPath())
                    .resizeDimen(R.dimen.cv_width, R.dimen.cv_iv_height)
                    .centerCrop()
                    .into(backdrop_path);
        }

        @Override
        public void onClick(View v) {
            click.onClick(movies.get(getAdapterPosition()));
        }
    }
}
