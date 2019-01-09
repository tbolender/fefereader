package de.timbolender.fefereader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.db.DatabaseException;
import de.timbolender.fefereader.db.DatabaseWrapper;
import de.timbolender.fefereader.db.SQLiteOpenHelper;
import de.timbolender.fefereader.db.SQLiteWrapper;
import de.timbolender.fefereader.service.UpdateService;
import de.timbolender.fefereader.ui.view.PostView;
import de.timbolender.fefereader.util.PreferenceHelper;

public class DetailsActivity extends AppCompatActivity implements PostView.OnLinkClickedListener {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
    private static final String FEFE_BASE_URL = "https://blog.fefe.de/?ts=";
    private static final String SHARE_URL = FEFE_BASE_URL + "%s";

    public static final String INTENT_EXTRA_POST = "post";

    SQLiteOpenHelper databaseHelper;
    DatabaseWrapper databaseWrapper;
    BroadcastReceiver updateReceiver;
    PreferenceHelper preferenceHelper;
    Post post;
    @BindView(R.id.post_view)
    PostView postView;

    /**
     * Creates an intent to show this activity with given post.
     * @param context Context to use.
     * @param post Post to display.
     * @return Intent showing the post.
     */
    public static Intent createShowPostIntent(Context context, Post post) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(DetailsActivity.INTENT_EXTRA_POST, post);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        preferenceHelper = new PreferenceHelper(this);

        // Set up database
        databaseHelper = new SQLiteOpenHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        databaseWrapper = new SQLiteWrapper(database);

        // Extract post
        final Intent intent = getIntent();
        post = intent.getParcelableExtra(INTENT_EXTRA_POST);

        // Set title
        Date data = new Date(post.getDate());
        setTitle(DATE_FORMAT.format(data));

        // Fill view
        String customStyle = preferenceHelper.getPostStyle();
        postView.fill(post, customStyle);
        postView.setOnLinkClickedListener(this);

        // Create receiver for updates
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received content update notification");
                abortBroadcast();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);

        MenuItem item = menu.findItem(R.id.menu_bookmarked);
        item.setTitle(post.isBookmarked() ? R.string.menu_bookmark_delete : R.string.menu_bookmark_create);
        item.setIcon(post.isBookmarked() ? R.drawable.ic_bookmark_border : R.drawable.ic_bookmark);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_bookmarked) {
            databaseWrapper.setBookmarked(post.getId(), !post.isBookmarked());
            post = databaseWrapper.getPost(post.getId());
            invalidateOptionsMenu();
            return true;
        }
        else if(item.getItemId() == R.id.menu_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(SHARE_URL, post.getId()));
            shareIntent.setType("text/plain");

            startActivity(Intent.createChooser(shareIntent, getString(R.string.intent_share_title)));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register broadcast receiver for notifications
        IntentFilter intentFilter = new IntentFilter(UpdateService.BROADCAST_UPDATE_FINISHED);
        intentFilter.setPriority(UpdateService.BROADCAST_PRIORITY_UI);
        registerReceiver(updateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(updateReceiver);

        super.onPause();
    }

    @Override
    public void onLinkClicked(String url) {
        // Handle links to post internally
        if(url.startsWith(FEFE_BASE_URL)) {
            try {
                Log.d(TAG, "Clicked on link to post, try to handle it internally");
                String postId = url.substring(FEFE_BASE_URL.length());
                Post post = databaseWrapper.getPost(postId);

                Intent intent = createShowPostIntent(this, post);
                startActivity(intent);
                return;
            }
            catch(DatabaseException e) {
                Log.d(TAG, "Post is not stored, following normal procedure");
            }
        }

        // Start intent with optional preview
        final Intent urlIntent = new Intent(Intent.ACTION_VIEW);
        urlIntent.setData(Uri.parse(url));
        if(preferenceHelper.isUrlInspectionEnabled()) {
            Snackbar snackbar = Snackbar.make(postView, url, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.button_open_link, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(urlIntent);
                }
            });
            snackbar.show();
        }
        else {
            startActivity(urlIntent);
        }
    }

    @Override
    protected void onDestroy() {
        databaseWrapper.cleanUp();
        databaseHelper.close();
        super.onDestroy();
    }
}
