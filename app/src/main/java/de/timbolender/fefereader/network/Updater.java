package de.timbolender.fefereader.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.timbolender.fefereader.data.RawPost;
import de.timbolender.fefereader.db.DatabaseWrapper;
import okhttp3.OkHttpClient;

public class Updater {
    private final DatabaseWrapper databaseWrapper;

    public Updater(DatabaseWrapper databaseWrapper) {
        this.databaseWrapper = databaseWrapper;
    }

    public void update() throws ParseException, IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();
        Parser parser = new Parser();
        Fetcher fetcher = new Fetcher(client, parser);
        List<RawPost> posts = fetcher.fetch();

        for(RawPost post : posts) {
            databaseWrapper.addOrUpdatePost(post);
        }
    }
}
