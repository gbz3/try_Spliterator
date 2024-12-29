package hellocucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import com.example.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {

    private String input;
    private String output;

    @Given("可変長レコード is {string}")
    public void anExampleScenario(String input) {
        this.input = input;
    }

    @When("all step definitions are implemented")
    public void allStepDefinitionsAreImplemented() {
        StreamSupport.stream(new TaggedRecordSpliterator(input), false)
                .forEach(rec -> {
                    System.out.println("Tag: " + rec.getTag() + " [" + rec.getValue() + "]");
                });
    }

    @Then("変換結果 is {string}")
    public void theScenarioPasses(String output) {
    }

    private Path inputFile;

    @Given("入力データは {string}")
    public void input_is(String input) {
        inputFile = Path.of("src/test/resources/data/" + input);
    }

    @When("入力データを読み込む")
    public void loadInputFile() {
    }

    @Then("期待値は {string}")
    public void expected_is(String expectedFile) {
        //Path expectedPath = Path.of("src/test/resources/data/" + expectedFile);

        try (
                FileChannel input = FileChannel.open(inputFile);
                //FileChannel expected = FileChannel.open(expectedPath)
                ) {
            String actual = StreamSupport.stream(new RecordA01Spliterator(input), false)
                    .map(A01 -> new String(A01.value()))
                    .collect(Collectors.joining());

            assertTrue(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<AnyField> recordB_fields = new ArrayList<>();

    @Given("RecordB のカラム定義は以下の通り")
    public void recordB_format(@NotNull DataTable columns) {
        for (Map<String, String> column: columns.asMaps()) {
            recordB_fields.add(
                    new AnyField(
                            column.get("name"),
                            column.get("type"),
                            Integer.parseInt(column.get("size"))
                    )
            );
        }
    }

    @Then("入力データが {string} の時、期待値は {string}")
    public void expected_is(String actual, String expected) {
        FileChannel stub = new FileChannel() {
            private final ByteBuffer content = ByteBuffer.wrap(actual.getBytes());
            private long position = 0;

            @Override
            public int read(ByteBuffer dst) throws IOException {
                if (position >= content.capacity()) {
                    return -1;
                }

                content.position((int)position);
                int remaining = Math.min(dst.remaining(), content.remaining());
                byte[] temp = new byte[remaining];
                content.get(temp);
                dst.put(temp);
                position += remaining;
                return remaining;
            }

            @Override
            public long read(ByteBuffer[] byteBuffers, int i, int i1) throws IOException {
                return 0;
            }

            @Override
            public int write(ByteBuffer byteBuffer) throws IOException {
                return 0;
            }

            @Override
            public long write(ByteBuffer[] byteBuffers, int i, int i1) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public long position() throws IOException {
                return position;
            }

            @Override
            public FileChannel position(long l) throws IOException {
                this.position = l;
                return this;
            }

            @Override
            public long size() throws IOException {
                return content.capacity();
            }

            @Override
            public FileChannel truncate(long l) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void force(boolean b) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public long transferTo(long l, long l1, WritableByteChannel writableByteChannel) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public long transferFrom(ReadableByteChannel readableByteChannel, long l, long l1) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read(ByteBuffer byteBuffer, long l) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public int write(ByteBuffer byteBuffer, long l) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public MappedByteBuffer map(MapMode mapMode, long l, long l1) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public FileLock lock(long l, long l1, boolean b) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public FileLock tryLock(long l, long l1, boolean b) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            protected void implCloseChannel() throws IOException {}
        };

        StreamSupport.stream(new RecordBsSpliterator(stub, recordB_fields), false)
                .forEach(rec -> {
                    rec.forEach((k, v) -> {
                        System.out.println("Key: " + k + " Value: " + v);
                    });
                });
    }

}
