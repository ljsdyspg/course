package com.example.kcb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button button_testing = (Button) findViewById(R.id.button_test);
        button_testing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseName = "Chinese";
                String teacher = "YW";
                String classRoom = "Bs03";
                String day = "1";
                String start = "1";
                String end = "2";



              //  course course = new course(courseName, teacher, classRoom, Integer.valueOf(day), Integer.valueOf(start), Integer.valueOf(end));
                ArrayList<Course> courses =  new ArrayList<Course>();
                Course course1 = new Course("模式识别","怀念","3区 1-605",1,3,5);
                Course course2 = new Course("电子商务与电子政务","陈艳娇","3区 1-201",1,6,7);
                Course course3 = new Course("系统级程序设计","韩波","3区 605",2,3,5);
                Course course4 = new Course("软件体系结构","刘浩文","3区 1-631",2,6,8);
                courses.add(course1);
                courses.add(course2);
                courses.add(course3);
                courses.add(course4);
                Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                //传递实例
                //intent.putExtra("course", course);
                intent.putExtra("courses",(Serializable)courses);
                setResult(1, intent);
                finish();
                //startActivity(intent);
                }
        });
    }
}
