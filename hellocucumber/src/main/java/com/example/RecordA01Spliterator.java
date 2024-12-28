package com.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Spliterator;
import java.util.function.Consumer;

public class RecordA01Spliterator implements Spliterator<RecordA01> {
    private final FileChannel channel;
    private long position;
    private final long end;

    public RecordA01Spliterator(FileChannel ch) throws IOException {
        channel = ch;
        position = 0;
        end = ch.size();
    }

    @Override
    public boolean tryAdvance(Consumer<? super RecordA01> action) {
        try {
            if (position >= end) {
                return false;
            }

            // タグ長判定
            ByteBuffer tagLeadBuff = ByteBuffer.allocate(2);
            channel.read(tagLeadBuff, position);
            tagLeadBuff.flip();
            String tagLead = StandardCharsets.US_ASCII.decode(tagLeadBuff).toString();
            int tagLength = tagLead.equals("XX")? 4: 2;

            // タグ取得
            ByteBuffer tagBuff = ByteBuffer.allocate(tagLength);
            channel.read(tagBuff, position);
            tagBuff.flip();
            String tag = StandardCharsets.US_ASCII.decode(tagBuff).toString();

            // データ長読み込み
            ByteBuffer lengthBuff = ByteBuffer.allocate(3);
            channel.read(lengthBuff);
            lengthBuff.flip();

            // アスキー数字をデータ長に変換
            byte[] lengthBytes = lengthBuff.array();
            int dataLength = (lengthBytes[0] - '0') * 100 + (lengthBytes[1] - '0') * 10 + (lengthBytes[2] - '0');

            // データ読み込み
            ByteBuffer dataBuff = ByteBuffer.allocate(dataLength);
            channel.read(dataBuff);

            action.accept(new RecordA01(tag, dataBuff.array()));

            position += tagLength + 3 + dataLength;
            return true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Spliterator<RecordA01> trySplit() {
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
