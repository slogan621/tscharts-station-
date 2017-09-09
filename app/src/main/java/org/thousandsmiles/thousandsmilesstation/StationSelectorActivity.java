/*
 * (C) Copyright Syd Logan 2017
 * (C) Copyright Thousand Smiles Foundation 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.thousandsmiles.thousandsmilesstation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class StationSelectorActivity extends AppCompatActivity {

    private Activity m_activity = this;
    private SessionSingleton m_sess = SessionSingleton.getInstance();
    private Context m_context;

    public void handleButtonPress(View v)
    {
        this.m_activity.finish();
    }

    public static final long DISCONNECT_TIMEOUT = 15000; // 5 min = 5 * 60 * 1000 ms

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {

        }
    };

    private void confirmClinicStationSelection(final JSONObject cs) {
        final int id;
        final int stationId;
        final String name;
        try {
            name = cs.getString("name");
            id = cs.getInt("id");
            stationId = cs.getInt("station");
        } catch (JSONException e) {
            Toast.makeText(StationSelectorActivity.this,"Unable to process this station. Please try again.",Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(String.format("Sign in to clinic station %s?", name));
                alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            m_sess.setActiveStationName(name);
                            m_sess.setActiveStationId(id);
                            m_sess.setActiveStationStationId(stationId);

                            Intent intent = new Intent(m_activity, StationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(StationSelectorActivity.this,"Please select another station.",Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            /*
            // Perform any required operation on disconnect
            Intent intent = new Intent(m_activity, LetterSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            */
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    private void LayoutClinicStationGrid() {
        TableLayout layout = (TableLayout) findViewById(R.id.namestablelayout);

        int numClinicStations = m_sess.getClinicStationCount();
        /*
        ArrayList<Volunteer> list = getIntent().getParcelableArrayListExtra("volunteers");

        Integer count = new Integer(0);
        TableRow row = null;
        for (Volunteer x : list) {
        */
        TableRow row = null;
        for (int count = 0; count < numClinicStations; count++) {
            boolean newRow = false;
            if ((count % 5) == 0) {
                newRow = true;
                row = new TableRow(getApplicationContext());
                row.setWeightSum((float)1.0);
                TableRow.LayoutParams parms = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                int leftMargin=10;
                int topMargin=2;
                int rightMargin=10;
                int bottomMargin=2;

                parms.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

                row.setLayoutParams(parms);
            }

            LinearLayout btnLO = new LinearLayout(this);

            LinearLayout.LayoutParams paramsLO = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            btnLO.setOrientation(LinearLayout.VERTICAL);

            //btnLO.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT));
            TableRow.LayoutParams parms = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);

            int leftMargin=10;
            int topMargin=2;
            int rightMargin=10;
            int bottomMargin=2;

            parms.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

            btnLO.setLayoutParams(parms);

            ImageButton button = new ImageButton(getApplicationContext());


            button.setImageDrawable(getResources().getDrawable(R.drawable.medical_selector));
            button.setBackgroundColor(getResources().getColor(R.color.lightGray));
            button.setTag(count);
            //button.setBackgroundColor(getResources().getColor(R.color.girlPink));

            //button.setTag(x);
            //button.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT));

            //button.setTextColor(getApplicationContext().getResources().getColor(R.color.black));


            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    JSONObject tag = (JSONObject) v.getTag();
                    confirmClinicStationSelection(tag);
                }
            });

            btnLO.addView(button);

            TextView txt = new TextView(getApplicationContext());

            JSONObject cs = m_sess.getClinicStationData(count);
            if (cs == null) {
                continue;
            }
            String name;
            try {
                name = cs.getString("name");
            } catch (JSONException e) {
                continue;
            }
            txt.setText(String.format("%s", name));
            button.setTag(cs);
            txt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            txt.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            btnLO.addView(txt);

            if (row != null) {
                row.addView(btnLO);
            }
            if (newRow == true) {
                layout.addView(row, new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }
        //HideyHelper h = new HideyHelper();
        //h.toggleHideyBar(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_station_selector);

        /*
        Button button = (Button) findViewById(R.id.patient_search_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            LayoutSearchResults();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            HideyHelper h = new HideyHelper();
            }

        });
        */
        m_context = getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //HideyHelper h = new HideyHelper();
        //h.toggleHideyBar(this);

        final ClinicREST clinicREST = new ClinicREST(m_context);

        final Object lock;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        lock = clinicREST.getClinicData(year, month, day);

        final Thread thread = new Thread() {
            public void run() {
            synchronized (lock) {
                // we loop here in case of race conditions or spurious interrupts
                while (true) {
                    try {
                        lock.wait();
                        break;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
            }

            SessionSingleton data = SessionSingleton.getInstance();
            int status = clinicREST.getStatus();
            if (status == 200) {
                if (m_sess.updateClinicStationData() == false) {
                    StationSelectorActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.error_unable_to_get_clinicstation_data, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    StationSelectorActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            LayoutClinicStationGrid();
                        }
                    });
                }
                /*
                m_task = new GetAndDisplayTask();
                m_task.setContext(getApplicationContext());
                m_task.execute();
                */
                return;
            } else if (status == 101) {
                StationSelectorActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_connect, Toast.LENGTH_LONG).show();
                    }
                });

            } else if (status == 400) {
                StationSelectorActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_internal_bad_request, Toast.LENGTH_LONG).show();
                    }
                });
            } else if (status == 404) {
                StationSelectorActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_clinic_not_found_date, Toast.LENGTH_LONG).show();
                    }
                });
            } else if (status == 500) {
                StationSelectorActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_internal_error, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                StationSelectorActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                    }
                });
            }
            }
        };
        thread.start();
    }
}
