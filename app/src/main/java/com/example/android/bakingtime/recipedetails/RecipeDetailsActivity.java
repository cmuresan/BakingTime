package com.example.android.bakingtime.recipedetails;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.ActivityRecipeDetailsBinding;
import com.example.android.bakingtime.stepdetails.StepDetailsActivity;
import com.example.android.networkmodule.model.Ingredient;
import com.example.android.networkmodule.model.Recipe;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity implements OnStepItemClickListener {

    public static final String EXTRA_RECIPE = "RecipeDetailsActivity.EXTRA_RECIPE";
    private static final int FIRST_STEP = 0;
    private static final String SELECTED_STEP = "RecipeDetailsActivity.SELECTED_STEP";
    private static final String VIDEO_CURRENT_POSITION = "RecipeDetailsActivity.VIDEO_CURRENT_POSITION";
    private static final String PLAYERS_STATE = "RecipeDetailsActivity.PLAYERS_STATE";
    private Recipe recipe;
    private ActivityRecipeDetailsBinding binding;
    private StepsAdapter stepsAdapter;
    private boolean isTwoPane;
    private SimpleExoPlayer exoPlayer;
    private int selectedStep;
    private long videoSavedPosition = 0;
    private boolean playersState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        binding = DataBindingUtil.setContentView(this,
                R.layout.activity_recipe_details);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_RECIPE)) {
            recipe = intent.getParcelableExtra(EXTRA_RECIPE);
        }

        if (recipe != null) {
            setTitle(recipe.getName());
            initRecipeStepsRecyclerView();
            bindRecipeData();
        } else {
            Toast.makeText(this, getString(R.string.recipe_error), Toast.LENGTH_SHORT).show();
        }

        if (binding.stepDetails != null) {
            isTwoPane = true;
            if (savedInstanceState != null) {
                videoSavedPosition = savedInstanceState.getLong(VIDEO_CURRENT_POSITION);
                playersState = savedInstanceState.getBoolean(PLAYERS_STATE);
                selectedStep = savedInstanceState.getInt(SELECTED_STEP);
                handleDetailsFragmentData(selectedStep);
            } else {
                handleDetailsFragmentData(FIRST_STEP);
            }
        } else {
            isTwoPane = false;
        }
    }

    private void handleDetailsFragmentData(int position) {
        selectedStep = position;
        binding.stepDescription.setText(recipe.getSteps().get(position).getDescription());

        binding.stepVideo.setVisibility(View.VISIBLE);

        String videoUrl = recipe.getSteps().get(position).getVideoURL();
        String thumbnailUrl = recipe.getSteps().get(position).getThumbnailURL();
        if (!TextUtils.isEmpty(videoUrl)) {
            initializePlayer(Uri.parse(videoUrl));
        } else if (!TextUtils.isEmpty(thumbnailUrl)) {
            setPlayerThumbnail(Uri.parse(thumbnailUrl));
        } else {
            binding.stepVideo.setVisibility(View.GONE);
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            binding.stepVideo.setPlayer(exoPlayer);
        }


        String userAgent = Util.getUserAgent(this, "BakingTime");
        MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                this, userAgent), new DefaultExtractorsFactory(), null, null);
        if (videoSavedPosition > 0) {
            exoPlayer.seekTo(videoSavedPosition);
        }
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(playersState);
    }

    private void setPlayerThumbnail(Uri mediaUri) {
        Picasso.with(this).load(mediaUri).into(target);
    }

    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            binding.stepVideo.setDefaultArtwork(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Toast.makeText(RecipeDetailsActivity.this, getString(R.string.thumbnail_error),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onStop() {
        Picasso.with(this).cancelRequest(target);
        stepsAdapter.removeClickListener();
        releasePlayer();
        super.onStop();
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void setTitle(String name) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(name);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindRecipeData() {
        setIngredients();

        stepsAdapter.setSteps(recipe.getSteps());
    }

    private void setIngredients() {
        List<String> ingredients = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredients.add(ingredient.toString());
        }
        String finalString = TextUtils.join("\n", ingredients);
        binding.recipeDetails.ingredientsContent.setText(finalString);
    }

    private void initRecipeStepsRecyclerView() {
        stepsAdapter = new StepsAdapter(this);
        stepsAdapter.setClickListener(this);

        binding.recipeDetails.stepsRecyclerView.setHasFixedSize(true);
        binding.recipeDetails.stepsRecyclerView.setAdapter(stepsAdapter);
    }

    @Override
    public void onStepItemClick(int position) {
        if (isTwoPane) {
            handleDetailsFragmentData(position);
        } else {
            Intent stepIntent = new Intent(this, StepDetailsActivity.class);
            stepIntent.putExtra(StepDetailsActivity.EXTRA_STEP_NUMBER, position);
            stepIntent.putParcelableArrayListExtra(StepDetailsActivity.EXTRA_STEPS, new ArrayList<>(recipe.getSteps()));
            startActivity(stepIntent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_STEP, selectedStep);
        if (exoPlayer != null) {
            long currentPosition = exoPlayer.getCurrentPosition();
            outState.putLong(VIDEO_CURRENT_POSITION, currentPosition);
            outState.putBoolean(PLAYERS_STATE, exoPlayer.getPlayWhenReady());
        }
    }
}
