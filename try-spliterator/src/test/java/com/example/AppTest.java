package com.example;

import dev.iamgbz3.VariableLengthField;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
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

    public void test3recordsX() {
        String content = ("X".repeat(1012) + "@@").repeat(3);

        try (FileChannel stub = new FileChannelStub(content)) {
            String actual = StreamSupport.stream(new MipSpliterator(stub), false)
                    .map(String::new)
                    .collect(Collectors.joining());

            assertEquals(1012 * 3, actual.length());
            assertTrue(actual.chars().allMatch(ch -> ch == 'X'));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testSameRecords() {
        List<VariableLengthField> fieldsOfRecord = List.of(
                VariableLengthField.of("A", "X", 1),
                VariableLengthField.of("B", "X", 2),
                VariableLengthField.of("C", "X", 3)
        );
        //String content = "ABBCCC".repeat(168) + "ABBC@@CC";
        final String expected = "ABBCCC".repeat(200);
        BiFunction<String, Integer, String> toMip = (src, times) -> {
            byte[] srcBytes = src.getBytes();
            byte[] insertBytes = new byte[]{(byte)0x40, (byte)0x40};
            int insertCount = (srcBytes.length - 1) / 1012 + 1;
            byte[] destBytes = new byte[srcBytes.length + insertCount * 2];
            int srcPos = 0;
            int destPos = 0;
            while (srcPos < srcBytes.length) {
                int copyLen = Math.min(1012, srcBytes.length - srcPos);
                System.arraycopy(srcBytes, srcPos, destBytes, destPos, copyLen);
                srcPos += copyLen;
                destPos += copyLen;
                if (srcPos < srcBytes.length) {
                    System.arraycopy(insertBytes, 0, destBytes, destPos, insertBytes.length);
                    destPos += insertBytes.length;
                }
            }
            return new String(destBytes);
        };

        try (FileChannel stub = new FileChannelStub(toMip.apply(expected, 200))) {
            AtomicInteger recordCount = new AtomicInteger(0);
            String actual = StreamSupport.stream(dev.iamgbz3.MipSpliterator.of(stub, fieldsOfRecord), false)
                    .peek(e -> System.out.printf("#%03d: %s%n", recordCount.incrementAndGet(), String.join("/", e.values())))
                    .map(m -> String.join(":", m.values()))
                    .collect(Collectors.joining());

            assertEquals((fieldsOfRecord.stream().mapToInt(VariableLengthField::size).sum() + fieldsOfRecord.size() - 1) * 200, actual.length());
            assertEquals("A:BB:CCC".repeat(200), actual);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
