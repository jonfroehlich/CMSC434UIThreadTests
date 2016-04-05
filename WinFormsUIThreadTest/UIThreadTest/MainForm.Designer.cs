namespace UIThreadTest
{
    
    partial class MainForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this._progressBar = new System.Windows.Forms.ProgressBar();
            this._buttonStartDownload = new System.Windows.Forms.Button();
            this._buttonCancel = new System.Windows.Forms.Button();
            this._labelDownloadProgress = new System.Windows.Forms.Label();
            this._comboBoxThreadImplementation = new System.Windows.Forms.ComboBox();
            this.SuspendLayout();
            // 
            // _progressBar
            // 
            this._progressBar.Location = new System.Drawing.Point(32, 41);
            this._progressBar.Name = "_progressBar";
            this._progressBar.Size = new System.Drawing.Size(457, 23);
            this._progressBar.Step = 1;
            this._progressBar.TabIndex = 0;
            // 
            // _buttonStartDownload
            // 
            this._buttonStartDownload.Location = new System.Drawing.Point(302, 84);
            this._buttonStartDownload.Name = "_buttonStartDownload";
            this._buttonStartDownload.Size = new System.Drawing.Size(100, 23);
            this._buttonStartDownload.TabIndex = 1;
            this._buttonStartDownload.Text = "Start Download";
            this._buttonStartDownload.UseVisualStyleBackColor = true;
            this._buttonStartDownload.Click += new System.EventHandler(this.OnButtonStartDownloadClick);
            // 
            // _buttonCancel
            // 
            this._buttonCancel.Enabled = false;
            this._buttonCancel.Location = new System.Drawing.Point(414, 84);
            this._buttonCancel.Name = "_buttonCancel";
            this._buttonCancel.Size = new System.Drawing.Size(75, 23);
            this._buttonCancel.TabIndex = 2;
            this._buttonCancel.Text = "Cancel";
            this._buttonCancel.UseVisualStyleBackColor = true;
            this._buttonCancel.Click += new System.EventHandler(this.OnButtonCancelClick);
            // 
            // _labelDownloadProgress
            // 
            this._labelDownloadProgress.AutoSize = true;
            this._labelDownloadProgress.Location = new System.Drawing.Point(29, 22);
            this._labelDownloadProgress.Name = "_labelDownloadProgress";
            this._labelDownloadProgress.Size = new System.Drawing.Size(102, 13);
            this._labelDownloadProgress.TabIndex = 3;
            this._labelDownloadProgress.Text = "Download Progress:";
            // 
            // _comboBoxThreadImplementation
            // 
            this._comboBoxThreadImplementation.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this._comboBoxThreadImplementation.FormattingEnabled = true;
            this._comboBoxThreadImplementation.Location = new System.Drawing.Point(32, 86);
            this._comboBoxThreadImplementation.Name = "_comboBoxThreadImplementation";
            this._comboBoxThreadImplementation.Size = new System.Drawing.Size(224, 21);
            this._comboBoxThreadImplementation.TabIndex = 4;
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(523, 136);
            this.Controls.Add(this._comboBoxThreadImplementation);
            this.Controls.Add(this._labelDownloadProgress);
            this.Controls.Add(this._buttonCancel);
            this.Controls.Add(this._buttonStartDownload);
            this.Controls.Add(this._progressBar);
            this.Name = "MainForm";
            this.Text = "MainForm";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ProgressBar _progressBar;
        private System.Windows.Forms.Button _buttonStartDownload;
        private System.Windows.Forms.Button _buttonCancel;
        private System.Windows.Forms.Label _labelDownloadProgress;
        private System.Windows.Forms.ComboBox _comboBoxThreadImplementation;
    }
}

