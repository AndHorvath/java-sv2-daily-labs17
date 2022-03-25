package day04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie(1L, "Title", LocalDate.of(2010, 10, 20));
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
    void testToStringTest() {
        assertEquals(
            "Movie (id: 1) { title: Title, release date: 2010-10-20 }", movie.toString()
        );
    }
}