package de.timbolender.fefesblogreader.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.timbolender.fefesblogreader.R;
import de.timbolender.fefesblogreader.data.Post;
import de.timbolender.fefesblogreader.data.RawPost;
import de.timbolender.fefesblogreader.db.DatabaseWrapper;
import de.timbolender.fefesblogreader.db.PostReader;
import de.timbolender.fefesblogreader.db.SQLiteOpenHelper;
import de.timbolender.fefesblogreader.db.SQLiteWrapper;
import de.timbolender.fefesblogreader.network.Fetcher;
import de.timbolender.fefesblogreader.network.Parser;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements PostAdapter.OnPostSelectedListener {
    SQLiteOpenHelper databaseHelper;
    PostAdapter postAdapter;
    DatabaseWrapper databaseWrapper;

    @BindView(R.id.post_list) RecyclerView postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        databaseHelper = new SQLiteOpenHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        databaseWrapper = new SQLiteWrapper(database);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        postList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        postList.addItemDecoration(dividerItemDecoration);

        // Fill content
        PostReader reader = databaseWrapper.getPostsReader();
        postAdapter = new PostAdapter(reader, this);
        postList.setAdapter(postAdapter);

        // Just for testing, allow retrieval in main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_refresh) {
            onRefreshClick();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        databaseWrapper.cleanUp();
        databaseHelper.close();
        super.onDestroy();
    }

    public void onRefreshClick() {
        try {
            OkHttpClient client = new OkHttpClient();
            Parser parser = new Parser();
            Fetcher fetcher = new Fetcher(client, parser);
            List<RawPost> posts = fetcher.fetch();

            for(RawPost post : posts) {
                databaseWrapper.addOrUpdatePost(post);
            }

            updateAdapter();
            Toast.makeText(this, "Feed aktualisiert", Toast.LENGTH_SHORT).show();
        }
        catch(ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAdapter() {
        PostReader reader = databaseWrapper.getPostsReader();
        postAdapter.setReader(reader);
    }

    @Override
    public void OnPostSelected(Post post) {
        databaseWrapper.markRead(post);
        updateAdapter();

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.INTENT_EXTRA_POST_CONTENT, post.getContents());
        startActivity(intent);
    }
}
