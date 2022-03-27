package day05;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieTest {

    Movie movie;
    double epsilon;

    @BeforeEach
    void setUp() {
        movie = new Movie(1L, "Title", LocalDate.of(2010, 10, 20));
        epsilon = Math.pow(10, -5);
    }

    @Test
    void getIdTest() {
        assertEquals(1L, movie.getId());
    }

    @Test
    void getTitleTest() {
        assertEquals("Title", movie.getTitle());
    }

    @Test
    void getLocalDateTest() {
        assertEquals(LocalDate.of(2010, 10, 20), movie.getLocalDate());
    }

    @Test
    void getAverageRatingTest() {
        assertEquals(0.0, movie.getAverageRating(), epsilon);
    }

    @Test
    void setAverageRatingTest() {
        movie.setAverageRating(4.8);
        assertEquals(4.8, movie.getAverageRating(), epsilon);
    }

    @Test
    void testToStringTest() {
        assertEquals(
            "Movie (id: 1) { title: Title, release date: 2010-10-20, average rating: 0.0 }", movie.toString()
        );
    }
}