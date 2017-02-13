package de.timbolender.fefereader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.ButterKnife;
import de.timbolender.fefereader.R;
import de.timbolender.fefereader.util.Html;

public class DetailsActivity extends AppCompatActivity {
    public static final String INTENT_EXTRA_POST_CONTENT = "post_content";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        TextView view = ButterKnife.findById(this, R.id.contents);
        view.setText(Html.fromHtml(intent.getStringExtra(INTENT_EXTRA_POST_CONTENT)));
    }
}
