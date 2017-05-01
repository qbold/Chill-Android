package com.iamchill.chill;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Qbold on 06.01.2016.
 */
public class TwitterFollowers extends TwitterApiClient {

    public TwitterFollowers(TwitterSession session) {
        super(session);
    }

    /**
     * Provide CustomService with defined endpoints
     */
    public CustomService getCustomService() {
        return getService(CustomService.class);
    }
}

// example users/show service endpoint
interface CustomService {
    @GET("/1.1/friends/ids.json")
    void show(@Query("user_id") long id, Callback<FriendsResult> cb);
}