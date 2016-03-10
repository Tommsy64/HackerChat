package io.github.tommsy64.hackerchat;

public interface Serializer<T> {
    public String serialize(T object);

    public T deserialize(String object);
}
