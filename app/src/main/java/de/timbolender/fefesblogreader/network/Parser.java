package de.timbolender.fefesblogreader.network;

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
                RawPost post = new RawPost(id, System.currentTimeMillis(), postContent, currentDate.toString());
                postBuilder.add(post);
            }
        }

        return postBuilder.build();
    }
}
