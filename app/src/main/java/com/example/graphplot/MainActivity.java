package com.example.graphplot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    GraphView graphView;
    EditText yvalue;
    Button insert;
    FirebaseDatabase firebaseDatabase;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
    LineGraphSeries  series;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphView = findViewById(R.id.graph);
        yvalue = findViewById(R.id.Enter_yvalue);
        insert = findViewById(R.id.submit);

    series = new LineGraphSeries();
    graphView.addSeries(series);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("chartTable");

        setListener();

        graphView.getGridLabelRenderer().setNumHorizontalLabels(10);

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX)
                {
                    return  simpleDateFormat.format(new Date((long) value));
                }
                else {
                return super.formatLabel(value, isValueX);}
            }
        });
    }

    private void setListener()
    {

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id= databaseReference.push().getKey();
                        long x =  new Date().getTime();
                        int y = Integer.parseInt(yvalue.getText().toString());
                        pointValue pointVal = new pointValue(x,y);
                        databaseReference.child(id).setValue(pointVal);




            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataPoint[] dt = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                int index=0;

                for (DataSnapshot mydatasnapshot : dataSnapshot.getChildren())

                {
                    pointValue pointvalu = mydatasnapshot.getValue(pointValue.class);
                    dt[index] = new DataPoint(pointvalu.getxValue(),pointvalu.getyValue());
                    index++;
                }

                series.resetData(dt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
