package de.timbolender.fefereader.ui;

import android.content.Context;
import android.content.Intent;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.db.Post;
import de.timbolender.fefereader.ui.helper.NotificationSilencer;
import de.timbolender.fefereader.ui.view.PostView;
import de.timbolender.fefereader.util.PreferenceHelper;
import de.timbolender.fefereader.viewmodel.DetailsViewModel;
import de.timbolender.fefereader.viewmodel.PostViewModel;

import static de.timbolender.fefereader.viewmodel.PostViewModelKt.toPostViewModel;

public class DetailsActivity extends AppCompatActivity implements PostView.OnLinkClickedListener {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
    private static final String FEFE_BASE_URL = "https://blog.fefe.de/?ts=";
    private static final String SHARE_URL = FEFE_BASE_URL + "%s";

    public static final String INTENT_EXTRA_POST_ID = "post_id";

    DetailsViewModel vm;
    LiveData<Post> postVm;
    PostView postView;

    NotificationSilencer updateReceiver;
    PreferenceHelper preferenceHelper;

    /**
     * Creates an intent to show this activity with given post.
     * @param context Context to use.
     * @param postId Post id to display.
     * @return Intent showing the post.
     */
    public static Intent createShowPostIntent(Context context, String postId) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(DetailsActivity.INTENT_EXTRA_POST_ID, postId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        postView = findViewById(R.id.post_view);

        preferenceHelper = new PreferenceHelper(this);

        // Set up data binding
        final Intent intent = getIntent();
        String postId = intent.getStringExtra(INTENT_EXTRA_POST_ID);

        vm = new ViewModelProvider(this).get(DetailsViewModel.class);
        postVm = vm.getPost(postId);
        postVm.observe(this, dbPost -> {
            PostViewModel post = toPostViewModel(dbPost);

            // Set title
            Date date = post.getDate();
            setTitle(DATE_FORMAT.format(date));

            // Fill view
            String customStyle = preferenceHelper.getPostStyle();
            postView.fill(post, customStyle);
            postView.setOnLinkClickedListener(this);

            // Update menu icons
            invalidateOptionsMenu();
        });

        // Create receiver to consume update notifications
        updateReceiver = new NotificationSilencer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);

        Post post = postVm.getValue();
        if(post != null) {
            MenuItem item = menu.findItem(R.id.menu_bookmarked);
            item.setTitle(post.isBookmarked() ? R.string.menu_bookmark_delete : R.string.menu_bookmark_create);
            item.setIcon(post.isBookmarked() ? R.drawable.ic_bookmark_border : R.drawable.ic_bookmark);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Ensure post is present
        Post currentPost = postVm.getValue();
        if(currentPost == null)
            return super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.menu_bookmarked) {
            vm.toggleBookmark(currentPost.getId());
            return true;
        }
        else if(item.getItemId() == R.id.menu_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(SHARE_URL, currentPost.getId()));
            shareIntent.setType("text/plain");

            startActivity(Intent.createChooser(shareIntent, getString(R.string.intent_share_title)));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateReceiver.register(this);
    }

    @Override
    protected void onPause() {
        updateReceiver.unregister(this);

        super.onPause();
    }

    @Override
    public void onLinkClicked(String url) {
        // Handle links to post internally
        if(url.startsWith(FEFE_BASE_URL)) {
            Log.d(TAG, "Clicked on link to post, try to handle it internally");
            String postId = url.substring(FEFE_BASE_URL.length());

            Intent intent = createShowPostIntent(this, postId);
            startActivity(intent);
            return;
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
}
