package co.wlue.pageturner;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import com.pdrogfer.mididroid.MidiFile;
import com.pdrogfer.mididroid.MidiTrack;
import com.pdrogfer.mididroid.event.MidiEvent;
import com.pdrogfer.mididroid.event.NoteOff;
import com.pdrogfer.mididroid.event.NoteOn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import co.wlue.pageturner.utils.Note;
import co.wlue.pageturner.utils.NotesArrayList;

/**
 * Created by researcher on 12/09/16.
 */
public class MidiViewerActivity extends Activity {

    private String[] noteNames;

    private int[] resolutionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midiviewer);

        noteNames = getResources().getStringArray(R.array.notes);
        resolutionArray = new int[15];

        try {
            MidiFile midi = new MidiFile(getResources().openRawResource(R.raw.bach_846_simplified_rh));

            Log.d("PT_MIDI", "# of tracks: " + midi.getTrackCount());

            MidiTrack track = midi.getTracks().get(0);

            Iterator<MidiEvent> it = track.getEvents().iterator();
            List<MidiEvent> eventsToRemove = new ArrayList<MidiEvent>();

            while(it.hasNext())
            {
                MidiEvent event = it.next();

                if(!(event instanceof NoteOn) && !(event instanceof NoteOff))
                {
                    eventsToRemove.add(event);
                    Log.d("PT_MIDI","Event to remove: " + event);
                }
            }

            for(MidiEvent event : eventsToRemove)
            {
                track.removeEvent(event);
            }


            Iterator<MidiEvent> noteOnEvents = track.getEvents().iterator();
            int resolution = midi.getResolution();
            initializeResolutionArray(resolution);
            NotesArrayList allNotes = new NotesArrayList();
            while(noteOnEvents.hasNext())
            {
                MidiEvent event = noteOnEvents.next();
                if(event instanceof NoteOn) {
                    NoteOn noteOnEvent = (NoteOn) event;

                    int noteValue = noteOnEvent.getNoteValue();
                    int noteVelocity = noteOnEvent.getVelocity();
                    long tick = noteOnEvent.getTick();

                    if(noteVelocity != 0) {
                        Note note = new Note(noteValue, noteVelocity,noteNames[noteValue-12]);

                        allNotes.addNote(tick,note);

                    } else {
                        allNotes.setLengthOfNote(tick, noteValue, resolutionArray);
                    }
                    Log.d("PT_MIDI","Note On: " + noteValue + " - Velocity: " + noteVelocity + " Note: " + Html.fromHtml(noteNames[noteValue-12]));

                } else if(event instanceof NoteOff) {
                    NoteOff noteOffEvent = (NoteOff) event;

                    int noteValue = noteOffEvent.getNoteValue();
                    int noteVelocity = noteOffEvent.getVelocity();
                    long tick = noteOffEvent.getTick();
                    allNotes.setLengthOfNote(tick, noteValue, resolutionArray);
                    Log.d("PT_MIDI","Note Off: " + noteValue + " - Velocity: " + noteVelocity + " Note: " + Html.fromHtml(noteNames[noteValue-12]));

                } else {
                    Log.d("PT_MIDI","Event type: " + event);
                }

            }


            logd("OURFORMAT",allNotes.toString());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeResolutionArray(int resolution) {
        int tempResolution = resolution * 4;
        for(int i = 0; i< resolutionArray.length; i++) {
            resolutionArray[i] = tempResolution;
            tempResolution = tempResolution/2;
        }
    }

    private static void logd(String TAG, String message) {
        int maxLogSize = 2000;
        for(int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            android.util.Log.d(TAG, message.substring(start, end));
        }
    }
}
