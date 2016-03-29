package com.codepath.apps.restclienttemplate.activities;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.ComposeFragment;
import com.codepath.apps.restclienttemplate.utils.DividerItemDecoration;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.TweetUtil;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.ComposeDialogListener {

    private TwitterClient client;
    private ArrayList<Tweet> mTweets = new ArrayList<>();
    private TweetAdapter adapter = new TweetAdapter(mTweets);
    @Bind(R.id.store_house_ptr_frame)
    PtrClassicFrameLayout mPtrFrame;
    @Bind(R.id.recycler_timeline)
    RecyclerView mRecycler;
    @Bind(R.id.btn_fab)
    FloatingActionButton mBtnFAB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        client = TwitterApplication.getRestClient();

        init();

        populateTimeline(0);

    }


    private void init() {
        mRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.addItemDecoration(new DividerItemDecoration(this));


        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                populateTimeline(0);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        mRecycler.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(page + 1);
            }
        });
        mRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mBtnFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCompose();
            }
        });

    }

    private void showCompose() {
        FragmentManager manager = getSupportFragmentManager();
        ComposeFragment dialog = ComposeFragment.newInstance();
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        dialog.show(manager, "fragment_compose");
    }

    private void populateTimeline(int page) {
        if (TweetUtil.isConnected()) {
            if (page == 1) {
                mTweets.clear();
                adapter.notifyDataSetChanged();
            }
            client.getHomeTimeline(page, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    mTweets.addAll(Tweet.fromJsonArray(response));
                    adapter.notifyItemRangeInserted(adapter.getItemCount(), mTweets.size() - 1);
                    mPtrFrame.refreshComplete();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getApplicationContext(), "Some thing error", Toast.LENGTH_LONG).show();
                    mPtrFrame.refreshComplete();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
            mPtrFrame.refreshComplete();
        }
    }

    @Override
    public void onFinishEditDialog(String inputText, long replyId) {
        if (TweetUtil.isConnected()) {
            client.postTweet(inputText, replyId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mTweets.add(0, Tweet.findOrCreateFromJson(response));
                    adapter.notifyItemInserted(0);
                    mRecycler.scrollToPosition(0);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getBaseContext(), "Something error", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Connection problem", Toast.LENGTH_LONG).show();
        }
    }
}
