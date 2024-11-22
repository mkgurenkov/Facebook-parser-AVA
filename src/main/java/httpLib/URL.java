package httpLib;

public class URL {
    private StringBuilder value;

    public URL(String value) {
        this.value = new StringBuilder(value);
    }

    public URL addParameter(String name, String value) {
        for (int i = this.value.length() - 1; i > 0; i --) {
            if (this.value.charAt(i) == '&' || this.value.charAt(i) == '?') { //значит уже есть какие-то параметры
                this.value.append("&").append(name).append("=").append(value);
                return this;
            }
        }
        this.value.append("?").append(name).append("=").append(value);
        return this;
    }

    public URL add(String value) {
        this.value.append(value);
        return this;
    }

    public String getValue() {
        return value.toString();
    }

    public void setValue(String value) {
        this.value = new StringBuilder(value);
    }
}
