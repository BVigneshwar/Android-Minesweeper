package com.example.minisweeper;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button start_button, prev_grid_size_button, next_grid_size_button;
    TextView grid_size_selector, best_time_display;
    Spinner theme_selector;
    String grid_size_array[] = {"7 x 5", "10 x 7", "12 x 9", "13 x 11"};
    int size_selector_index = 0;
    long best_time = Long.MAX_VALUE;
    static int selected_theme = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        selected_theme = intent.getIntExtra("THEME", 0);
        ThemeChanger.onActivityCreateSetTheme(this, selected_theme);
        setContentView(R.layout.activity_main);

        start_button = (Button) findViewById(R.id.start_button);
        grid_size_selector = (TextView) findViewById(R.id.grid_size_selector);
        prev_grid_size_button = (Button) findViewById(R.id.prev_grid);
        next_grid_size_button = (Button) findViewById(R.id.next_grid);
        best_time_display = (TextView) findViewById(R.id.best_time);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        theme_selector = (Spinner) navigationView.getMenu().findItem(R.id.nav_theme).getActionView();

        start_button.setOnClickListener(this);
        updateBestTime();

        grid_size_selector.setOnTouchListener(new View.OnTouchListener() {
            float x1=0, y1=0, x2=0, y2=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        if(x1 < x2){
                            //left to right slide
                            if(size_selector_index > 0){
                                grid_size_selector.setText(grid_size_array[--size_selector_index]);
                                if(size_selector_index == 0){
                                    prev_grid_size_button.setVisibility(View.INVISIBLE);
                                }else{
                                    next_grid_size_button.setVisibility(View.VISIBLE);
                                }
                                updateBestTime();
                            }
                        }
                        if(x1 > x2){
                            //right to left slide
                            if(size_selector_index < grid_size_array.length -1){
                                grid_size_selector.setText(grid_size_array[++size_selector_index]);
                                if(size_selector_index == grid_size_array.length - 1){
                                    next_grid_size_button.setVisibility(View.INVISIBLE);
                                }else{
                                    prev_grid_size_button.setVisibility(View.VISIBLE);
                                }
                                updateBestTime();
                            }
                        }
                }
                return true;
            }
        });

        prev_grid_size_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(size_selector_index > 0){
                    grid_size_selector.setText(grid_size_array[--size_selector_index]);
                    if(size_selector_index == 0){
                        prev_grid_size_button.setVisibility(View.INVISIBLE);
                    }else{
                        next_grid_size_button.setVisibility(View.VISIBLE);
                    }
                    updateBestTime();
                }
            }
        });

        next_grid_size_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(size_selector_index < grid_size_array.length -1){
                    grid_size_selector.setText(grid_size_array[++size_selector_index]);
                    if(size_selector_index == grid_size_array.length - 1){
                        next_grid_size_button.setVisibility(View.INVISIBLE);
                    }else{
                        prev_grid_size_button.setVisibility(View.VISIBLE);
                    }
                    updateBestTime();
                }
            }
        });
        theme_selector.setSelection(selected_theme, false);
        theme_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(MainActivity.selected_theme != position){
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("THEME", position);
                    MainActivity.this.finish();
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        String str[] = grid_size_array[size_selector_index].split(" ");
        intent.putExtra("ROW_COUNT", Integer.parseInt(str[0]));
        intent.putExtra("COLUMN_COUNT", Integer.parseInt(str[2]));
        intent.putExtra("BEST_TIME", best_time);
        intent.putExtra("THEME", selected_theme);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateBestTime();
    }

    public void updateBestTime(){
        String str[] = grid_size_array[size_selector_index].split(" ");
        int row_num = Integer.parseInt(str[0]);
        int col_num = Integer.parseInt(str[2]);
        DatabaseHelper helper = new DatabaseHelper(this);
        Cursor cursor = helper.getRecord(row_num, col_num);
        if(cursor.moveToFirst()){
            best_time = cursor.getLong(0);
            int minutes = (int)(best_time/60);
            int seconds = (int)(best_time%60);
            best_time_display.setText(minutes+" : "+seconds);
        }else{
            best_time_display.setText("Good Luck !!!");
        }
    }


}