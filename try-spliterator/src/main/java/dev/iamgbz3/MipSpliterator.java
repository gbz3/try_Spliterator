package dev.iamgbz3;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class MipSpliterator implements Spliterator<LinkedHashMap<String, String>> {
    private final FileChannel input;
    private int filePosition = 0;

    private final List<VariableLengthField> fieldsOfRecord;
    private final int sizeOfRecord;

    private MipSpliterator(FileChannel input, List<VariableLengthField> fields) {
        this.input = input;
        this.fieldsOfRecord = fields;
        this.sizeOfRecord = fields.stream().mapToInt(VariableLengthField::size).sum();
    }

    @Contract("_, _ -> new")
    public static @NotNull MipSpliterator of(FileChannel input, @NotNull List<VariableLengthField> fields) {
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("fields must not be empty");
        }
        return new MipSpliterator(input, fields);
    }

    @Override
    public boolean tryAdvance(Consumer<? super LinkedHashMap<String, String>> action) {
        try {
            if (filePosition + sizeOfRecord > input.size()) {
                return false;
            }

            // split record
            LinkedHashMap<String, String> record = new LinkedHashMap<>();
            for (VariableLengthField field : fieldsOfRecord) {
                ByteBuffer bb = ByteBuffer.allocate(field.size());
                // バッファを埋める
                while (bb.position() < bb.capacity()) {
                    int lengthInMipBlock = filePosition % 1014 + bb.capacity() - bb.position();
                    if (lengthInMipBlock > 1012) {
                        bb.limit(1012 - filePosition % 1014);
                        filePosition += input.read(bb, filePosition) + 2;
                    } else {
                        bb.limit(field.size());
                        filePosition += input.read(bb, filePosition);
                    }
                }
                bb.flip();
                String data = StandardCharsets.US_ASCII.decode(bb).toString();

                record.put(field.name(), data);
            }
            action.accept(record);

            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Spliterator<LinkedHashMap<String, String>> trySplit() {
        // 並列処理は実装しない
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return ORDERED | NONNULL;
    }

}
