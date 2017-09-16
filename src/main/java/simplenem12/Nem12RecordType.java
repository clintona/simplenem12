package simplenem12;

public enum Nem12RecordType {
    NEM12_100("100"), NEM12_200("200"), NEM12_300("300"), NEM12_900("900");

    private final String code;

    Nem12RecordType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
