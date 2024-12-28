package com.example;

import java.util.Spliterator;
import java.util.function.Consumer;

public class TaggedRecordSpliterator implements Spliterator<TaggedRecord> {
    private final String input;
    private int position = 0;

    public TaggedRecordSpliterator(String input) {
        this.input = input;
    }

    @Override
    public boolean tryAdvance(Consumer<? super TaggedRecord> action) {
        if (position >= input.length()) {
            return false;
        }

        // 先頭2バイトを読んでタグ長を決定
        String initialTag = input.substring(position, position + 2);
        int tagLength = determineTagLength(initialTag);

        // 完全なタグの読み取り
        String tag = tagLength == 2 ? initialTag :
                input.substring(position, position + tagLength);
        position += tagLength;

        // データ長の読み取り
        int length = Integer.parseInt(input.substring(position, position + 3));
        position += 3;

        // データの読み取り
        String data = input.substring(position, position + length);
        position += length;

        action.accept(new TaggedRecord(tag, data));
        return true;
    }

    private int determineTagLength(String initialTag) {
        // タグの長さを決定するロジック
        // 例: 特定のプレフィックスの場合は4バイト、それ以外は2バイト
        return initialTag.startsWith("XX") ? 4 : 2;
    }

    @Override
    public Spliterator<TaggedRecord> trySplit() {
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
