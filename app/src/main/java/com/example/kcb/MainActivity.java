package com.example.kcb;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //星期几，使用相对布局
    private RelativeLayout day;

    //SQLite Helper类
    private DatabaseHelper databaseHelper = new DatabaseHelper
            (this, "database.db", null, 1);

    //最少课程数
    int currentcoursesNumber = 0;
    //最大课程数
    int maxcoursesNumber = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//生命周期方法
        setContentView(R.layout.activity_main);//设置要使用的布局管理器

        //工具条
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createLeftView();

        //从数据库读取数据
        loadData();
    }

    //从数据库加载数据
    private void loadData() {
        ArrayList<Course> coursesList = new ArrayList<>(); //课程列表
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();//从helper中获得数据库
        //游标，表示每一行的集合
        Cursor cursor = sqLiteDatabase.rawQuery("select * from courses", null);
        if (cursor.moveToFirst()) {
            do {
                coursesList.add(new Course(
                        cursor.getString(cursor.getColumnIndex("course_name")),
                        cursor.getString(cursor.getColumnIndex("teacher_name")),
                        cursor.getString(cursor.getColumnIndex("class_room")),
                        cursor.getInt(cursor.getColumnIndex("day")),
                        cursor.getInt(cursor.getColumnIndex("class_start")),
                        cursor.getInt(cursor.getColumnIndex("class_end"))));
            } while(cursor.moveToNext());
        }
        cursor.close();

        //使用从数据库读取出来的课程信息来加载课程表视图
        for (Course course : coursesList) {
            //createLeftView(course);//课程节数视图
            createcourseView(course);//课程视图
        }
    }

    //保存数据到数据库  1.打开数据库2.执行SQL语句
    private void saveData(Course course) {
        //当数据库不可写入时，getReadableDatabase()以只读的方式打开数据库，而getWritableDatabase()会出现异常
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        //执行SQL语句
        sqLiteDatabase.execSQL
                ("insert into courses(course_name, teacher_name, class_room, day, class_start, class_end) " + "values(?, ?, ?, ?, ?, ?)",
                new String[] {course.getCourseName(),
                        course.getTeacher(),
                        course.getClassRoom(),
                        course.getDay()+"",
                        course.getStart()+"",
                        course.getEnd()+""}
                );
    }

    //创建课程节数视图
    private void createLeftView() {
        for (int i = 0;i<maxcoursesNumber;i++){
            View view = LayoutInflater.from(this).inflate(R.layout.left_view,null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,190);
            view.setLayoutParams(params);
            TextView textView = view.findViewById(R.id.class_number_text);
            textView.setText(String.valueOf(++currentcoursesNumber));
            LinearLayout leftViewLayout = findViewById(R.id.left_view_layout);
            leftViewLayout.addView(view);
        }
        /*int len = course.getEnd();//获取所有课程最后一节课是多少节课
        if (len > maxcoursesNumber) {
            for (int i = 0; i < len-maxcoursesNumber; i++) {
                //LayoutInflater为布局服务，LayoutInflater.from.inflate()加载布局
                View view = LayoutInflater.from(this).inflate(R.layout.left_view, null);
                //LayoutParams设置相关的属性，宽和高
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(110,300);//180
                //应用上一行所设置的属性
                view.setLayoutParams(params);
                //设置TextView的属性值，从0开始，一直到最大结课时间
                TextView text = view.findViewById(R.id.class_number_text);
                text.setText(String.valueOf(++currentcoursesNumber));

                LinearLayout leftViewLayout = findViewById(R.id.left_view_layout);
                //把当前的视图加到线性布局中去
                leftViewLayout.addView(view);
            }
        }*/
        //maxcoursesNumber = len;
    }

    //创建课程视图
    private void createcourseView(final Course course) {
        int height = 190;
        int getDay = course.getDay();
        if ((getDay < 1 || getDay > 7) || course.getStart() > course.getEnd())
            //如果输入错误，给出消息提示
            Toast.makeText(this, "星期几没写对,或课程结束时间比开始时间还早~~", Toast.LENGTH_LONG).show();
        else {
            switch (getDay) {
                case 1: day = findViewById(R.id.monday); break;
                case 2: day = findViewById(R.id.tuesday); break;
                case 3: day = findViewById(R.id.wednesday); break;
                case 4: day = findViewById(R.id.thursday); break;
                case 5: day = findViewById(R.id.friday); break;
                case 6: day = findViewById(R.id.saturday); break;
                case 7: day = findViewById(R.id.weekday); break;
            }
            //每一个课程都是一个course_card
            final View v = LayoutInflater.from(this).inflate(R.layout.course_card, null); //加载单个课程布局
            v.setY(height * (course.getStart())-1); //设置开始高度,即第几节课开始,比如第一节课就从0开始
            //给课程布局设置参数，宽
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    //宽适应原布局单元的大小，布局高度为（占的课时*每个课时占的高度）
                    (ViewGroup.LayoutParams.MATCH_PARENT,(course.getEnd()-course.getStart()+1)*height - 8); //设置布局高度,即跨多少节课
            v.setLayoutParams(params);//属性绑定
            TextView text = v.findViewById(R.id.text_view);
            text.setText(course.getCourseName() + "\n" + course.getTeacher() + "\n" +"@"+ course.getClassRoom()); //显示课程名
            day.addView(v);
            //长按删除课程
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setVisibility(View.GONE);//先隐藏
                    day.removeView(v);//再移除课程视图
                    SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
                    sqLiteDatabase.execSQL("delete from courses where course_name = ?", new String[] {course.getCourseName()});
                    return true;
                }
            });
            v.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Toast.makeText(MainActivity.this,"按久一点就可以删了",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //获取创建课表中的course实例
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1 && data != null) {
            ArrayList<Course> courses = (ArrayList<Course>)data.getSerializableExtra("courses");
            for(Course course : courses){
            //创建课程表左边视图(节数)
            //createLeftView(course);
            //创建课程表视图
            createcourseView(course);
            //存储数据到数据库
            saveData(course);}
            //createcourseView(course_single);
            //saveData(course_single);
        }else if(requestCode == 0 && resultCode == 0 && data != null){
            Course course_single = (Course) data.getSerializableExtra("courses");
            createcourseView(course_single);
            saveData(course_single);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_courses:
                Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.menu_about:
                Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                startActivityForResult(intent1, 1);
                break;
        }
        return true;
    }
}