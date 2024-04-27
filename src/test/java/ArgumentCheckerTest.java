import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ArgumentCheckerTest {
    private ArgumentChecker argumentChecker;
    @Before
    public void setUp() {
        argumentChecker = new ArgumentChecker();
    }
    @Test
    public void testCheckArgumentsCountWithCorrectCount() {
        String message = "command arg1 arg2";
        assertTrue(argumentChecker.checkArgumentsCount(3, message));
    }

    @Test
    public void testCheckArgumentsCountWithIncorrectCount() {
        String message = "command arg1";
        assertFalse(argumentChecker.checkArgumentsCount(3, message));
    }

    @Test
    public void testCheckArgumentsCountWithEmptyMessage() {
        String message = "";
        assertFalse(argumentChecker.checkArgumentsCount(3, message));
    }

    @Test
    public void testIsCommandWithValidCommand() {
        ArgumentChecker argumentChecker = new ArgumentChecker();
        assertTrue(argumentChecker.isCommand("/help"));
    }

    @Test
    public void testIsCommandWithInvalidCommand() {
        ArgumentChecker argumentChecker = new ArgumentChecker();
        assertFalse(argumentChecker.isCommand("/invalidCommand"));
    }

    @Test
    public void testIsCommandWithEmptyString() {
        ArgumentChecker argumentChecker = new ArgumentChecker();
        assertFalse(argumentChecker.isCommand(""));
    }

    @Test
    public void testIsCommandWithNull() {
        ArgumentChecker argumentChecker = new ArgumentChecker();
        assertFalse(argumentChecker.isCommand(null));
    }

    @Test
    public void testIsCommandWithCommandAndAdditionalText() {
        ArgumentChecker argumentChecker = new ArgumentChecker();
        assertTrue(argumentChecker.isCommand("/create newfile.txt"));
    }

    @Test
    public void testIsCommandWithCommandInDifferentCase() {
        ArgumentChecker argumentChecker = new ArgumentChecker();
        assertFalse(argumentChecker.isCommand("/VIEWFILECONTENT"));
    }
}
