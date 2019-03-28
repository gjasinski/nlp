package lab1;

import java.util.Objects;

public class Key {
    private int ngram;
    private String language;

    public Key() {
    }

    public Key(int ngram, String language) {
        this.ngram = ngram;
        this.language = language;
    }

    public int getNgram() {
        return ngram;
    }

    public void setNgram(int ngram) {
        this.ngram = ngram;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return ngram == key.ngram &&
                Objects.equals(language, key.language);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ngram, language);
    }
}
