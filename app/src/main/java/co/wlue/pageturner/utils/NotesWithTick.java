package co.wlue.pageturner.utils;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by researcher on 14/09/16.
 */
public class NotesWithTick implements Comparable<NotesWithTick>{

    private long tick;
    private ArrayList<Note> notes;

    public NotesWithTick(long tick) {
        this.tick = tick;
        notes = new ArrayList<>();
    }

    public long getTick() {
        return tick;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public boolean hasNoteValue(int noteValue) {
        Note note = getNote(noteValue);
        if(note!=null)
            return true;
        return false;
    }

    public void addLength(long newTick, int noteValue, int[] resolutionArray) {
        Note note = getNote(noteValue);
        long tickDelta = newTick - tick;
        note.setNoteLength(binarySearch(resolutionArray,0,resolutionArray.length - 1,tickDelta));

    }

    private static int binarySearch (int[] arr, int first, int last, long key)
    {

        if (first > last) {
            // if either first or last is negative, return the first element.
            if(first<0||last<0)
                return 0;

                // if either first or last are greater than arr length, return the last element.
            else if(first>=arr.length-1||last>=arr.length-1)
                return arr.length-1;
                // otherwise, get values in the array for indecies first and last, compare then to
                // your key and return the closest.
            else
            {
                double firstVal = arr[first];
                double lastVal = arr[last];
                if(Math.abs(firstVal-key)<Math.abs(lastVal-key)) {
                    return first;
                }
                else {
                    return last;
                }
            }
        }
        int mid = first + (last - first)/2;
        if (arr[mid] == key)
            return mid;
        else if (arr[mid] > key)
            return binarySearch(arr, mid + 1, last, key);
        else
            return binarySearch(arr, first, mid - 1, key);
    }

    public Note getNote(int noteValue) {
        for(int i = 0; i< notes.size(); i++) {
            Note note = notes.get(i);
            if(note.getNoteValue() == noteValue) {
                return note;
            }
        }
        return null;
    }


    @Override
    public int compareTo(NotesWithTick o) {
        return (int)(this.tick - o.tick);
    }

    @Override
    public String toString() {

        return "Tick: " + tick + " Notes: { \n" + TextUtils.join("\n", notes) + "\n }";
    }
}
