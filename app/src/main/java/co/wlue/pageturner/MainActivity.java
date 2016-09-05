package co.wlue.pageturner;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

import co.wlue.pageturner.utils.FixedDoubleStack;
import co.wlue.pageturner.utils.FixedStack;


public class MainActivity extends ActionBarActivity {


    private Button btnStartStop;
    private TextView txtFrequency;
    private TextView txtStrength;
    private TextView txtNote;
    private double[] lastFrequencies;
    private double[] lastStrengths;
    private int historyIndex;
    private int smoothFactor;
    private ArrayList<Double> frequencies;
    private String[] noteNames;
    private Switch methodSelector;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStartStop = (Button) findViewById(R.id.btn_start_stop);
        methodSelector = (Switch) findViewById(R.id.method_selector);
        txtFrequency = (TextView) findViewById(R.id.txt_frequency);
        txtStrength = (TextView) findViewById(R.id.txt_strength);
        txtNote = (TextView) findViewById(R.id.txt_note);
        smoothFactor = 10;
        lastFrequencies = new double[smoothFactor];
        lastStrengths = new double[smoothFactor];
        historyIndex = 0;
        for(int i = 0; i<lastFrequencies.length; i++)
        {
            lastFrequencies[i] = 0;
            lastStrengths[i] = 0;
        }

        frequencies = getAllFrequencies((double) getResources().getInteger(R.integer.A4));
        noteNames = getResources().getStringArray(R.array.notes);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startStop(View view)
    {
        if(started){
            started = false;
            btnStartStop.setText("Start");
        } else {
            started = true;
            btnStartStop.setText("Stop");
            if(!methodSelector.isChecked()) {
                recordTask = new RecordAudio();
                recordTask.execute();
            } else {
                recordTask2 = new RecordAudio2();
                recordTask2.execute();
            }

        }


    }

    int audioSource = MediaRecorder.AudioSource.MIC;    // Audio source is the device MIC
    int channelConfig = AudioFormat.CHANNEL_IN_MONO;    // Recording in mono
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; // Records in 16bit


    int sampleSize = 256;                               // deal with this many samples at a time
    int samplingFrequency = 8000;                             // Sample rate in Hz

    RecordAudio recordTask;                             // Creates a Record Audio command
    RecordAudio2 recordTask2;                             // Creates a Record Audio command
    boolean started = false;
    private RealDoubleFFT transformer;

    private class RecordAudio extends AsyncTask<Void, double[], Void> {

        AudioRecord audioRecord;
        @Override
        protected Void doInBackground(Void... params){

        /*Calculates the fft and frequency of the input*/
            //try{
            transformer = new RealDoubleFFT(sampleSize);
            int bufferSize = AudioRecord.getMinBufferSize(samplingFrequency, channelConfig, audioEncoding);                // Gets the minimum buffer needed
            audioRecord = new AudioRecord(audioSource, samplingFrequency, channelConfig, audioEncoding, bufferSize);   // The RAW PCM sample recording

            int bufferReadResult;

            short[] buffer = new short[sampleSize];          // Save the raw PCM samples as short bytes
            double[] toTransform = new double[sampleSize];
            try{
                audioRecord.startRecording();
            }
            catch(IllegalStateException e){
                Log.e("Recording failed", e.toString());

            }
            while (started) {
                bufferReadResult = audioRecord.read(buffer, 0, sampleSize);
                if(isCancelled())
                    break;

                for (int i = 0; i < sampleSize && i < bufferReadResult; i++) {
                    toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
                }

                transformer.ft(toTransform);

                publishProgress(toTransform);
                if(isCancelled())
                    break;
            }

            try{
                audioRecord.stop();
            }
            catch(IllegalStateException e){
                Log.e("Stop failed", e.toString());

            }

            return null;
        }

        protected void onProgressUpdate(double[]... strengths){
            //print the frequency
            double maximumFrequency = -1;
            double maximumStrength = -1;
                for (int i = 0; i < strengths[0].length; i++)
                {
                    if(strengths[0][i]>maximumStrength)
                    {
                        maximumFrequency = (i)*(samplingFrequency/2)/sampleSize;
                        maximumStrength = strengths[0][i];
                }
            }

            addFrequencyToHistory(maximumFrequency, maximumStrength);
            double[] currentValues = getAverageFrequency();

            int indexOfGuessedFrequency = getIndexOfClosestFrequency(currentValues[0]);
            double guessedFrequency = frequencies.get(indexOfGuessedFrequency);


            CharSequence guessedNote = Html.fromHtml(noteNames[indexOfGuessedFrequency]);

            txtFrequency.setText(Double.toString(guessedFrequency));
            txtNote.setText(guessedNote);
            txtStrength.setText(Double.toString(round(currentValues[1],5)));

        }

        protected void onPostExecute(Void result) {
            try{
                audioRecord.stop();
            }
            catch(IllegalStateException e){
                Log.e("Stop failed", e.toString());

            }
            recordTask.cancel(true);

        }

    }

    public double[] getAverageFrequency()
    {
        double[] result = new double[2];
        double totalFrequency = 0;
        double totalStrength = 0;
        for(int i = 0; i< smoothFactor; i++)
        {
            totalFrequency+=lastFrequencies[i];
            totalStrength+=lastStrengths[i];
        }

        double averageFrequency = totalFrequency/smoothFactor;
        double averageStrength = totalStrength/smoothFactor;
        result[0] = averageFrequency;
        result[1] = averageStrength;
        return result;
    }

    public void addFrequencyToHistory(double frequency, double strength)
    {
        if(historyIndex == smoothFactor)
            historyIndex = 0;
        lastFrequencies[historyIndex] = frequency;
        lastStrengths[historyIndex] = strength;
        historyIndex++;
    }


    public static ArrayList<Double> getAllFrequencies(double A4frequency)
    {

        //57 half steps down from A4
        //50 half steps up from A4
        ArrayList<Double> allFrequencies = new ArrayList<>();
        double newFrequency;
        for(int i = -57; i<51; i++)
        {

            newFrequency = round(A4frequency*Math.pow(Math.pow(2,(1/(double)12)),i),2);
            allFrequencies.add(newFrequency);
        }

        return allFrequencies;
    }

    public int getIndexOfClosestFrequency(double frequency)
    {
        return binarySearch(frequencies, 0, frequencies.size()-1, frequency);
    }

    public static int binarySearch (ArrayList<Double> arr, int first, int last, double key)
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
                double firstVal = arr.get(first);
                double lastVal = arr.get(last);
                if(Math.abs(firstVal-key)<Math.abs(lastVal-key))
                    return first;
                else
                    return last;
            }
        }
        int mid = first + (last - first)/2;
        if (arr.get(mid) == key)
            return mid;
        else if (arr.get(mid) > key)
            return binarySearch(arr, first, mid - 1, key);
        else
            return binarySearch(arr, mid + 1, last, key);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private class RecordAudio2 extends AsyncTask<Void, double[], Void> {

        AudioRecord audioRecord;
        @Override
        protected Void doInBackground(Void... params){

        /*Calculates the fft and frequency of the input*/
            //try{
            transformer = new RealDoubleFFT(sampleSize);
            int bufferSize = AudioRecord.getMinBufferSize(samplingFrequency, channelConfig, audioEncoding);                // Gets the minimum buffer needed
            audioRecord = new AudioRecord(audioSource, samplingFrequency, channelConfig, audioEncoding, bufferSize);   // The RAW PCM sample recording

            int bufferReadResult;

            short[] buffer = new short[sampleSize];          // Save the raw PCM samples as short bytes
            double[] toTransform = new double[sampleSize];
            try{
                audioRecord.startRecording();
            }
            catch(IllegalStateException e){
                Log.e("Recording failed", e.toString());

            }
            while (started) {
                bufferReadResult = audioRecord.read(buffer, 0, sampleSize);
                if(isCancelled())
                    break;

                for (int i = 0; i < sampleSize && i < bufferReadResult; i++) {
                    toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
                }

                transformer.ft(toTransform);

                publishProgress(toTransform);
                if(isCancelled())
                    break;
            }

            try{
                audioRecord.stop();
            }
            catch(IllegalStateException e){
                Log.e("Stop failed", e.toString());

            }

            return null;
        }

        protected void onProgressUpdate(double[]... strengths){
            //print the frequency
            FixedDoubleStack<Double> maxValues = new FixedDoubleStack<>(6);
            for (int i = 0; i < strengths[0].length; i++)
            {
                double frequency = (i)*(samplingFrequency/2)/sampleSize;
                double strength = strengths[0][i];

                maxValues.add(strength,frequency);
            }

            Double[] maximums = maxValues.getTop();
            addFrequencyToHistory(maximums[1], maximums[0]);
            double[] currentValues = getAverageFrequency();

            int indexOfGuessedFrequency = getIndexOfClosestFrequency(currentValues[0]);
            double guessedFrequency = frequencies.get(indexOfGuessedFrequency);


            CharSequence guessedNote = Html.fromHtml(noteNames[indexOfGuessedFrequency]);

            txtFrequency.setText(Double.toString(guessedFrequency));
            txtNote.setText(guessedNote);
            txtStrength.setText(Double.toString(round(currentValues[1],5)));

        }

        protected void onPostExecute(Void result) {
            try{
                audioRecord.stop();
            }
            catch(IllegalStateException e){
                Log.e("Stop failed", e.toString());

            }
            recordTask.cancel(true);

        }

    }

}
