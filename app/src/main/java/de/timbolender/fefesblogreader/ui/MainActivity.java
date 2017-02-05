package de.timbolender.fefesblogreader.ui;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.timbolender.fefesblogreader.R;
import de.timbolender.fefesblogreader.data.Post;
import de.timbolender.fefesblogreader.data.RawPost;
import de.timbolender.fefesblogreader.network.Fetcher;
import de.timbolender.fefesblogreader.network.Parser;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity{
    @BindView(R.id.post_list) RecyclerView postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        postList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        postList.addItemDecoration(dividerItemDecoration);

        // Just for testing, allow retrieval in main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @OnClick(R.id.load_button)
    public void onClick(View view) {
        try {
            OkHttpClient client = new OkHttpClient();
            Parser parser = new Parser();
            Fetcher fetcher = new Fetcher(client, parser);
            List<RawPost> posts = fetcher.fetch();

            List<Post> viewablePosts = new ArrayList<>();
            for(RawPost post : posts) {
                viewablePosts.add(new Post(post));
            }

            PostAdapter postAdapter = new PostAdapter(viewablePosts, null);
            postList.setAdapter(postAdapter);
        }
        catch(ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
