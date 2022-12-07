package edu.byu.cs.tweeter.server.dao.model;

import edu.byu.cs.tweeter.model.domain.Status;

/**
 * Represents a status in the database.
 */
public class DBStatus implements Comparable<DBStatus> {

    /**
     * The status.
     */
    private final Status status;

    /**
     * The alias of the user who posted the status.
     */
    private final String posterAlias;

    public DBStatus(Status status, String posterAlias) {
        this.status = status;
        this.posterAlias = posterAlias;
    }

    public Status getStatus() {
        return status;
    }

    public String getPosterAlias() {
        return posterAlias;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBStatus data = (DBStatus) o;
        return this.status.equals(data.getStatus());
    }

    @Override
    public int compareTo(DBStatus DBStatus) {
        return this.status.compareTo(DBStatus.getStatus());
    }

    @Override
    public int hashCode() {
        return this.status.hashCode();
    }
}
