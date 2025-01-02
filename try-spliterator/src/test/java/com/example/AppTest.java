package com.example;

import dev.iamgbz3.VariableLengthField;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    Function<String, String> toMip = src -> {
        final Charset srcCharset = StandardCharsets.UTF_8;
        final byte[] insertBytes = new byte[]{(byte)0x40, (byte)0x40};
        final int cutSize = 1012;

        byte[] srcBytes = src.getBytes(srcCharset);
        int insertCount = (srcBytes.length - 1) / cutSize + 1;
        byte[] destBytes = new byte[srcBytes.length + insertCount * insertBytes.length];
        for (int srcPos = 0, destPos = 0; srcPos < srcBytes.length;) {
            int copyLen = Math.min(cutSize, srcBytes.length - srcPos);
            System.arraycopy(srcBytes, srcPos, destBytes, destPos, copyLen);
            srcPos += copyLen;
            destPos += copyLen;
            if (srcPos < srcBytes.length) {
                System.arraycopy(insertBytes, 0, destBytes, destPos, insertBytes.length);
                destPos += insertBytes.length;
            }
        }
        return new String(destBytes, srcCharset);
    };

    public void testSameRecords() {
        List<VariableLengthField> fieldsOfRecord = List.of(
                VariableLengthField.of("A", "X", 1),
                VariableLengthField.of("B", "X", 2),
                VariableLengthField.of("C", "X", 3)
        );
        final String expected = "ABBCCC".repeat(200);

        try (FileChannel stub = new FileChannelStub(toMip.apply(expected))) {
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
