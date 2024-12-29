package com.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class RecordBsSpliterator implements Spliterator<LinkedHashMap<String, String>> {
    private final FileChannel input;
    private int position = 0;
    private final List<AnyField> fields;

    public RecordBsSpliterator(FileChannel input, List<AnyField> recordB_fields) {
        this.input = input;
        this.fields = recordB_fields;
    }

    @Override
    public boolean tryAdvance(Consumer<? super LinkedHashMap<String, String>> action) {
        try {
            if (position >= input.size()) {
                return false;
            }

            LinkedHashMap<String, String> record = new LinkedHashMap<>();
            for (AnyField field : fields) {
                ByteBuffer bb = ByteBuffer.allocate(field.size());
                input.read(bb);
                bb.flip();
                String data = StandardCharsets.US_ASCII.decode(bb).toString();
                position += field.size();

                record.put(field.name(), data);
            }
            action.accept(record);

            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Spliterator<LinkedHashMap<String, String>> trySplit() {
        return null;    // 並列処理は実装しない
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
