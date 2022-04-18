package com.example.explorejournal;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Attempt {
    private final String note;
    private final Date date;
    private final double rating;

    public Attempt(String note, Date date, double rating) {
        this.note = note;
        this.date = date;
        this.rating = rating;
    }

    public String getNote() {
        return note;
    }

    public Date getDate() {
        return date;
    }

    public double getRating() {
        return rating;
    }

    @NonNull
    @Override
    public String toString() {
        return "Attempt{" +
                "date=" + date +
                ", rating=" + rating +
                ", note='" + note + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attempt)) return false;
        Attempt attempt = (Attempt) o;
        return Double.compare(attempt.getRating(), getRating()) == 0 && getNote().equals(attempt.getNote()) && getDate().equals(attempt.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNote(), getDate(), getRating());
    }
}
