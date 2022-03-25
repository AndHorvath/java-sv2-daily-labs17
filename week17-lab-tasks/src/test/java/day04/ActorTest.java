package day04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActorTest {

    Actor actor;

    @BeforeEach
    void setUp() {
        actor = new Actor(1L, "Actor");
    }

    @Test
    void getIdTest() {
        assertEquals(1L, actor.getId());
    }

    @Test
    void getNameTest() {
        assertEquals("Actor", actor.getName());
    }

    @Test
    void testToStringTest() {
        assertEquals("Actor{id=1, name='Actor'}", actor.toString());
    }
}