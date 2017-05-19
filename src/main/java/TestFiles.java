import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by Dylan on 18/05/17.
 */
public class TestFiles {

    /* @inject test start */

    @Test
    public void parseFile_s0bad1prog_fails() throws Exception {
        testParseFile("s0_bad1.prog", false);
    }
    
    @Test
    public void parseFile_s0bad2prog_fails() throws Exception {
        testParseFile("s0_bad2.prog", false);
    }
    
    @Test
    public void parseFile_s0bad3prog_fails() throws Exception {
        testParseFile("s0_bad3.prog", false);
    }
    
    @Test
    public void parseFile_s0bad4prog_fails() throws Exception {
        testParseFile("s0_bad4.prog", false);
    }
    
    @Test
    public void parseFile_s0fullprog_noErrors() throws Exception {
        testParseFile("s0_full.prog", true);
    }
    
    @Test
    public void parseFile_s0simpleprog_noErrors() throws Exception {
        testParseFile("s0_simple.prog", true);
    }
    
    @Test
    public void parseFile_s1bad1prog_fails() throws Exception {
        testParseFile("s1_bad1.prog", false);
    }
    
    @Test
    public void parseFile_s1bad2prog_fails() throws Exception {
        testParseFile("s1_bad2.prog", false);
    }
    
    @Test
    public void parseFile_s1bad3prog_fails() throws Exception {
        testParseFile("s1_bad3.prog", false);
    }
    
    @Test
    public void parseFile_s1bad4prog_noErrors() throws Exception {
        testParseFile("s1_bad4.prog", true);
    }
    
    @Test
    public void parseFile_s1bad5prog_fails() throws Exception {
        testParseFile("s1_bad5.prog", false);
    }
    
    @Test
    public void parseFile_s1fullprog_noErrors() throws Exception {
        testParseFile("s1_full.prog", true);
    }
    
    @Test
    public void parseFile_s1simpleprog_noErrors() throws Exception {
        testParseFile("s1_simple.prog", true);
    }
    
    @Test
    public void parseFile_s2bad1prog_fails() throws Exception {
        testParseFile("s2_bad1.prog", false);
    }
    
    @Test
    public void parseFile_s2bad2prog_fails() throws Exception {
        testParseFile("s2_bad2.prog", false);
    }
    
    @Test
    public void parseFile_s2bad3prog_fails() throws Exception {
        testParseFile("s2_bad3.prog", false);
    }
    
    @Test
    public void parseFile_s2bad4prog_fails() throws Exception {
        testParseFile("s2_bad4.prog", false);
    }
    
    @Test
    public void parseFile_s2bad5prog_fails() throws Exception {
        testParseFile("s2_bad5.prog", false);
    }
    
    @Test
    public void parseFile_s2bad6prog_fails() throws Exception {
        testParseFile("s2_bad6.prog", false);
    }
    
    @Test
    public void parseFile_s2bad7prog_fails() throws Exception {
        testParseFile("s2_bad7.prog", false);
    }
    
    @Test
    public void parseFile_s2fullprog_noErrors() throws Exception {
        testParseFile("s2_full.prog", true);
    }
    
    @Test
    public void parseFile_s2simpleprog_noErrors() throws Exception {
        testParseFile("s2_simple.prog", true);
    }
    
    @Test
    public void parseFile_s3fullprog_noErrors() throws Exception {
        testParseFile("s3_full.prog", true);
    }
    
    @Test
    public void parseFile_s3simpleprog_noErrors() throws Exception {
        testParseFile("s3_simple.prog", true);
    }
    
    /* @inject test end */

    private void testParseFile(String filePath, boolean shouldSucceed) throws IOException {
        InputStream fileStream = getClass().getClassLoader()
                .getResourceAsStream("TestPrograms/" + filePath);
        try {
            RobotProgramNode programNode = Parser.parseProgram(
                    new Scanner(fileStream),
                    new Logger.SystemOutputLogger()
            );
            assertTrue(programNode.toString(), shouldSucceed);
        } catch (ParserFailureException ignored) {
            assertFalse(ignored.toString(), shouldSucceed);
        }
    }
}
