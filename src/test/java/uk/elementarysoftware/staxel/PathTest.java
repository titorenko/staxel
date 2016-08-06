package uk.elementarysoftware.staxel;

import static org.junit.Assert.*;

import org.junit.Test;

public class PathTest {
    
    @Test
    public void testEndsWithForEmptyInput() {
        Path p = new Path();
        assertTrue(p.endsWith());
        p.push("location");
        p.push("country");
        assertTrue(p.endsWith());
    }
    
    @Test
    public void testEndsWithHappyScenarios() {
        Path p = new Path();
        p.push("location");
        p.push("country");
        assertTrue(p.endsWith("country"));
        assertTrue(p.endsWith("location", "country"));
        assertFalse(p.endsWith("location"));
    }
}
