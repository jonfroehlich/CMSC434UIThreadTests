package edu.umd.hcil.uithreadtest;

/**
 * Created by jon on 4/4/2016.
 */
public enum ThreadImplementation {
    DoWorkOnUIThread,
    DoWorkInSeparateThreadButIncorrectly,
    DoWorkInSeparateThreadCallActivityRunOnUIThread,
    DoWorkInSeparateThreadCallViewPostRunnable,
    DoWorkInSeparateThreadDownloadDataAsyncTask
}
