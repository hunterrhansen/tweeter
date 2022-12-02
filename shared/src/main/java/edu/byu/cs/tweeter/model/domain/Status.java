package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Represents a status (or tweet) posted by a user.
 */
public class Status implements Comparable<Status>, Serializable {
    /**
     * Text for the status.
     */
    public String post;
    /**
     * User who sent the status.
     */
    public User user;
    /**
     * String representation of the date/time at which the status was sent.
     */
    public String datetime;
    /**
     * URLs contained in the post text.
     */
    public List<String> urls;
    /**
     * User mentions contained in the post text.
     */
    public List<String> mentions;
    /**
     * Status id for use in db
     */
    public String id;

    public Status() {
    }

    public Status(String post, User user, String datetime, List<String> urls, List<String> mentions) {
        this.post = post;
        this.user = user;
        this.datetime = datetime;
        this.urls = urls;
        this.mentions = mentions;
        this.id = null;
    }

    public Status(String post, User user, String datetime, List<String> urls, List<String> mentions, String id) {
        this.post = post;
        this.user = user;
        this.datetime = datetime;
        this.urls = urls;
        this.mentions = mentions;
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getPost() {
        return post;
    }

    public List<String> getUrls() {
        return urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return Objects.equals(post, status.post) &&
                Objects.equals(user, status.user) &&
                Objects.equals(datetime, status.datetime) &&
                Objects.equals(mentions, status.mentions) &&
                Objects.equals(urls, status.urls) &&
                Objects.equals(id, status.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, user, datetime, mentions, urls, id);
    }

    @Override
    public int compareTo(Status status) {
        return this.id.compareTo(status.getID());
    }

    @Override
    public String toString() {
        return "Status{" +
                "post='" + post + '\'' +
                ", user=" + user +
                ", datetime=" + datetime +
                ", mentions=" + mentions +
                ", urls=" + urls +
                '}';
    }

}
