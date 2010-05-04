package org.openxdr;

public final class Union {
    private final int type;
    private final Object value;

    public Union(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    public final int getType() {
        return type;
    }

    public final Object getValue() {
        return value;
    }
}
