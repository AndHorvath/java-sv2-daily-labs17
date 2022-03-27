package day05;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    Rating rating;

    @BeforeEach
    void setUp() {
        rating = new Rating(1L, 5);
    }

    @Test
    void getMovieTitleTest() {
        assertEquals(1L, rating.getMovieId());
    }

    @Test
    void getRatingValueTest() {
        assertEquals(5, rating.getRatingValue());
    }
}