package com.example;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Spliterator;
import java.util.function.Consumer;

public class MipSpliterator implements Spliterator<byte[]> {
    private final FileChannel input;
    private int position = 0;

    public MipSpliterator(FileChannel input) {
        this.input = input;
    }

    @Override
    public boolean tryAdvance(Consumer<? super byte[]> action) {
        try {
            if (position >= input.size()) {
                return false;
            }

            ByteBuffer data = ByteBuffer.allocate(1012);
            ByteBuffer delimiter = ByteBuffer.allocate(2);

            input.read(data);
            input.read(delimiter);
            if (data.position() < data.capacity()
                    || delimiter.position() < delimiter.capacity()
                    || delimiter.array()[0] != '@'
                    || delimiter.array()[1] != '@') {
                throw new InvalidObjectException("Invalid input (data.pos=" + data.position() + " delimiter.pos=" + delimiter.position() + ")");
            }

            data.flip();
            action.accept(data.array());
            position += data.capacity() + delimiter.capacity();
            return true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Spliterator<byte[]> trySplit() {
        return null;  // 並列処理は実装しない
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
