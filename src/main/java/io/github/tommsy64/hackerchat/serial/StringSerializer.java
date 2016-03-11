package io.github.tommsy64.hackerchat.serial;

public class StringSerializer implements Serializer<String> {
    @Override
    public String serialize(String object) {
        return object;
    }

    @Override
    public String deserialize(String object) {
        return object;
    }
}
