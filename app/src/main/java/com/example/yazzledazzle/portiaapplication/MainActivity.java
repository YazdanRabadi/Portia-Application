package com.example.yazzledazzle.portiaapplication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Main Activity
 * Shows the weather from 6 cities
 * NOTE: I would have used the API call that fetches info for
 * multiple cities at once however the API doesn't support
 * XML output for that particular method. I found this out
 * after the XML parsing code was done. Given more time I'd switch
 * to a JSON parser and use a single API call to make the app
 * more efficient.
 */
public class MainActivity extends Activity {

    //These are used to store the values
    //before they are output to the screen.
    //Needed these as the View cannot be referenced
    //from multiple threads at one time
    public static String t1="";
    public static String t2="";
    public static String t3="";
    public static String t4="";
    public static String t5="";
    public static String t6="";


    /**
     * This class handles the RESTful api call
     * Using openweathermap.com
     */
    private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //This is the API url
            String urlString=params[0]; // URL to call
            //Result is stored here
            InputStream in = null;

            // HTTP Get and stored
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (Exception e ) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }

            // Parse XML result
            XmlPullParserFactory pullParserFactory;
            try {
                pullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = pullParserFactory.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parseXML(parser);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Method done
            return"Done";

        }

        //This gets called whenever the thread is done
        protected void onPostExecute(String result) {

            //Check to see if all threads are done. This lets me do all the View editing at one time
            if(!t1.isEmpty() && !t2.isEmpty() && !t3.isEmpty() && !t4.isEmpty() && !t5.isEmpty() && !t6.isEmpty()){
                //Start dumping data to the screen
                TextView t = (TextView)findViewById(R.id.cityTemp1);
                t.setText(t1);

                t = (TextView)findViewById(R.id.cityTemp2);
                t.setText(t2);

                t = (TextView)findViewById(R.id.cityTemp3);
                t.setText(t3);

                t = (TextView)findViewById(R.id.cityTemp4);
                t.setText(t4);

                t = (TextView)findViewById(R.id.cityTemp5);
                t.setText(t5);

                t = (TextView)findViewById(R.id.cityTemp6);
                t.setText(t6);

            }

        }

        private void parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {
            //Stores the parsed results
            String cityName="";
            String cityTemp="";

            int eventType = parser.getEventType();
            //Starts looping through the tree
            while( eventType!= XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch(eventType)
                {
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        Log.d("XML",name);

                        if ( name.equals("city")) { //Check if the node is a city
                            cityName = parser.getAttributeValue(1); //get the name attirbute
                        }
                        else if (name.equals("temperature")) { //check if the node is temperature
                            cityTemp = parser.getAttributeValue(0); //get the temp attribute
                        }
                        break;
                    case XmlPullParser.END_TAG: //stop the loop when at the end
                        break;
                } // end switch

                eventType = parser.next();
            } // end while

            //Checks the result to see which city we got.
            if(cityName.equals("Ottawa")){
                t1=cityTemp + "C";
            }
            else if(cityName.equals("Toronto")){
                t2=cityTemp + "C";
            }
            else if(cityName.equals("Montreal")){
                t3=cityTemp + "C";
            }
            else if(cityName.equals("Calgary")){
                t4=cityTemp + "C";
            }
            else if(cityName.equals("Vancouver")){
                t5=cityTemp + "C";
            }
            else if(cityName.equals("Halifax")){
                t6=cityTemp + "C";
            }
        }


    } // end CallAPI


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Start the API call immediately when the app starts
        verifyEmail();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // This is the method that is called when the submit button is clicked
    public void verifyEmailFromButton(View view) {
        //Empty the text fields
        TextView t = (TextView)findViewById(R.id.cityTemp1);
        t.setText("-");

        t = (TextView)findViewById(R.id.cityTemp2);
        t.setText("-");

        t = (TextView)findViewById(R.id.cityTemp3);
        t.setText("-");

        t = (TextView)findViewById(R.id.cityTemp4);
        t.setText("-");

        t = (TextView)findViewById(R.id.cityTemp5);
        t.setText("-");

        t = (TextView)findViewById(R.id.cityTemp6);
        t.setText("-");

        //Clear the values from last time
        t1=t2=t3=t4=t5=t6="";
        //Make the API calls all over again
        verifyEmail();
    }

    public void verifyEmail() {
        //Going through the API call for 6 cities
        //NOTE: There is a call for multiple cities but it doesnt support XML mode
        //I discovered this after I had the rest of the app structured for XML so making
        //6 calls was faster than switching my XML parser to a JSON parser.

        //OTTAWA
        String urlString = "http://api.openweathermap.org/data/2.5/weather?id=6094817&APPID=60ee2157c2fbf18963156fbfa0286436&mode=xml&units=metric";
        new CallAPI().execute(urlString);
        //TORONTO
        urlString = "http://api.openweathermap.org/data/2.5/weather?id=6167865&APPID=60ee2157c2fbf18963156fbfa0286436&mode=xml&units=metric";
        new CallAPI().execute(urlString);
        //MONTREAL
        urlString = "http://api.openweathermap.org/data/2.5/weather?id=6077243&APPID=60ee2157c2fbf18963156fbfa0286436&mode=xml&units=metric";
        new CallAPI().execute(urlString);
        //CALGARY
        urlString = "http://api.openweathermap.org/data/2.5/weather?id=5913490&APPID=60ee2157c2fbf18963156fbfa0286436&mode=xml&units=metric";
        new CallAPI().execute(urlString);
        //VANCOUVER
        urlString = "http://api.openweathermap.org/data/2.5/weather?id=6173331&APPID=60ee2157c2fbf18963156fbfa0286436&mode=xml&units=metric";
        new CallAPI().execute(urlString);
        //HALIFAX
        urlString = "http://api.openweathermap.org/data/2.5/weather?id=6324729&APPID=60ee2157c2fbf18963156fbfa0286436&mode=xml&units=metric";
        new CallAPI().execute(urlString);

    }
}
