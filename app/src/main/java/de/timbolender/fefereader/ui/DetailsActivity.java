package de.timbolender.fefereader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.data.Post;
import de.timbolender.fefereader.service.UpdateService;
import de.timbolender.fefereader.util.Html;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());

    public static final String INTENT_EXTRA_POST = "post";

    BroadcastReceiver updateReceiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        Post post = intent.getParcelableExtra(INTENT_EXTRA_POST);
        TextView view = ButterKnife.findById(this, R.id.contents);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(Html.fromHtml(post.getContents()));

        Date data = new Date(post.getDate());
        setTitle(DATE_FORMAT.format(data));

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
}
