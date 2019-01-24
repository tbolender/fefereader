package de.timbolender.fefereader.network;

import android.util.Log;

import com.google.common.collect.ImmutableList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.timbolender.fefereader.data.RawPost;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Parses a blog page for posts.
 */
public class Parser {
    private static final String TAG = Parser.class.getSimpleName();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM d yyyy", Locale.US);

    /**
     * Parse blog posts from given text.
     * @param content Text to parse.
     * @return All parsed blog posts.
     * @throws ParseException Error when parsing.
     */
    public List<RawPost> parse(String content) throws ParseException {
        checkNotNull(content);

        Document document = Jsoup.parse(content);

        Date currentDate = null;
        ImmutableList.Builder<RawPost> postBuilder = new ImmutableList.Builder<>();

        long baseTime = System.currentTimeMillis() * 1000;
        long indexCounter = 999;

        for(Element element : document.select("body > h3, body > ul")) {
            if(element.tagName().equals("h3")) {
                currentDate = DATE_FORMAT.parse(element.text());
            }
            else {
                if(currentDate == null) {
                    throw new ParseException("No date available for first post!", -1);
                }

                for(Element entry : element.children().select("li")) {
                    // Skip non-direct children
                    if(!entry.parent().equals(element))
                        continue;

                    Elements entryContent = entry.children();
                    Element idElement = entryContent.get(0);
                    String id = idElement.attr("href").replace("?ts=", "");
                    String postContent = entry.html().replace(idElement.outerHtml(), "");
                    RawPost post = new RawPost(id, baseTime + indexCounter, postContent, currentDate.getTime());
                    Log.v(TAG, "Parsed " + post.toString());
                    postBuilder.add(post);

                    // Decrement index counter
                    if(indexCounter == 0) {
                        throw new IllegalArgumentException("Parser supports only 255 posts per parsing");
                    }

                    indexCounter--;
                }
            }
        }

        return postBuilder.build();
    }
}
