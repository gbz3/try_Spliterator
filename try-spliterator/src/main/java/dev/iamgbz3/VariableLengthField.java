package dev.iamgbz3;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record VariableLengthField(String name, String type, int size) {

    @Contract("_, _, _ -> new")
    public static @NotNull VariableLengthField of(String name, String type, int size) {
        return new VariableLengthField(name, type, size);
    }

}
