package com.everest.util;

public class Identifier {
    private final String type;
    private final String name;

    public Identifier(String type, String name) {
        this.type = type.toLowerCase();
        this.name = name.toLowerCase();
    }

    public String getType() { return type; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return type + ":" + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Identifier)) return false;
        Identifier other = (Identifier) obj;
        return this.type.equals(other.type) && this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return (type + name).hashCode();
    }
}
