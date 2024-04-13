import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.example.internal.ArgumentChecker;

public class ArgumentCheckerTest {
    private ArgumentChecker argumentChecker;
    @Before
    public void setUp() {
        argumentChecker = new ArgumentChecker();
    }
    @Test
    public void testCheckArgumentsWithCorrectCount() {
        String message = "command arg1 arg2";
        assertTrue(argumentChecker.checkArguments(3, message));
    }

    @Test
    public void testCheckArgumentsWithIncorrectCount() {
        String message = "command arg1";
        assertFalse(argumentChecker.checkArguments(3, message));
    }

    @Test
    public void testCheckArgumentsWithEmptyMessage() {
        String message = "";
        assertFalse(argumentChecker.checkArguments(3, message));
    }
}
