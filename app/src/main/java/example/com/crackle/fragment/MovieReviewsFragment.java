package example.com.crackle.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.crackle.R;
import example.com.crackle.adapter.MovieReviewAdapter;
import example.com.crackle.listener.MovieApiClient;
import example.com.crackle.model.Movie;
import example.com.crackle.model.Review;
import example.com.crackle.model.ReviewResults;
import example.com.crackle.utils.MovieApiService;
import example.com.crackle.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static example.com.crackle.utils.Constants.API_KEY;
import static example.com.crackle.utils.Constants.LINEAR_LAYOUT;
import static example.com.crackle.utils.Constants.MOVIE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieReviewsFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private MovieApiClient client;
    private Call<ReviewResults> call;
    private List<Review> reviewList;


    public MovieReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        Utils.setupRecyclerView(getContext(), recyclerView, LINEAR_LAYOUT);

        reviewList = new ArrayList<>();
        final MovieReviewAdapter adapter = new MovieReviewAdapter(getContext(), reviewList);
        recyclerView.setAdapter(adapter);

        client = MovieApiService.getClient().create(MovieApiClient.class);
        call = client.getMovieReviews(((Movie)getArguments().getParcelable(MOVIE)).getMovieId(), API_KEY);
        call.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(Call<ReviewResults> call, Response<ReviewResults> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() == null || response.body().getReviewList() == null) {
                    return;
                }
                if (response.body().getReviewList().size() > 0) {
                    reviewList.addAll(response.body().getReviewList());
                    adapter.notifyDataSetChanged();
                    emptyTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ReviewResults> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error getting movie reviews", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public static Fragment newInstance(Movie movie) {
        MovieReviewsFragment fragment = new MovieReviewsFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }
}