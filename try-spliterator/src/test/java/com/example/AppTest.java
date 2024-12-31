package com.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testSpliterator() {
        String content = ("X".repeat(1012) + "@ ").repeat(3);

        try (FileChannel stub = new FileChannelStub(content)) {
            StreamSupport.stream(new MipSpliterator(stub), false)
                    .map(array -> array.length)
                    .forEach( length -> assertEquals(1012, (int) length));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
