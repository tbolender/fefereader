package de.timbolender.fefereader.network;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetched current front page of Fefe's blog.
 */
public class Fetcher {
    private static final String URL = "https://blog.fefe.de/";

    private final OkHttpClient client;
    private final Parser parser;

    public Fetcher(OkHttpClient client, Parser parser) {
        checkNotNull(client);
        checkNotNull(parser);

        this.client = client;
        this.parser = parser;
    }

    /**
     * Retrieve blog posts in current thread.
     * @return All parsed blog posts.
     * @throws IOException Error during retrieval.
     * @throws ParseException Error when parsing the posts.
     */
    public List<RawPost> fetch() throws IOException, ParseException{
        Request request = new Request.Builder()
            .url(URL)
            .build();

        Response response = client.newCall(request).execute();
        return parser.parse(response.body().string());
    }
}
