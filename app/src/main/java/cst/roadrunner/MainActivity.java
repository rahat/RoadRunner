package cst.roadrunner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "A8dGeDxlmuJcIER5pxDgPgchJm6J6QcQbIii9dGp", "ibn7etv5iaj6DWEPL3crLEPXAKqojpVvIoZc1NP0");
        ParseFacebookUtils.initialize(this);
    }

    public void startRunActivity(View view) {
        startActivity(new Intent(MainActivity.this, runActivity.class));
    }

    public void startSocialActivity(View view) {
        startActivity(new Intent(MainActivity.this, socialActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
