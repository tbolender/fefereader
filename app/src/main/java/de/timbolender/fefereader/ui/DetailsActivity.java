package de.timbolender.fefereader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.db.DatabaseWrapper;
import de.timbolender.fefereader.db.SQLiteOpenHelper;
import de.timbolender.fefereader.db.SQLiteWrapper;
import de.timbolender.fefereader.service.UpdateService;
import de.timbolender.fefereader.ui.view.PostView;
import de.timbolender.fefereader.util.PreferenceHelper;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());

    public static final String INTENT_EXTRA_POST = "post";

    SQLiteOpenHelper databaseHelper;
    DatabaseWrapper databaseWrapper;
    BroadcastReceiver updateReceiver;
    PreferenceHelper preferenceHelper;
    Post post;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        preferenceHelper = new PreferenceHelper(this);

        // Set up database
        databaseHelper = new SQLiteOpenHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        databaseWrapper = new SQLiteWrapper(database);

        final Intent intent = getIntent();
        post = intent.getParcelableExtra(INTENT_EXTRA_POST);

        Date data = new Date(post.getDate());
        setTitle(DATE_FORMAT.format(data));
        final PostView view = ButterKnife.findById(this, R.id.post_view);
        String customStyle = preferenceHelper.getPostStyle();
        view.fill(post, customStyle);
        view.setOnLinkClickedListener(new PostView.OnLinkClickedListener() {
            @Override
            public void onLinkClicked(String url) {
                final Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(url));
                if(preferenceHelper.isUrlInspectionEnabled()) {
                    Snackbar snackbar = Snackbar.make(view, url, Snackbar.LENGTH_INDEFINITE);
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
        });

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
    protected void onDestroy() {
        databaseWrapper.cleanUp();
        databaseHelper.close();
        super.onDestroy();
    }
}
