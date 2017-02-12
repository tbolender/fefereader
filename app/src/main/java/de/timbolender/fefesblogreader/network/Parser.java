package de.timbolender.fefesblogreader.network;

import android.util.Log;

import com.google.common.collect.ImmutableList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.timbolender.fefesblogreader.data.RawPost;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Parses a blog page for posts.
 */
public class Parser {
    private static final String TAG = Parser.class.getSimpleName();

    private static final Pattern SEARCH_PATTERN = Pattern.compile("<h3>(.+)<\\/h3>|<li><a href=\"\\?ts=([\\d\\w]+)\">\\[l\\]<\\/a> ?(.+?)\\n");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM d yyyy", Locale.US);

    /**
     * Parse blog posts from given text.
     * @param content Text to parse.
     * @return All parsed blog posts.
     * @throws ParseException Error when parsing.
     */
    public List<RawPost> parse(String content) throws ParseException {
        checkNotNull(content);

        Matcher matcher = SEARCH_PATTERN.matcher(content);

        Date currentDate = null;
        ImmutableList.Builder<RawPost> postBuilder = new ImmutableList.Builder<>();

        long baseTime = System.currentTimeMillis() << 8;
        long indexCounter = 255;
        while(matcher.find()) {
            if(matcher.group(2) == null) {
                currentDate = DATE_FORMAT.parse(matcher.group(1));
            }
            else {
                if(currentDate == null) {
                    throw new ParseException("No date available for post!", matcher.start());
                }

                String id = matcher.group(2);
                String postContent = matcher.group(3);
                RawPost post = new RawPost(id, baseTime + indexCounter, postContent, currentDate.toString());
                Log.v(TAG, "Parsed " + post.toString());
                postBuilder.add(post);

                // Decrement index counter
                if(indexCounter == 0) {
                    throw new IllegalArgumentException("Parser supports only 255 posts per parsing");
                }

                indexCounter--;
            }
        }

        return postBuilder.build();
    }
}
