package com.testing.aestroidneostats;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.testing.aestroidneostats.pojo.DataPojo;
import com.testing.aestroidneostats.pojo.GraphPojo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SERVER_TIME_OUT = 30000;
    private LinearLayout showAestroidDetailLayout;
    private LinearLayout startDateLayout;
    private LinearLayout endDateLayout;
    private TextView startDateText;
    private TextView endDateText;
    private Button submitButton;
    private TextView fastestIdText;
    private TextView fastestSpeedText;
    private TextView closestIdText;
    private TextView closestSpeedText;
    private TextView averageSizeText;
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;
    private int mYear, mMonth, mDay;
    private ArrayList<GraphPojo> graphObjectList;
    private double totalAestroidLength = 0.0;
    private double averageAestroidLenght = 0.0;
    private String startDate;
    private String endDate;
    private int elementCount;
    private DataPojo dataPojo1;
    private DataPojo dataPojo2;
    private LineChart mpLineChart;
    private ArrayList<String> dateArrayList;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initInstance();
        setListener();
    }

    private void initView() {

        showAestroidDetailLayout = findViewById(R.id.showDetailLayout);
        startDateLayout = findViewById(R.id.setStartDateBtn);
        endDateLayout = findViewById(R.id.setEndDateBtn);
        startDateText = findViewById(R.id.startDateTextView);
        endDateText = findViewById(R.id.endDateTextView);
        submitButton = findViewById(R.id.submitBtn);
        fastestIdText = findViewById(R.id.fastestAestroidId);
        fastestSpeedText = findViewById(R.id.fastestAestroidSpeed);
        closestIdText = findViewById(R.id.closestAestroidId);
        closestSpeedText = findViewById(R.id.closestAestroidSpeed);
        averageSizeText = findViewById(R.id.averageSize);
        mpLineChart = findViewById(R.id.chart);
    }

    private void initInstance() {

        Calendar calendarStart = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMMM yyyy");
         currentDate = sdfDate.format(calendarStart.getTime());
        startDateText.setText(currentDate);
        endDateText.setText(currentDate);

        //for system navigation bar color #E58221
        getWindow().setNavigationBarColor(Color.parseColor("#ff8256"));
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("loading.....");
        progressDialog.setCanceledOnTouchOutside(false);
        builder = new AlertDialog.Builder(MainActivity.this);

        graphObjectList = new ArrayList<>();
        dateArrayList = new ArrayList<>();


    }

    private void setListener() {

        startDateLayout.setOnClickListener(this);
        endDateLayout.setOnClickListener(this);
        submitButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Integer id = v.getId();
        switch (id) {
            case R.id.setStartDateBtn: {
                getDateByDatePicker(startDateText);
                break;

            }

            case R.id.setEndDateBtn: {
                getDateByDatePicker(endDateText);
                break;

            }

            case R.id.submitBtn: {
                if (isValid()) {
                    hitApi();
                }

                break;

            }
        }
    }

    /* This method check validation for start date and end date */
    private boolean isValid() {

        if (TextUtils.isEmpty(startDateText.getText())) {
            Toast.makeText(this, "Enter Start Date ", Toast.LENGTH_LONG).show();
            return false;

        } else if (TextUtils.isEmpty(endDateText.getText())) {
            Toast.makeText(this, "Enter End Date ", Toast.LENGTH_LONG).show();
            return false;

        } else {
            return true;
        }

    }

    /* This method for get date from date picker */
    private void getDateByDatePicker(final TextView textView) {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.set(Calendar.YEAR, year);
                        calendarStart.set(Calendar.MONTH, monthOfYear);
                        calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String date = sdf.format(calendarStart.getTime());
                        if (textView.equals(endDateText)) {

                            long diff = calculateDays(startDateText.getText().toString(), date);
                            if (diff > 8) {
                                endDateText.setHint(currentDate);
                                Toast.makeText(MainActivity.this, "Feed date limit is only 7 Days", Toast.LENGTH_LONG).show();

                            } else if (diff < 0) {
                                Toast.makeText(MainActivity.this, "End date should be after start date ", Toast.LENGTH_LONG).show();
                                endDateText.setHint(currentDate);

                            } else {
                                textView.setText(date);
                            }

                        }else if (textView.equals(startDateText)){

                            endDateText.setText(date);
                            textView.setText(date);

                        } else {
                            textView.setText(date);
                        }

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    /* This method is used to hit api for get details of aestroid */
    private void hitApi() {

        graphObjectList.clear();
        dataPojo1 = new DataPojo(" ", 0.0, 0.0);
        dataPojo2 = new DataPojo(" ", 0.0, 0.0);
        averageAestroidLenght = 0.0;
        totalAestroidLength = 0.0;
        dateArrayList.clear();

        progressDialog.show();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

        startDate = dateFormatter(startDateText.getText().toString(), sdf, format);
        endDate = dateFormatter(endDateText.getText().toString(), sdf, format);

        final String AESTROID_API = "https://api.nasa.gov/neo/rest/v1/feed?start_date=" + startDate + "&end_date=" + endDate + "&api_key=Ks2sRwIMvhh1r6bNYQSdUP7OR42zyioL4WP0eup4";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AESTROID_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    progressDialog.dismiss();
                    submitButton.setEnabled(true);

                    JSONObject jsonObject = new JSONObject(response);
                    elementCount = jsonObject.getInt("element_count");
                    JSONObject near_earth_objects = jsonObject.getJSONObject("near_earth_objects");

                    dateArrayList = funForSetDate();
                    for (int i = 0; i < near_earth_objects.length(); i++) {

                        JSONArray dateArray = near_earth_objects.getJSONArray(dateArrayList.get(i));
                        graphObjectList.add(new GraphPojo(dateArrayList.get(i), dateArray.length()));

                        drawLineChart();

                        for (int j = 0; j < dateArray.length(); j++) {
                            String id = dateArray.getJSONObject(j).getString("id");
                            JSONObject estimated_diameter = dateArray.getJSONObject(j).getJSONObject("estimated_diameter");
                            JSONObject kilometers = estimated_diameter.getJSONObject("kilometers");

                            double diameterMin = kilometers.getDouble("estimated_diameter_min");
                            double diameterMax = kilometers.getDouble("estimated_diameter_max");

                            double average = (diameterMin + diameterMax) / 2;

                            totalAestroidLength = totalAestroidLength + average;

                            JSONArray close_approach_data = dateArray.getJSONObject(j).getJSONArray("close_approach_data");
                            JSONObject relative_velocity = close_approach_data.getJSONObject(0).getJSONObject("relative_velocity");

                            double kilometers_per_hour = relative_velocity.getDouble("kilometers_per_hour");
                            if (kilometers_per_hour > dataPojo1.getSpeed()) {
                                dataPojo1.setSpeed(kilometers_per_hour);
                                dataPojo1.setId(id);
                            }

                            fastestIdText.setText(dataPojo1.getId());
                            fastestSpeedText.setText(dataPojo1.getSpeed() + " km/h");

                            JSONObject miss_distance = close_approach_data.getJSONObject(0).getJSONObject("miss_distance");

                            DecimalFormat df = new DecimalFormat("#.######");
                            double closestDistance_current = miss_distance.getDouble("kilometers");
                            if (dataPojo2.getClosestDistance() == 0.0 || closestDistance_current < dataPojo2.getClosestDistance()) {
                                dataPojo2.setClosestDistance(Double.parseDouble(df.format(closestDistance_current)));
                                dataPojo2.setId(id);
                            }
                            closestIdText.setText(dataPojo2.getId());
                            closestSpeedText.setText(dataPojo2.getClosestDistance() + " km");

                        }

                    }

                    /*
                     get average aestroid length
                     */
                    if (elementCount > 0) {
                        averageAestroidLenght = totalAestroidLength / elementCount;
                        averageSizeText.setText(averageAestroidLenght + " km");
                    }
                    showAestroidDetailLayout.setVisibility(View.VISIBLE);


                } catch (Exception e) {
                    showAlertDialog("Volley Exception", e.toString());
                    progressDialog.dismiss();
                    submitButton.setEnabled(true);
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showAlertDialog("Error", error.toString());
                submitButton.setEnabled(true);
                progressDialog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(SERVER_TIME_OUT, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        Volley.newRequestQueue(MainActivity.this).add(stringRequest);


    }

    /* This method is used for draw line chart for aestroid */
    private void drawLineChart() {


        LineDataSet lineDataset1 = new LineDataSet(dataValue(), "No of Asteroids");
        LineData lineData = new LineData(lineDataset1);

        if (mpLineChart.getLineData() != null) {
            mpLineChart.getLineData().clearValues();
        }

        XAxis xAxis = mpLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8);

        // for hide x asix grip
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new MyAxisValueFormatter());

        // for hide right y axis line
        YAxis rightAxis = mpLineChart.getAxisRight();
        rightAxis.setEnabled(false);

        //for set width and text size of line
        lineDataset1.setColor(Color.parseColor("#753391"));
        lineDataset1.setLineWidth(2f);
        lineDataset1.setValueTextSize(12f);

        // for hide x axis description
        Description description = new Description();
        description.setText("Date");
        description.setTextSize(12);
        description.setTextColor(Color.parseColor("#753391"));
        mpLineChart.setDescription(description);

        mpLineChart.setData(lineData);
        mpLineChart.setBorderColor(Color.RED);
        mpLineChart.invalidate();


    }

    /* This method is used to find the no of days between the given dates */
    public static long calculateDays(String startDate, String endDate) {
        try {
            Date currentDate = ConvertDate(startDate);
            Date laterDate = ConvertDate(endDate);

            return (laterDate.getTime() - currentDate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }


    }

    /* This method is used for convert for convert string to date */
    public static Date ConvertDate(String dateString) throws ParseException {

        DateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        Date date = format.parse(dateString);
        return date;

    }

    /* This method is used for set y axis value in line chart */
    public ArrayList<Entry> dataValue() {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        for (int i = 0; i < graphObjectList.size(); i++) {
            dataVals.add(new Entry(i, graphObjectList.get(i).getNoOfAstroid()));
        }

        return dataVals;
    }

    /* This method is used for convert string to date and again date to string */
    public String dateFormatter(String dateString, SimpleDateFormat sdf, DateFormat dateFormat) {

        try {
            Date currentDate = dateFormat.parse(dateString);
            String date = sdf.format(currentDate);

            return date;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return " ";
    }

    /* This method is used for show alert dialog  */
    public void showAlertDialog(String title, String message) {
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                dialog.dismiss();
            }
        });

        builder.show();
    }

    public ArrayList<String> funForSetDate(){

        int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
        String nextCurrentDate;

        ArrayList<String> dateTypeList = new ArrayList<>();
        String startDate = startDateText.getText().toString();
        String endDate = endDateText.getText().toString();
        long diff = calculateDays(startDate,endDate);

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

        try {

            for (int i = 0; i <= diff; i++){

                Date dateSelectedFrom = sdf1.parse(startDate);
                nextCurrentDate = sdf2.format(dateSelectedFrom.getTime() + (MILLIS_IN_DAY) * i);

                dateTypeList.add(nextCurrentDate);

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateTypeList;
    }


    private class MyAxisValueFormatter extends ValueFormatter {

        @Override
        public String getAxisLabel(float value, AxisBase axis) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YY");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            axis.setLabelCount(graphObjectList.size(), true);
            for (int i = 0; i < graphObjectList.size(); i++) {
                if (i == value) {
                    String date1 = graphObjectList.get(i).getDate();

                    String date = dateFormatter(date1, sdf, dateFormat);
                    return date;
                }
            }

            return " ";
        }
    }


}