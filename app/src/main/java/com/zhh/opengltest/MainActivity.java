package com.zhh.opengltest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import com.zhh.opengltest.databinding.ActivityMainBinding;

import java.lang.reflect.Constructor;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        mBinding.btnShape.setOnClickListener(v->{
            Intent intent = new Intent(this,ShapeListActivity.class);
            startActivity(intent);
        });
    }
}