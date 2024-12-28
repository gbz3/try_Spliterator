package com.example;

public class TaggedRecord {
    private final String tag;
    private final String value;

    public TaggedRecord(String tag, String value) {
        this.tag = tag;
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public String getValue() {
        return value;
    }

}
