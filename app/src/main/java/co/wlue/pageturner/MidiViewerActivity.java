package co.wlue.pageturner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import co.wlue.pageturner.midi_utils.MidiFile;
import co.wlue.pageturner.midi_utils.MidiOptions;
import co.wlue.pageturner.midi_utils.SheetMusic;
import co.wlue.pageturner.utils.LicenceKeyInstance;
import co.wlue.pageturner.utils.SeeScoreView;
import co.wlue.pageturner.utils.SystemView;
import uk.co.dolphin_com.sscore.BarGroup;
import uk.co.dolphin_com.sscore.Component;
import uk.co.dolphin_com.sscore.Header;
import uk.co.dolphin_com.sscore.Item;
import uk.co.dolphin_com.sscore.LoadOptions;
import uk.co.dolphin_com.sscore.NoteItem;
import uk.co.dolphin_com.sscore.SScore;
import uk.co.dolphin_com.sscore.Tempo;
import uk.co.dolphin_com.sscore.ex.ScoreException;
import uk.co.dolphin_com.sscore.ex.XMLValidationException;

/**
 * Created by researcher on 12/09/16.
 */
public class MidiViewerActivity extends Activity {

    private String[] noteNames;

    public static final int numberOfNoteLengths = 14;

    private int[] resolutionArray;

    private MidiFile midifile;   /* The midi file to play */
    private MidiOptions options; /* The options for sheet music and sound */
    private long midiCRC;      /* CRC of the midi bytes */
    private LinearLayout layout; /* THe layout */
    private SheetMusic sheet;    /* The sheet music */
    private ScrollView sheetScrollView;

    /**
     * the View which displays the score
     */
    private SeeScoreView ssview;

    /**
     * the current magnification.
     * <p>Preserved to avoid reload on rotate (which causes complete destruction and recreation of this Activity)
     */
    private float magnification;

    /**
     * the current bar preserved on player stop so it can be restarted in the same place
     */
    private int currentBar;

    private static final int kMinTempoBPM = 30;
    private static final int kMaxTempoBPM = 240;
    private static final int kDefaultTempoBPM = 80;
    private static final double kMinTempoScaling = 0.5;
    private static final double kMaxTempoScaling = 2.0;
    private static final double kDefaultTempoScaling = 1.0;

    /**
     * the current viewed score.
     * <p>Preserved to avoid reload on rotate (which causes complete destruction and recreation of this Activity)
     */
    private SScore currentScore;

    private Button testBtn1;
    private Button testBtn2;
    private Button testBtn3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midiviewer);
        testBtn1 = (Button) findViewById(R.id.btn_test1);
        testBtn2 = (Button) findViewById(R.id.btn_test2);
        testBtn3 = (Button) findViewById(R.id.btn_test3);

        noteNames = getResources().getStringArray(R.array.notes);
//        layout = (LinearLayout) findViewById(R.id.midi_viewer_layout);
//        new ByteArrayOutputStream();
//
//        byte[] data = readRawByteArray(getResources().openRawResource(R.raw.bach_846_simplified_rh_and_lh));
//        if (data == null || data.length <= 6 || !hasMidiHeader(data)) {
//            showErrorDialog("Error: Unable to open midi file");
//            return;
//        }
//
//        ClefSymbol.LoadImages(this);
//        TimeSigSymbol.LoadImages(this);
//        MidiPlayer.LoadImages(this);
//
//        String title = "Whatever title";
//        this.setTitle("MidiSheetMusic: " + title);
//        try {
//            midifile = new MidiFile(data, title);
//        }
//        catch (MidiFileException e) {
//            this.finish();
//            return;
//        }
//
//        options = new MidiOptions(midifile);
//        CRC32 crc = new CRC32();
//        crc.update(data);
//        midiCRC = crc.getValue();
//        SharedPreferences settings = getPreferences(0);
//        options.scrollVert = settings.getBoolean("scrollVert", false);
//        options.shade1Color = settings.getInt("shade1Color", options.shade1Color);
//        options.shade2Color = settings.getInt("shade2Color", options.shade2Color);
//        String json = settings.getString("" + midiCRC, null);
//        MidiOptions savedOptions = MidiOptions.fromJson(json);
//        if (savedOptions != null) {
//            options.merge(savedOptions);
//        }
////        createView();
//        createSheetMusic(options);

//        noteNames = getResources().getStringArray(R.array.notes);
//        resolutionArray = new int[numberOfNoteLengths];
//
//        try {
//            MidiFile midi = new MidiFile(getResources().openRawResource(R.raw.bach_846_simplified_rh));
//
//            Log.d("PT_MIDI", "# of tracks: " + midi.getTrackCount());
//
//            MidiTrack track = midi.getTracks().get(0);
//
//            Iterator<MidiEvent> it = track.getEvents().iterator();
//            List<MidiEvent> eventsToRemove = new ArrayList<MidiEvent>();
//
//            while(it.hasNext())
//            {
//                MidiEvent event = it.next();
//
//                if(!(event instanceof NoteOn) && !(event instanceof NoteOff))
//                {
//                    eventsToRemove.add(event);
//                    Log.d("PT_MIDI","Event to remove: " + event);
//                }
//            }
//
//            for(MidiEvent event : eventsToRemove)
//            {
//                track.removeEvent(event);
//            }
//
//
//            Iterator<MidiEvent> noteOnEvents = track.getEvents().iterator();
//            int resolution = midi.getResolution();
//            initializeResolutionArray(resolution);
//            NotesArrayList allNotes = new NotesArrayList();
//            while(noteOnEvents.hasNext())
//            {
//                MidiEvent event = noteOnEvents.next();
//                if(event instanceof NoteOn) {
//                    NoteOn noteOnEvent = (NoteOn) event;
//
//                    int noteValue = noteOnEvent.getNoteValue();
//                    int noteVelocity = noteOnEvent.getVelocity();
//                    long tick = noteOnEvent.getTick();
//
//                    if(noteVelocity != 0) {
//                        Note note = new Note(noteValue, noteVelocity,noteNames[noteValue-12]);
//
//                        allNotes.addNote(tick,note);
//
//                    } else {
//                        allNotes.setLengthOfNote(tick, noteValue, resolutionArray);
//                    }
//                    Log.d("PT_MIDI","Note On: " + noteValue + " - Velocity: " + noteVelocity + " Note: " + Html.fromHtml(noteNames[noteValue-12]));
//
//                } else if(event instanceof NoteOff) {
//                    NoteOff noteOffEvent = (NoteOff) event;
//
//                    int noteValue = noteOffEvent.getNoteValue();
//                    int noteVelocity = noteOffEvent.getVelocity();
//                    long tick = noteOffEvent.getTick();
//                    allNotes.setLengthOfNote(tick, noteValue, resolutionArray);
//                    Log.d("PT_MIDI","Note Off: " + noteValue + " - Velocity: " + noteVelocity + " Note: " + Html.fromHtml(noteNames[noteValue-12]));
//
//                } else {
//                    Log.d("PT_MIDI","Event type: " + event);
//                }
//
//            }
//
//
//            logd("OURFORMAT",allNotes.toString());
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        testBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testOne();
            }
        });

        testBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testTwo();
            }
        });

        testBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testThree();
            }
        });

        currentScore = null;
        currentBar = 0;
        magnification = 1.0F;

        ssview = new SeeScoreView(this, getAssets(), new SeeScoreView.ZoomNotification(){

            public void zoom(float scale) {
//                showZoom(scale);
                magnification = scale;
            }

        }, new SeeScoreView.TapNotification(){
            public void tap(int systemIndex, int partIndex, int barIndex, Component[] components)
            {
                currentBar = barIndex;
//                    if (player != null) {
//                        boolean isPlaying = (player.state() == Player.State.Started);
//                        if (isPlaying)
//                            player.pause();
//                        ssview.setCursorAtBar(testNumber3, SeeScoreView.CursorType.line, 200);
//                        if (isPlaying) {
//                            player.startAt(testNumber3, false/*no countIn*/);
//                        }
//                    }
//                    else
//                        ssview.setCursorAtBar(testNumber3, SeeScoreView.CursorType.box, 200);
                System.out.println("tap system:" + systemIndex + " bar:" + barIndex);
                for (Component comp : components)
                    System.out.println(comp);
            }
        });

        hideBeat();
        sheetScrollView = (ScrollView) findViewById(R.id.scrollView1);
        sheetScrollView.addView(ssview);
        final ViewTreeObserver vto = sheetScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ssview.setHeight(sheetScrollView.getHeight());
                new Thread(new Runnable(){ // load file on background thread

                    public void run() {

                        byte[] data = readRawByteArray(getResources().openRawResource(R.raw.totoro));

                        try {
//                    LoadOptions loadOptions = new LoadOptions(LicenceKeyInstance.SeeScoreLibKey, true);
//                    final SScore score = SScore.loadXMLData(data,loadOptions);
//                    final SScore score = SScore.loadXMLData(data,null);
//                    File extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                    File file = new File(extDir,"totoro.mxl");

                            final SScore score = loadMXLFile(data);
                            new Handler(Looper.getMainLooper()).post(new Runnable(){

                                public void run() {
                                    if (score != null)
                                    {
                                        currentScore = score;
                                        showScore(score); // update score in SeeScoreView on foreground thread
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Log.d("ERROR", "error");
                            e.printStackTrace();
                        }


                    }
                }).start();
                sheetScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


//        sheetScrollView.setOnTouchListener(new View.OnTouchListener(){
//
//            @Override
//            public boolean onTouch(View arg0, MotionEvent event) {
//                return ssview.onTouchEvent(event);
//            }
//
//        });
//        setTempo(kDefaultTempoBPM);
//        SeekBar tempoSlider = (SeekBar) findViewById(R.id.tempoSlider);
//        tempoSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            /**
//             * called on moving the tempo slider. Updates the tempo text and the player tempo if playing
//             */
//            public void onProgressChanged(SeekBar seekBar, int sliderValCents, boolean b) {
//                if (b && currentScore != null)
//                {
//                    if (currentScore.hasDefinedTempo()) {
//                        try {
//                            double scaling = sliderPercentToScaling(sliderValCents);
//                            Tempo tempo = currentScore.tempoAtStart();
//                            setTempoText((int)(scaling * tempo.bpm + 0.5));
////                            if (player != null) {
////                                try {
////                                    player.updateTempo();
////                                } catch (Player.PlayerException ex) {
////                                    System.out.println("Failed to set player tempo " + ex);
////                                }
////                            }
//                        }
//                        catch (ScoreException ex)
//                        {}
//                    } else {
//                        setTempoText(sliderPercentToBPM(sliderValCents));
//                    }
//                } else {
//                    setTempoText(sliderPercentToBPM(sliderValCents));
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        if (savedInstanceState != null) // restore state on device rotation avoiding file reload
//        {
//            String filePath = savedInstanceState.getString(CURRENT_FILE);
//            if (filePath != null && filePath.length() > 0)
//                currentFile = new File(filePath);
//            nextFileIndex = savedInstanceState.getInt(NEXT_FILE_INDEX);
//            magnification = savedInstanceState.getFloat(MAGNIFICATION);
//
//            Object o = getLastNonConfigurationInstance();
//            if (o instanceof SScore)
//            {
//                currentScore = (SScore)o; // onResume updates the ui with this score
//            }
//        }
//        showZoom(magnification);



        Log.d("Done","Done");
    }

    private void initializeResolutionArray(int resolution) {
        int tempResolution = resolution * 4;
        for(int i = 0; i< resolutionArray.length; i++) {
            resolutionArray[i] = tempResolution;
            if (i % 2 == 0) {
                tempResolution = tempResolution*3/4;
            } else {
                tempResolution = tempResolution*2/3;
            }

        }
    }

//    /**
//     * Returns the number of ticks of a 1/32 note
//     * @param resolution is the number of ticks of a quarter note
//     * @return Number of ticks of a 1/32 note
//     */
//    private long minimalTick(int resolution) {
//        return resolution/8;
//    }

    private static void logd(String TAG, String message) {
        int maxLogSize = 2000;
        for(int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            android.util.Log.d(TAG, message.substring(start, end));
        }
    }

    /**
     * Reads a file from /raw/res/ and returns it as a byte array
     * @return byte[] if successful, null otherwise
     */
    public static byte[] readRawByteArray(InputStream is)
    {

        byte[] raw = new byte[] {};
        try {
            raw = new byte[is.available()];
            is.read(raw);
        }
        catch (IOException e) {
            e.printStackTrace();
            raw = null;
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return raw;
    }

    /** Return true if the data starts with the header MTrk */
    boolean hasMidiHeader(byte[] data) {
        String s;
        try {
            s = new String(data, 0, 4, "US-ASCII");
            if (s.equals("MThd"))
                return true;
            else
                return false;
        }
        catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    /** Show an error dialog with the given message */
    void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

//    void createView() {
//        layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        setContentView(layout);
//        layout.requestLayout();
//    }

    /** Create the SheetMusic view with the given options */
    private void
    createSheetMusic(MidiOptions options) {
        if (sheet != null) {
            layout.removeView(sheet);
        }
        sheet = new SheetMusic(this);
        sheet.init(midifile, options);
//        sheet.setPlayer(player);
        layout.addView(sheet);
        layout.requestLayout();
        sheet.callOnDraw();
    }

//    /** display the current zoom value in the TextView label */
//    private void showZoom(float scale) {
//        TextView zoomText = (TextView)findViewById(R.id.zoomLabel);
//        NumberFormat nf = NumberFormat.getNumberInstance();
//        nf.setMaximumFractionDigits(2);
//        zoomText.setText("scale: "+nf.format(scale));
//    }

    private void hideBeat() {
        TextView beatText = (TextView) findViewById(R.id.beatText);
        beatText.setVisibility(TextView.INVISIBLE);
    }

//    private void setTempoText(int tempoVal) {
//        TextView tempoText = (TextView) findViewById(R.id.tempoText);
//        tempoText.setText("" + tempoVal + " BPM");
//    }

    private double sliderPercentToScaling(int percent) {
        return kMinTempoScaling + (percent/100.0) * (kMaxTempoScaling - kMinTempoScaling);
    }

    private int sliderPercentToBPM(int percent) {
        return kMinTempoBPM + (int)((percent/100.0) * (kMaxTempoBPM - kMinTempoBPM));
    }

//    private void setTempo(int bpm) {
//        setTempoSliderValPercent(bpmToSliderPercent(bpm));
//        setTempoText(bpm);
//    }

    private void setTempoSliderValPercent(int percent) {
        SeekBar tempoSlider = (SeekBar) findViewById(R.id.tempoSlider);
        tempoSlider.setProgress(percent);
    }

    private int bpmToSliderPercent(int bpm) {
        return (int)(100.0 * (bpm - kMinTempoBPM) / (double)(kMaxTempoBPM - kMinTempoBPM));
    }

    /**
     * update the UI to show the score
     *
     * @param score the score
     */
    private void showScore(SScore score)
    {
        ssview.setLayoutCompletionHandler(new Runnable(){
            public void run()
            {
                // we could do something here when the score has finished loading
            }
        });
        hideBeat();
//        setPlayButtonImage(PlayPause.play); // show play in menu
//        showTranspose(score);
        //TODO: Trim score to viewable score
        
        ssview.setScore(score, magnification); // relayout after transpose
        showTitle(score);
        // set tempo slider to default tempo
//        if (currentScore.hasDefinedTempo()) {
//            try {
//                Tempo tempo = currentScore.tempoAtStart();
//                setTempoScaling(kDefaultTempoScaling, tempo.bpm);
//            }
//            catch (ScoreException ex)
//            {}
//        } else {
////            setTempo(kDefaultTempoBPM);
//        }
    }

    /**
     * update the transpose TextView with the current transpose setting for the score
     *
     * @param score the score
     */
    private void showTranspose(SScore score)
    {
        int semi = score.getTranspose();
        if (semi == 0)
            setTransposeText("");
        else if (semi > 0)
            setTransposeText("+" + Integer.toString(semi) + " semi");
        else if (semi < 0)
            setTransposeText(Integer.toString(semi) + " semi");
    }

    /**
     * update the titleLabel with the titleText
     *
     * @param score the displayed {@link SScore}
     */
    private void showTitle(SScore score) {
        TextView titleLabel = (TextView) findViewById(R.id.titleLabel);
        titleLabel.setText(titleText(score));
    }

//    private void setTempoScaling(double tempoScaling, int nominalBPM) {
//        setTempoSliderValPercent(scalingToSliderPercent(tempoScaling ));
//        setTempoText(scalingToBPM(tempoScaling, nominalBPM));
//    }

    private int scalingToSliderPercent(double scaling) {
        return (int)(0.5+(100 * ((scaling - kMinTempoScaling) / (kMaxTempoScaling - kMinTempoScaling))));
    }

    private int scalingToBPM(double scaling, int nominalBPM) {
        return  (int)(nominalBPM * scaling);
    }

    /** show the text in the transpose TextView  */
    private void setTransposeText(String text) {
        TextView transposeTextView = (TextView) findViewById(R.id.transposeLabel);
        transposeTextView.setText(text);
    }

    /**
     * get a suitable String to use as a title for the score
     *
     * @param score the {@link SScore}
     * @return the title {@link String}
     */
    private String titleText(SScore score)
    {
        Header header = score.getHeader();
        return header.work_title + " - " + header.composer;
    }

    /**
     * Load the given xml file and return a SScore.
     *
     * @param file the file
     * @return the score
     */
    private SScore loadXMLFile(File file)
    {
        if (!file.getName().endsWith(".xml"))
            return null;
        try
        {
            LoadOptions loadOptions = new LoadOptions(LicenceKeyInstance.SeeScoreLibKey, true);
            return SScore.loadXMLFile(file, loadOptions);
        }
        catch (XMLValidationException e) {
            Log.w("sscore", "loadfile <" + file + "> xml validation error: " + e.getMessage());
        } catch (ScoreException e) {
            Log.w("sscore", "loadfile <" + file + "> error:" + e);
        }
        return null;
    }

    /**
     * load a .mxl file and return a {@link SScore}
     * We use a ZipInputStream to decompress the .mxl data into a UTF-8 XML byte buffer
     *
     * @param data byte array of MXL file
     * @return a {@link SScore}
     */
    private SScore loadMXLFile(byte[] data)
    {
//        if (!file.getName().endsWith(".mxl"))
//            return null;

//        InputStream is;
        try {

//            is = new FileInputStream(file);
            ZipInputStream zis = null;
            try
            {
                zis = new ZipInputStream(new ByteArrayInputStream(data));
//                zis = new ZipInputStream(new BufferedInputStream(is));
                ZipEntry ze;
                while ((ze = zis.getNextEntry()) != null) {
                    if (!ze.getName().startsWith("META-INF") // ignore META-INF/ and container.xml
                            && ze.getName() != "container.xml")
                    {
                        // read from Zip into buffer and copy into ByteArrayOutputStream which is converted to byte array of whole file
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int count;
                        while ((count = zis.read(buffer)) != -1) { // load in 1K chunks
                            os.write(buffer, 0, count);
                        }
                        try
                        {
                            LoadOptions loadOptions = new LoadOptions(LicenceKeyInstance.SeeScoreLibKey, true);
                            return SScore.loadXMLData(os.toByteArray(), loadOptions);
                        }
                        catch (XMLValidationException e)
                        {
                            Log.w("sscore", "load byte array xml validation error: " + e.getMessage());
                        }
                        catch (ScoreException e)
                        {
                            Log.w("sscore", "load byte array error:" + e);
                        }
                    }
                }
            } catch (IOException e) {
                Log.w("Open", "byte array read error", e);
                e.printStackTrace();
            }
            finally {
                if (zis != null)
                    zis.close();
            }
        } catch (IOException e) {
            Log.w("Open", "io exception ", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * load the SeeScoreLib.so library
     */
    static {
        System.loadLibrary("stlport_shared");
        System.loadLibrary("SeeScoreLib");
    }

    private void testOne(){
//        int numBars = currentScore.numBars();
//        Toast.makeText(this, "The number of bars is: " + numBars,Toast.LENGTH_SHORT).show();
        ssview.previousPage();
    }

    private int testNumber = 0;

    private void testTwo() {
//        testNumber++;
//        if(testNumber >= currentScore.numBars())
//            testNumber = 1;
//        if(!ssview.setCursorAtBar(testNumber, SeeScoreView.CursorType.box,1000)) {
//            Toast.makeText(this, "Fail",Toast.LENGTH_SHORT).show();
//        }
        ssview.nextPage();
    }

    private int testNumber2 = 0;
    private int testNumber3 = 0;
    private int testNumber4 = 0;

    private ArrayList<NoteItem> noteItems = null;

    private void testThree() {

        Log.d("Called","called 3");
        int children = ssview.getChildCount();
        Log.d("Called", children + " children inside ssview");
//        for(int i = 0; i< children; i++) {
//            //Each line of the music sheet
//            SystemView sv = (SystemView) ssview.getChildAt(testNumber3);
//            Log.d("Called", "Child: " + i + sv.toString());
//        }

        try {
            if(noteItems == null) {
                int partsCount = currentScore.numParts();
                int barsCount = currentScore.numBars();
                if(testNumber2==partsCount)
                    testNumber2 = 0;
                if(testNumber3== barsCount) {
                    testNumber3 = 0;
                    testNumber2++;
                }

                BarGroup bg = currentScore.getBarContents(testNumber2, testNumber3);
                int numItems = bg.items.length;
                if(numItems>0) {
                    noteItems = new ArrayList<>();
                    for (int j = 0; j < numItems; j++) {
                        if (bg.items[j].type == Item.ItemType_note) {
                            NoteItem theItem = (NoteItem) currentScore.getItemForHandle(testNumber2, testNumber3, bg.items[j].item_h);
                            if(theItem.midipitch != 0) {
                                noteItems.add(theItem);
                            }
                        }
                    }
                    Collections.sort(noteItems, new Comparator<NoteItem>() {
                        public int compare(NoteItem o1, NoteItem o2) {
                            if (o1.start == o2.start)
                                return 0;
                            return o1.start < o2.start ? -1 : 1;
                        }
                    });
                    if (noteItems.size() > 0) {
                        NoteItem item = noteItems.remove(0);
                        SystemView sv = ssview.getSystemViewForBar(testNumber3);
                        SystemView svTemp = ssview.getSystemViewForBar(testNumber3-1);
                        if(svTemp!=null) {
                            svTemp.colourItem(0);
                        }
                        if (sv != null) {
                            sv.colourItem(item.item_h);
                            CharSequence guessedNote = Html.fromHtml(noteNames[item.midipitch-12]);
                            Toast.makeText(this,"The note is: " + item.midipitch + " - " + guessedNote, Toast.LENGTH_SHORT).show();
                            Log.d("Note","The note is: " + item.midipitch + " - " + guessedNote);
                        }
                        else
                            Log.d("Error", "SystemView is null");
                    }
                    if (noteItems.size() == 0) {
                        noteItems = null;
                        testNumber3++;
                    }
                }
                else {
                    testNumber3++;
                    testThree();
                }
            }
            else {
                if(noteItems.size()>0) {
                    NoteItem item = noteItems.remove(0);
                    SystemView svTemp = ssview.getSystemViewForBar(testNumber3-1);
                    if(svTemp!=null) {
                        svTemp.colourItem(0);
                    }
                    SystemView sv = ssview.getSystemViewForBar(testNumber3);
                    if(sv!=null) {
                        sv.colourItem(item.item_h);
                        CharSequence guessedNote = Html.fromHtml(noteNames[item.midipitch-12]);
                        Toast.makeText(this,"The note is: " + item.midipitch + " - " + guessedNote, Toast.LENGTH_SHORT).show();
                        Log.d("Note","The note is: " + item.midipitch + " - " + guessedNote);
                    }
                    else
                        Log.d("Error", "SystemView is null");
                }
                if(noteItems.size()==0) {
                    noteItems = null;
                    testNumber3++;
                }
            }



        } catch (ScoreException e) {
            e.printStackTrace();
        }



//        if(testNumber3 >= children) {
//            Toast.makeText(this, "End of score",Toast.LENGTH_SHORT).show();
//            testNumber3 = 0;
//            testNumber2 = 0;
//        }
//        else {
//
//            SystemView sv = (SystemView) ssview.getChildAt(testNumber3);
//
//
//        }


    }
}
