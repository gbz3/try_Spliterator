package com.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.IllegalFormatWidthException;
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

            ByteBuffer bb = ByteBuffer.allocate(1012);
            input.read(bb);
            if (bb.position() < 1012) {
                throw new IllegalFormatWidthException(bb.position());
            }

            bb.flip();
            action.accept(bb.array());
            position += 1014;
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
