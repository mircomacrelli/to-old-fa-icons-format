package net.mircomacrelli.fontawesome;

import java.util.Objects;

final class Icon {
    private String unicode;

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Icon icon = (Icon) o;
        return unicode.equals(icon.unicode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unicode);
    }

    @Override
    public String toString() {
        return "Icon{unicode='" + unicode + "'}";
    }
}
