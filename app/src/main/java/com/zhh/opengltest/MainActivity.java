package com.zhh.opengltest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
        mBinding.glSurface.setEGLContextClientVersion(2);
//        FGLRender render = new FGLRender(mBinding.glSurface);
//        render.setShape(TriangleColorFull.class);
//        TriangleColorFull colorFull = null;
//        try {
//            Constructor constructor=TriangleColorFull.class.getDeclaredConstructor(View.class);
//            constructor.setAccessible(true);
//            colorFull= (TriangleColorFull) constructor.newInstance(mBinding.glSurface);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        mBinding.glSurface.setRenderer(new RichTriangleRender());
//        mBinding.glSurface.setRenderer(new SimpleShapeRender());
        mBinding.glSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.glSurface.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.glSurface.onPause();
    }
}