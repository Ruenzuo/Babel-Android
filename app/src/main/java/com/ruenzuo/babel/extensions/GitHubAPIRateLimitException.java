package com.ruenzuo.babel.extensions;

/**
 * Created by ruenzuo on 24/08/14.
 */
public class GitHubAPIRateLimitException extends Exception {

    public GitHubAPIRateLimitException(String message) {
        super(message);
    }

}
