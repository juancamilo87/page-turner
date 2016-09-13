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
import com.pdrogfer.mididroid.examples.MidiManipulation;
import com.pdrogfer.mididroid.util.MidiUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by researcher on 12/09/16.
 */
public class MidiViewerActivity extends Activity {

    private String[] noteNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midiviewer);

        noteNames = getResources().getStringArray(R.array.notes);

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

            while(noteOnEvents.hasNext())
            {
                MidiEvent event = noteOnEvents.next();
                if(event instanceof NoteOn) {
                    NoteOn noteOnEvent = (NoteOn) event;

                    int noteValue = noteOnEvent.getNoteValue();
                    int noteVelocity = noteOnEvent.getVelocity();
                    Log.d("PT_MIDI","Note On: " + noteValue + " - Velocity: " + noteVelocity + " Note: " + Html.fromHtml(noteNames[noteValue-12]));

                } else if(event instanceof NoteOff) {
                    NoteOff noteOffEvent = (NoteOff) event;

                    int noteValue = noteOffEvent.getNoteValue();
                    int noteVelocity = noteOffEvent.getVelocity();
                    Log.d("PT_MIDI","Note Off: " + noteValue + " - Velocity: " + noteVelocity + " Note: " + Html.fromHtml(noteNames[noteValue-12]));

                } else {
                    Log.d("PT_MIDI","Event type: " + event);
                }

            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
