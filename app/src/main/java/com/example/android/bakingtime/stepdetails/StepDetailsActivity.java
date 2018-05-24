package com.example.android.bakingtime.stepdetails;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.ActivityStepDetailsBinding;
import com.example.android.networkmodule.model.Step;
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

public class StepDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_STEP_NUMBER = "StepDetailsActivity.EXTRA_STEP_NUMBER";
    public static final String EXTRA_STEPS = "StepDetailsActivity.EXTRA_STEPS";
    private ActivityStepDetailsBinding binding;
    private ArrayList<Step> steps;
    private int stepNumber;
    private SimpleExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_step_details);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(EXTRA_STEPS)) {
            showToast();
            finish();
            return;
        }

        steps = intent.getParcelableArrayListExtra(EXTRA_STEPS);
        stepNumber = intent.getIntExtra(EXTRA_STEP_NUMBER, 0);

        handleIntentData();
        addListeners();
    }

    private void handleIntentData() {

        handleButtons();

        setDescription();

        String videoUrl = steps.get(stepNumber).getVideoURL();
        String thumbnailUrl = steps.get(stepNumber).getThumbnailURL();
        if (!TextUtils.isEmpty(videoUrl)) {
            initializePlayer(Uri.parse(videoUrl));
        } else if (!TextUtils.isEmpty(thumbnailUrl)) {
            setPlayerThumbnail(Uri.parse(thumbnailUrl));
        } else {
            if (binding.stepDetails != null) {
                binding.stepDetails.stepVideo.setVisibility(View.GONE);
            }
        }
    }

    private void addListeners() {
        if (binding.stepDetails != null) {
            binding.stepDetails.stepPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(stepNumber - 1);
                }
            });

            binding.stepDetails.stepNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(stepNumber + 1);
                }
            });
        }
    }

    private void startActivity(int stepNumber) {
        Intent intent = new Intent(this, StepDetailsActivity.class);
        intent.putExtra(EXTRA_STEP_NUMBER, stepNumber);
        intent.putExtra(EXTRA_STEPS, steps);
        startActivity(intent);
        finish();
    }

    private void initializePlayer(Uri mediaUri) {
        if (binding.stepDetails != null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.stepDetails.stepVideo.setLayoutParams(new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                ));
                ActionBar supportActionBar = getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.hide();
                }
                binding.stepDetails.stepDescription.setVisibility(View.GONE);
                binding.stepDetails.stepPrevious.setVisibility(View.GONE);
                binding.stepDetails.stepNext.setVisibility(View.GONE);
            }

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            binding.stepDetails.stepVideo.setPlayer(exoPlayer);


            String userAgent = Util.getUserAgent(this, "BakingTime");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void setPlayerThumbnail(Uri mediaUri) {
        Picasso.with(this).load(mediaUri).into(target);
    }

    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (binding.stepDetails != null) {
                binding.stepDetails.stepVideo.setDefaultArtwork(bitmap);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Toast.makeText(StepDetailsActivity.this, getString(R.string.thumbnail_error),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onStop() {
        Picasso.with(this).cancelRequest(target);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void setDescription() {
        if (binding.stepDetails != null) {
            binding.stepDetails.stepDescription.setText(steps.get(stepNumber).getDescription());
        } else {
            showToast();
        }
    }

    private void handleButtons() {
        if (binding.stepDetails != null) {
            if (stepNumber == 0) {
                binding.stepDetails.stepPrevious.setVisibility(View.GONE);
            } else if (stepNumber == steps.size() - 1) {
                binding.stepDetails.stepNext.setVisibility(View.GONE);
            }
        } else {
            showToast();
        }
    }

    private void showToast() {
        Toast.makeText(this, getString(R.string.step_error), Toast.LENGTH_SHORT).show();
    }
}
