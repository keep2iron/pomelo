package io.github.keep2iron.app;

import java.util.Objects;

/**
 * Created by keep2iron on ${Date}.
 * write the powerful code ！
 * website : keep2iron.github.io
 */
public class Movie {

    private int id;
    private String movieName;
    private String movieImage;
    private String description;
    private int year;
    private String local;

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.id &&
                year == movie.year &&
                Objects.equals(movieName, movie.movieName) &&
                Objects.equals(movieImage, movie.movieImage) &&
                Objects.equals(description, movie.description) &&
                Objects.equals(local, movie.local);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, movieName, movieImage, description, year, local);
    }
}