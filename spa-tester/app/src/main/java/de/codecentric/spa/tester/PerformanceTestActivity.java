package de.codecentric.spa.tester;

import android.app.Activity;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.codecentric.spa.EntityWrapper;
import de.codecentric.spa.ctx.PersistenceContext;
import de.codecentric.spa.tester.R;
import de.codecentric.spa.tester.entity.City;

public class PerformanceTestActivity extends Activity {

    private Button executeButton;
    private TextView startTimestampText;
    private TextView endTimestampText;
    private TextView differenceValueText;
    private TextView deletionTimeText;

    private EntityWrapper wrapper;

    private List<City> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_test);

        executeButton = (Button) findViewById(R.id.executePerformanceTestBtn);
        startTimestampText = (TextView) findViewById(R.id.startTimestamp);
        endTimestampText = (TextView) findViewById(R.id.endTimestamp);
        differenceValueText = (TextView) findViewById(R.id.differenceValue);
        deletionTimeText = (TextView) findViewById(R.id.deletionTime);

        wrapper = PersistenceContext.getInstance().getEntityWrapper();
    }

    @Override
    protected void onStart() {
        super.onStart();

        prepareDataStructure();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss:SSS");

        executeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date start = new Date();
                startTimestampText.setText(simpleDateFormat.format(start));

                wrapper.batchInsert(data, City.class);

                Date end = new Date();
                endTimestampText.setText(simpleDateFormat.format(end));

                long difference = end.getTime() - start.getTime();
                differenceValueText.setText("Time to insert: " + difference + " ms");

                Date deleteStart = new Date();
                wrapper.deleteBy("id <> ?", new String[]{"0"}, City.class);
                Date deleteEnd = new Date();
                deletionTimeText.setText("Time to delete: " + (deleteEnd.getTime() - deleteStart.getTime()) + " ms");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        data = null;
    }

    private void prepareDataStructure() {
        int dataSize = 2000;
        data = new ArrayList<City>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            data.add(new City("city" + i, i * 50000));
        }
    }

}
