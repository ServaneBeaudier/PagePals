package com.pagepals.circle.modeltest;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import com.pagepals.circle.model.Message;
import com.pagepals.circle.model.Circle;

class MessageTest {

    @Test
    void testGettersSetters() {
        Message message = new Message();

        message.setId(1L);
        message.setAuteurId(10L);
        message.setContenu("Contenu du message");
        message.setDateEnvoi(LocalDateTime.now());

        Circle circle = new Circle();
        circle.setId(100L);
        message.setCircle(circle);

        assertEquals(1L, message.getId());
        assertEquals(10L, message.getAuteurId());
        assertEquals("Contenu du message", message.getContenu());
        assertNotNull(message.getDateEnvoi());
        assertEquals(100L, message.getCircle().getId());
    }

    @Test
    void testBuilder() {
        Circle circle = new Circle();
        circle.setId(100L);

        Message message = Message.builder()
                .id(1L)
                .auteurId(10L)
                .contenu("Contenu du message")
                .dateEnvoi(LocalDateTime.now())
                .circle(circle)
                .build();

        assertEquals(1L, message.getId());
        assertEquals(10L, message.getAuteurId());
        assertEquals("Contenu du message", message.getContenu());
        assertNotNull(message.getDateEnvoi());
        assertEquals(100L, message.getCircle().getId());
    }

    @Test
    void testEqualsAndHashCode() {
        Circle circle = new Circle();
        circle.setId(100L);

        Message msg1 = Message.builder()
                .id(1L)
                .auteurId(10L)
                .contenu("Contenu du message")
                .dateEnvoi(LocalDateTime.now())
                .circle(circle)
                .build();

        Message msg2 = Message.builder()
                .id(1L)
                .auteurId(10L)
                .contenu("Contenu du message")
                .dateEnvoi(msg1.getDateEnvoi())
                .circle(circle)
                .build();

        assertEquals(msg1, msg2);
        assertEquals(msg1.hashCode(), msg2.hashCode());
    }

    @Test
    void testToString() {
        Circle circle = new Circle();
        circle.setId(100L);

        Message message = Message.builder()
                .id(1L)
                .auteurId(10L)
                .contenu("Contenu du message")
                .dateEnvoi(LocalDateTime.now())
                .circle(circle)
                .build();

        String str = message.toString();
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("auteurId=10"));
        assertTrue(str.contains("contenu=Contenu du message"));
        assertTrue(str.contains("circle=")); // la représentation toString de Circle
    }
}
