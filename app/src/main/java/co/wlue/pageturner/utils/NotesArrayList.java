package co.wlue.pageturner.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by researcher on 14/09/16.
 */
public class NotesArrayList extends ArrayList<NotesWithTick> {

    private boolean containsTick(long tick) {
        return binarySearch(this, 0,this.size()-1,tick);
    }

    private NotesWithTick getTick(long tick) {
        int index = binarySearchIndex(this, 0, this.size()-1, tick);
        if(index!=-1) {
            return this.get(index);
        } else {
            return null;
        }
    }

    public void addNote(long tick, Note note) {
        if(containsTick(tick)) {
            getTick(tick).addNote(note);
        } else {
            NotesWithTick nwt = new NotesWithTick(tick);
            nwt.addNote(note);
            this.add(nwt);
        }
        Collections.sort(this);
    }

    public void setLengthOfNote(long tick, int noteValue, int[] resolutionArray) {
        int index = binarySearchFloorIndex(this, 0, this.size() - 1, tick);
        for(int i = index; i>=0; i--) {
            if(get(i).hasNoteValue(noteValue)) {
                get(i).addLength(tick, noteValue, resolutionArray);
                break;
            }
        }


    }

    private static boolean binarySearch (NotesArrayList arr, int first, int last, long key)
    {

        if (first > last) {
            // if either first or last is negative, return the first element.
            if(first<0||last<0) {
                return false;
            }

                // if either first or last are greater than arr length, return the last element.
            else if(first>=arr.size()-1||last>=arr.size()-1){
                if(arr.get(arr.size()-1).getTick() == key)
                    return true;
                else
                    return false;
            }
                // otherwise, get values in the array for indecies first and last, compare then to
                // your key and return the closest.
            else
            {
                double firstVal = arr.get(first).getTick();
                double lastVal = arr.get(last).getTick();
                if(Math.abs(firstVal-key)<Math.abs(lastVal-key)) {
                    if(arr.get(first).getTick() == key)
                        return true;
                    else
                        return false;
                }
                else {
                    if(arr.get(last).getTick() == key)
                        return true;
                    else
                        return false;
                }
            }
        }
        int mid = first + (last - first)/2;
        if (arr.get(mid).getTick() == key)
            return true;
        else if (arr.get(mid).getTick() > key)
            return binarySearch(arr, first, mid - 1, key);
        else
            return binarySearch(arr, mid + 1, last, key);
    }

    private static int binarySearchIndex (NotesArrayList arr, int first, int last, long key)
    {

        if (first > last) {
            // if either first or last is negative, return the first element.
            if(first<0||last<0) {
                if(arr.get(0).getTick() == key)
                    return 0;
                else
                    return -1;
            }

            // if either first or last are greater than arr length, return the last element.
            else if(first>=arr.size()-1||last>=arr.size()-1){
                if(arr.get(arr.size()-1).getTick() == key)
                    return arr.size()-1;
                else
                    return -1;
            }
            // otherwise, get values in the array for indecies first and last, compare then to
            // your key and return the closest.
            else
            {
                double firstVal = arr.get(first).getTick();
                double lastVal = arr.get(last).getTick();
                if(Math.abs(firstVal-key)<Math.abs(lastVal-key)) {
                    if(arr.get(first).getTick() == key)
                        return first;
                    else
                        return -1;
                }
                else {
                    if(arr.get(last).getTick() == key)
                        return last;
                    else
                        return -1;
                }
            }
        }
        int mid = first + (last - first)/2;
        if (arr.get(mid).getTick() == key)
            return mid;
        else if (arr.get(mid).getTick() > key)
            return binarySearchIndex(arr, first, mid - 1, key);
        else
            return binarySearchIndex(arr, mid + 1, last, key);
    }

    private static int binarySearchFloorIndex (NotesArrayList arr, int first, int last, long key)
    {

        if (first > last) {
            // if either first or last is negative, return the first element.
            if(first<0||last<0)
                return 0;

            // if either first or last are greater than arr length, return the last element.
            else if(first>=arr.size()-1||last>=arr.size()-1)
                return arr.size()-1;
            // otherwise, get values in the array for indecies first and last, compare then to
            // your key and return the closest.
            else
            {
                double firstVal = arr.get(first).getTick();
                double lastVal = arr.get(last).getTick();
                if(Math.abs(firstVal-key)<Math.abs(lastVal-key)) {
                    return first;
                }
                else {
                    return last;
                }
            }
        }
        int mid = first + (last - first)/2;
        if (arr.get(mid).getTick() == key)
            return mid;
        else if (arr.get(mid).getTick() > key)
            return binarySearchFloorIndex(arr, first, mid - 1, key);
        else
            return binarySearchFloorIndex(arr, mid + 1, last, key);
    }

    @Override
    public String toString() {
        return "Notes with Ticks: { \n" + TextUtils.join("\n",this) + "\n }";
    }
}
