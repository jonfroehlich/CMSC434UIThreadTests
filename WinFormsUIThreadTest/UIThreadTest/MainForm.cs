using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace UIThreadTest
{
    /// <summary>
    /// This form does three things:
    ///   1. Shows what happens when you try to do lots of work on UI thread (ThreadImplementation.DoWorkOnUIThread)
    ///   2. Shows what happens when you try to modify the UI from non-UI thread (ThreadImplementation.DoWorkInSeparateThreadButIncorrectly)
    ///   2. Shows what happens with correct implementation (ThreadImplementation.DoWorkInSeparateThread)
    ///   
    /// See:
    ///   https://msdn.microsoft.com/en-us/library/ms171728(v=vs.100).aspx
    /// </summary>
    public partial class MainForm : Form
    {
        // Delegates are used to pass methods as arguments to other methods in C#. In this way,
        // a delegate is like a C++ function pointer. 
        private delegate void SetProgressBarDelegate(float fractionCompleted);
        
        private bool _cancelDownloadClicked = false;
        private enum ThreadImplementation { DoWorkOnUIThread, DoWorkInSeparateThreadButIncorrectly, DoWorkInSeparateThread };
        private ThreadImplementation _threadImplementation = ThreadImplementation.DoWorkOnUIThread;

        public MainForm()
        {  
            InitializeComponent();

            // setup the combobox with the various implementations of this application
            _comboBoxThreadImplementation.Items.Add(ThreadImplementation.DoWorkOnUIThread);
            _comboBoxThreadImplementation.Items.Add(ThreadImplementation.DoWorkInSeparateThreadButIncorrectly);
            _comboBoxThreadImplementation.Items.Add(ThreadImplementation.DoWorkInSeparateThread);
            _comboBoxThreadImplementation.SelectedIndex = 0;
        }

        /// <summary>
        /// Called when download button clicked. Starts download.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnButtonStartDownloadClick(object sender, EventArgs e)
        {
            // Disable start download button, enable cancel button, and grab selected thread implementation
            // in Combobox
            _buttonStartDownload.Enabled = false;
            _buttonCancel.Enabled = true;
            _threadImplementation = (ThreadImplementation)_comboBoxThreadImplementation.SelectedItem;

            // Start the correct implementation
            switch (_threadImplementation)
            {
                case ThreadImplementation.DoWorkOnUIThread:
                    // Download data from UI thread
                    DownloadData();
                    break;
                case ThreadImplementation.DoWorkInSeparateThreadButIncorrectly:
                case ThreadImplementation.DoWorkInSeparateThread:
                    // If we're here, then we should do work in a separate thread
                    // Create a new thread to download the data
                    Thread thread = new Thread(new ThreadStart(DownloadData));
                    thread.Start();
                    break;
            }
        }

        /// <summary>
        /// Called when cancel button clicked. Cancels the download
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnButtonCancelClick(object sender, EventArgs e)
        {
            _cancelDownloadClicked = true;
        }

        /// <summary>
        /// This is a dummy method but simulates a long-running method--such as downloading data--that
        /// should *not* be executed on the UI thread.
        /// </summary>
        private void DownloadData()
        {
            const int numFilesToDownload = 200;
            const int maxSleeptimeInMS = 100; //in milliseconds
            Random random = new Random();

            for (int i = 0; i < numFilesToDownload && !_cancelDownloadClicked; i++)
            {
                // This simulates the download
                Thread.Sleep(random.Next(maxSleeptimeInMS)); 

                float fractionCompleted = (float)(i + 1) / numFilesToDownload;
                SetProgressBar(fractionCompleted);
            }

            ResetUIAndStateTrackingVariables();
        }

        /// <summary>
        /// Resets the UI and state tracking variables. If this method is called from a non-UI thread, it executes
        /// asynchronously by calling BeginInvoke.
        /// See design pattern: https://msdn.microsoft.com/en-us/library/ms171728(v=vs.100).aspx#Anchor_0
        /// </summary>
        private void ResetUIAndStateTrackingVariables()
        {
            // InvokeRequired returns true if the caller must use Invoke or BeginInvoke when making UI related method
            // because the caller is on a different thread than the UI thread (i.e., the thread that created
            // the UI object)
            if (this.InvokeRequired)
            {
                // The MethodInvoker delegate is a special delegate that can execute any method
                // that is declared void and takes no parameters. Really just a convenience delegate
                // so we don't have to make a whole bunch of delegates for methods with the common void
                // and no parameter signature.
                this.BeginInvoke(new MethodInvoker(ResetUIAndStateTrackingVariables));
            }
            else
            {
                // Reset UI and state tracking variables
                _buttonStartDownload.Enabled = true;
                _buttonCancel.Enabled = false;
                _cancelDownloadClicked = false;
            }
        }

        /// <summary>
        /// Sets the progress bar to the current fraction. If this method is called from a non-UI thread, it executes
        /// asynchronously by calling BeginInvoke.
        /// See design pattern: https://msdn.microsoft.com/en-us/library/ms171728(v=vs.100).aspx#Anchor_0
        /// </summary>
        /// <param name="fraction">Fraction of progress bar to fill in</param>
        private void SetProgressBar(float fraction)
        {
            // InvokeRequired returns true if the caller must use Invoke or BeginInvoke when making UI related method
            // because the caller is on a different thread than the UI thread (i.e., the thread that created
            // the UI object)
            if (this.InvokeRequired &&
                _threadImplementation != ThreadImplementation.DoWorkInSeparateThreadButIncorrectly)
            {
                // Could call Invoke() or BeginInvoke() here. Both functions execute the specified
                // function (called delegate in C# language) on the thread that owns the control (in other
                // words, the UI thread)
                //
                // Both Invoke() and BeginInvoke() allow for parameter passing
                // See: https://msdn.microsoft.com/en-us/library/a06c0dc2(v=vs.110).aspx                 
                this.BeginInvoke(new SetProgressBarDelegate(SetProgressBar), fraction);
            }
            else
            {
                _progressBar.Value = _progressBar.Minimum + (int)((_progressBar.Maximum - _progressBar.Minimum) * fraction);
            }
        }

    }
}
