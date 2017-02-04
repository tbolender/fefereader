package de.timbolender.fefesblogreader.network;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.timbolender.fefesblogreader.data.RawPost;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Fetched current front page of Fefe's blog.
 */
public class Fetcher {
    private static final String URL = "https://blog.fefe.de/";

    private final OkHttpClient client;
    private final Parser parser;

    public Fetcher(OkHttpClient client, Parser parser) {
        this.client = client;
        this.parser = parser;
    }

    public List<RawPost> fetch() throws IOException, ParseException{
        Request request = new Request.Builder()
            .url(URL)
            .build();

        Response response = client.newCall(request).execute();
        return parser.parse(response.body().string());
    }
}
