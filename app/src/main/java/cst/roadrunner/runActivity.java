package cst.roadrunner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class runActivity extends Activity {

    Button distanceButton;
    EditText caloriesInput;
    EditText weightInput;
    TextView distanceView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        distanceButton = (Button)findViewById(R.id.getDistanceButton);
        caloriesInput  = (EditText)findViewById(R.id.calories);
        weightInput    = (EditText)findViewById(R.id.weight);
        distanceView   = (TextView)findViewById(R.id.distanceView);

        distanceButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        // Convert Editable to string
                        String caloriesString = caloriesInput.getText().toString();
                        String weightString = weightInput.getText().toString();

                        // Convert string to int
                        final int calories = Integer.parseInt(caloriesString);
                        final int weight = Integer.parseInt(weightString);

                        String distance = Double.toString(calculateDistance(calories, weight));
                        distanceView.setText(distance);
                    }
                });
    }

    /*
    The approximate formula for how long it takes to burn calories is M = C/(WA) where
    M is minutes,
    C is the number of calories ingested,
    W is your weight in pounds, and
    A is a number that corresponds to the intensity level of your activity.

    The value of A depends on the exercise or activity you do. For example, sleeping and
    watching TV have very low values, between 0.005 and 0.01. The activity values for walking
    are between 0.03 and 0.07, while the values for running are between 0.05 and 0.13 depending
    on your speed.
     */

    final double ACTIVITY_LEVEL = 0.09; // for moderate run

    public double calculateTime(int calories, int weight) {
        double minutes = calories / (weight * ACTIVITY_LEVEL);
        return minutes;
    }

    /*
    The average man jogs at a speed of 8.3 mph, or 100m in 27 seconds,
    while the average woman runs at 6.5 mph, covering 100m in 34 seconds.

    Average between man and woman = 7.4 mph = 0.123 miles/min
     */

    final double MILES_PER_MIN = 0.123;

    public double calculateDistance(int calories, int weight) {
        double distance = (calculateTime(calories, weight) * MILES_PER_MIN);
        return distance;
    }
}
