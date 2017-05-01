package com.iamchill.chill;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Qbold on 09.01.2016.
 */
public class FriendsResult {
    @SerializedName("ids")
    public final long[] ids;

    @SerializedName("previous_cursor")
    public final long previousCursor;

    @SerializedName("previous_cursor_str")
    public final String previousCursorStr;

    @SerializedName("next_cursor")
    public final long nextCursor;

    @SerializedName("next_cursor_str")
    public final String nextCursorStr;

    public FriendsResult(long[] ids, long previousCursor, String previousCursorStr,
                         long nextCursor, String nextCursorStr) {
//        System.out.println("SAWWQ");
        this.ids = ids;
        this.previousCursor = previousCursor;
        this.previousCursorStr = previousCursorStr;
        this.nextCursor = nextCursor;
        this.nextCursorStr = nextCursorStr;
    }
}