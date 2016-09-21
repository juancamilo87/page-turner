package co.wlue.pageturner.utils;

/**
 * Created by researcher on 14/09/16.
 */
public class Note {

    private int noteValue;
    private int noteLength;
    private int noteVelocity;
    private String noteName;

    public Note(int noteValue, int noteLength, int noteVelocity, String noteName) {
        this.noteValue = noteValue;
        this.noteLength = noteLength;
        this.noteVelocity = noteVelocity;
        this.noteName = noteName;
    }

    public Note(int noteValue, int noteVelocity, String noteName) {
        this.noteValue = noteValue;
        this.noteVelocity = noteVelocity;
        this.noteName = noteName;
    }

    public int getNoteValue() {
        return noteValue;
    }

    public void setNoteValue(int noteValue) {
        this.noteValue = noteValue;
    }

    public int getNoteLength() {
        return noteLength;
    }

    public void setNoteLength(int noteLength) {
        this.noteLength = noteLength;
    }

    public int getNoteVelocity() {
        return noteVelocity;
    }

    public void setNoteVelocity(int noteVelocity) {
        this.noteVelocity = noteVelocity;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    @Override
    public String toString() {
        return "Value: " + noteValue + " Length: " + noteLength + " Velocity: " + noteVelocity + " Name: " + noteName;
    }
}
