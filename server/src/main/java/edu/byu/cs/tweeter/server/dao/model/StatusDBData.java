package edu.byu.cs.tweeter.server.dao.model;

import edu.byu.cs.tweeter.model.domain.Status;

public class StatusDBData implements Comparable<StatusDBData> {
    private final Status status;
    private final String posterAlias;

    public StatusDBData(Status status, String posterAlias) {
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
        StatusDBData data = (StatusDBData) o;
        return this.status.equals(data.getStatus());
    }

    @Override
    public int compareTo(StatusDBData statusDBData) {
        return this.status.compareTo(statusDBData.getStatus());
    }

    @Override
    public int hashCode() {
        return this.status.hashCode();
    }
}
