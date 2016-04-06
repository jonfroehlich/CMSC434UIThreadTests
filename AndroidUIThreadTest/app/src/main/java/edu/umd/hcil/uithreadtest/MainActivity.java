package edu.umd.hcil.uithreadtest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Random;

/**
 * Demonstrates five different ways of performing background computation in Android, as specified
 * by the custom enum ThreadImplementation
 *
 * 1. DoWorkOnUIThread: performs the background work on the UI thread. You should not do this. If
 *    you run the code, you will see that this makes the UI unresponsive.
 * 2. DoWorkInSeparateThreadButIncorrectly: performs background work on separate thread--which is the
 *    desired behavior--but this separate thread also tries to modify the UI, which will cause an exception
 * 3. DoWorkInSeparateThreadCallActivityRunOnUIThread: performs background work on a separate thread
 *    and communicates with UI thread via Activity.runOnUiThread
 * 4. DoWorkInSeparateThreadCallViewPostRunnable: performs background work on a separate thread
 *    and communicates with the UI thread via View.post
 * 5. DoWorkInSeparateThreadDownloadDataAsyncTask: performs background work on a separate thread
 *    and communicates with the UI via AsyncTask
 */
public class MainActivity extends AppCompatActivity {

    private final int NUM_FILES_TO_DOWNLOAD = 100;
    private final int MAX_THREAD_SLEEP_TIME_IN_MS = 100;

    private ThreadImplementation _threadImplementation = ThreadImplementation.DoWorkOnUIThread;
    private boolean _cancelDownloadClicked = false;
    private Button _buttonCancel;
    private Button _buttonDownload;
    private ProgressBar _progressBar;
    private DownloadDataAsyncTask _downloadDataAsyncTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _buttonDownload = (Button)findViewById(R.id.buttonDownload);
        _buttonCancel = (Button)findViewById(R.id.buttonCancel);
        _progressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    public void OnButtonClickDownload(View view){
        // Disable start download button, enable cancel button, and grab selected thread implementation
        // in Combobox
        _buttonDownload.setEnabled(false);
        _buttonCancel.setEnabled(true);
        _threadImplementation = getSelectedThreadImplementation();

        switch(_threadImplementation){
            case DoWorkOnUIThread:
                downloadData();
                break;
            case DoWorkInSeparateThreadButIncorrectly:
                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        downloadData();
                    }
                });
                thread.start();
                break;

            case DoWorkInSeparateThreadCallActivityRunOnUIThread:
                // See: http://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
                Thread thread2 = new Thread(new DownloadDataCallActivityRunOnUIThreadRunnable(this));
                thread2.start();
                break;

            case DoWorkInSeparateThreadCallViewPostRunnable:
                // See: http://developer.android.com/reference/android/view/View.html#post(java.lang.Runnable)
                Thread thread3 = new Thread(new DownloadDataCallViewPostRunnable(this));
                thread3.start();
                break;

            case DoWorkInSeparateThreadDownloadDataAsyncTask:
                // See: http://developer.android.com/reference/android/os/AsyncTask.html
                _downloadDataAsyncTask = new DownloadDataAsyncTask(this);
                _downloadDataAsyncTask.execute(NUM_FILES_TO_DOWNLOAD);
                break;
        }
    }

    /**
     * Gets the selected thread implementation from the radio button group
     * @return ThreadImplementation representing the selected radio button
     */
    public ThreadImplementation getSelectedThreadImplementation(){
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        //String selectedRadioButtonResourceName = this.getResources().getResourceEntryName(selectedRadioButtonId);
        RadioButton radiobutton = (RadioButton)findViewById(selectedRadioButtonId);
        return ThreadImplementation.valueOf(radiobutton.getText().toString());
    }

    /**
     * Called when the Cancel button is clicked. Cancels the download
     * @param view
     */
    public void OnButtonClickCancel(View view){
        _cancelDownloadClicked = true;

        if(_downloadDataAsyncTask != null){
            _downloadDataAsyncTask.cancel(true);
            resetUIAndStateTrackingVariables();
        }

        _downloadDataAsyncTask = null;
    }

    /**
     * This is a dummy method but simulates a long-running method--such as downloading data--that
     * should *not* be executed on the UI thread.
     */
    private void downloadData() {
        final int numFilesToDownload = NUM_FILES_TO_DOWNLOAD;
        final int maxSleeptimeInMS = MAX_THREAD_SLEEP_TIME_IN_MS; //in milliseconds
        Random random = new Random();

        for (int i = 0; i < NUM_FILES_TO_DOWNLOAD && !_cancelDownloadClicked; i++)
        {
            try {
                // This simulates the download
                Thread.sleep(random.nextInt(maxSleeptimeInMS));
            }catch(InterruptedException e){
                Log.e("DownloadData", e.getMessage());
            }

            float fractionCompleted = (float)(i + 1) / numFilesToDownload;
            setProgressBar(fractionCompleted);
        }

        resetUIAndStateTrackingVariables();
    }

    /**
     * Sets the progress bar to the provided value
     * @param fraction
     */
    public void setProgressBar(float fraction){
        float progress = _progressBar.getMax() * fraction;
        _progressBar.setProgress((int)progress);
    }

    public void resetUIAndStateTrackingVariables(){
        // Reset UI and state tracking variables
        _buttonDownload.setEnabled(true);
        _buttonCancel.setEnabled(false);
        _cancelDownloadClicked = false;
    }

    /**
     * Downloads files and communicates with UI thread via the call Activity.runOnUiThread()
     * See:
     *  http://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
     *  http://developer.android.com/guide/components/processes-and-threads.html#WorkerThreads
     */
    private class DownloadDataCallActivityRunOnUIThreadRunnable implements Runnable{

        MainActivity _mainActivity;

        public DownloadDataCallActivityRunOnUIThreadRunnable(MainActivity mainActivity){
            _mainActivity = mainActivity;
        }

        public void run(){
            final int numFilesToDownload = NUM_FILES_TO_DOWNLOAD;
            final int maxSleeptimeInMS = MAX_THREAD_SLEEP_TIME_IN_MS; //in milliseconds
            Random random = new Random();

            for (int i = 0; i < NUM_FILES_TO_DOWNLOAD && !_cancelDownloadClicked; i++)
            {
                try {
                    // This simulates the download
                    Thread.sleep(random.nextInt(maxSleeptimeInMS));
                }catch(InterruptedException e){
                    Log.e("DownloadData", e.getMessage());
                }

                final float fractionCompleted = (float)(i + 1) / numFilesToDownload;

                // Runs the specified action on the UI thread
                // See: http://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
                _mainActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        _mainActivity.setProgressBar(fractionCompleted);
                    }
                });
            }

            _mainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    _mainActivity.resetUIAndStateTrackingVariables();
                }
            });
        }
    }

    /**
     * Runs UI related methods using View.Post, which puts a Runnable on the UI thread's message queue
     * See:
     *  http://developer.android.com/reference/android/view/View.html#post(java.lang.Runnable)
     *  http://developer.android.com/guide/components/processes-and-threads.html#WorkerThreads
     */
    private class DownloadDataCallViewPostRunnable implements Runnable{

        MainActivity _mainActivity;

        public DownloadDataCallViewPostRunnable(MainActivity mainActivity){
            _mainActivity = mainActivity;
        }

        public void run(){
            final int numFilesToDownload = NUM_FILES_TO_DOWNLOAD;
            final int maxSleeptimeInMS = MAX_THREAD_SLEEP_TIME_IN_MS; //in milliseconds
            ProgressBar progressBar = (ProgressBar)_mainActivity.findViewById(R.id.progressBar);
            Random random = new Random();

            for (int i = 0; i < NUM_FILES_TO_DOWNLOAD && !_cancelDownloadClicked; i++)
            {
                try {
                    // This simulates the download
                    Thread.sleep(random.nextInt(maxSleeptimeInMS));
                }catch(InterruptedException e){
                    Log.e("DownloadData", e.getMessage());
                }

                final float fractionCompleted = (float)(i + 1) / numFilesToDownload;

                // View.Post posts this Runnable on the UI thread's message queue
                // See: http://developer.android.com/reference/android/view/View.html#post(java.lang.Runnable)
                progressBar.post(new Runnable() {
                    public void run() {
                        _mainActivity.setProgressBar(fractionCompleted);
                    }
                });
            }

            // View.Post posts this Runnable on the UI thread's message queue
            // See: http://developer.android.com/reference/android/view/View.html#post(java.lang.Runnable)
            progressBar.post(new Runnable() {
                public void run() {
                    _mainActivity.resetUIAndStateTrackingVariables();
                }
            });
        }
    }

    /**
     * ASyncTask enables proper and easy use of the UI thread. It's probably my favorite approach.
     *
     * An asynchronous task is defined by 3 generic types, called Params, Progress and Result, and
     * 4 steps, called onPreExecute, doInBackground, onProgressUpdate and onPostExecute.
     *
     * The 3 Generic Types:
     * The three types used by an asynchronous task are the following:
     *   1. Params, the type of the parameters sent to the task upon execution.
     *   2. Progress, the type of the progress units published during the background computation.
     *   3. Result, the type of the result of the background computation.
     * Not all types are always used by an asynchronous task. To mark a type as unused, simply use the type Void:
     *
     * The Four Steps (Methods) That You Can Override
     *
     *   1. onPreExecute(), invoked on the UI thread before the task is executed. This step is normally used to setup the task,
     *      for instance by showing a progress bar in the user interface.
     *   2. doInBackground(Params...), invoked on the background thread immediately after onPreExecute() finishes executing.
     *      This step is used to perform background computation that can take a long time. The parameters of the asynchronous task are passed to this step.
     *      The result of the computation must be returned by this step and will be passed back to the last step. This step can also use publishProgress(Progress...) to publish
     *      one or more units of progress. These values are published on the UI thread, in the onProgressUpdate(Progress...) step.
     *   3. onProgressUpdate(Progress...), invoked on the UI thread after a call to publishProgress(Progress...). The timing of the execution is undefined.
     *      This method is used to display any form of progress in the user interface while the background computation is still executing. For instance, it
     *      can be used to animate a progress bar or show logs in a text field.
     *   4. onPostExecute(Result), invoked on the UI thread after the background computation finishes. The result of the background computation is passed to this step as a parameter.
     *
     * See:
     *   http://developer.android.com/reference/android/os/AsyncTask.html
     *   http://developer.android.com/guide/components/processes-and-threads.html
     */
    private class DownloadDataAsyncTask extends AsyncTask<Integer, Float, Integer> {

        private MainActivity _mainActivity;

        public DownloadDataAsyncTask(MainActivity mainActivity){
            _mainActivity = mainActivity;
        }

        protected void onPreExecute(){
            // nothing
        }

        protected Integer doInBackground(Integer... numFiles) {
            int numFilesToDownload = numFiles[0];
            Random random = new Random();

            int numFilesDownloaded = 0;
            for (numFilesDownloaded = 0; numFilesDownloaded < numFilesToDownload &&
                    !isCancelled(); numFilesDownloaded++)
            {
                try {
                    // This simulates the download
                    Thread.sleep(random.nextInt(MAX_THREAD_SLEEP_TIME_IN_MS));
                }catch(InterruptedException e){
                    Log.e("DownloadData", e.getMessage());
                }

                float fractionCompleted = (float)(numFilesDownloaded + 1) / numFilesToDownload;
                publishProgress(fractionCompleted);
            }

            return numFilesDownloaded;
        }

        protected void onProgressUpdate(Float... progressFraction) {
            _mainActivity.setProgressBar(progressFraction[0]);
        }

        protected void onPostExecute(Integer result) {
            _mainActivity.resetUIAndStateTrackingVariables();
        }
    }


}
