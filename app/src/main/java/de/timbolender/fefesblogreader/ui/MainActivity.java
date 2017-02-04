package de.timbolender.fefesblogreader.ui;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.timbolender.fefesblogreader.R;
import de.timbolender.fefesblogreader.data.RawPost;
import de.timbolender.fefesblogreader.network.Fetcher;
import de.timbolender.fefesblogreader.network.Parser;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity{
    @BindView(R.id.text_main) TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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

            for(RawPost post : posts) {
                mainText.append(post.getId() + "\n");
                mainText.append(Long.toString(post.getFetchedTimestamp()) + "\n");
                mainText.append(post.getDate() + "\n");
                mainText.append(Html.fromHtml(post.getContents()) + "\n");
                mainText.append("\n");
            }
        }
        catch(ParseException e) {
            e.printStackTrace();
            mainText.setText(e.toString());
        }
        catch(IOException e) {
            e.printStackTrace();
            mainText.setText(e.toString());
        }
    }
}
