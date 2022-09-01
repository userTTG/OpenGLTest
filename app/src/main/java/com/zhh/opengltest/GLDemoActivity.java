package com.zhh.opengltest;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.zhh.opengltest.databinding.ActivityGlDemoBinding;

import java.lang.reflect.Constructor;

public class GLDemoActivity extends AppCompatActivity {

    private static final String INTENT_KEY = "intent_key";

    public static void startAction(Context context,Class<? extends GLSurfaceView.Renderer> clazz){
        Intent intent = new Intent(context,GLDemoActivity.class);
        intent.putExtra(INTENT_KEY,clazz);
        context.startActivity(intent);
    }

    ActivityGlDemoBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_gl_demo);
        mBinding.glSurface.setEGLContextClientVersion(2);

        Class<? extends GLSurfaceView.Renderer> clazz = (Class<? extends GLSurfaceView.Renderer>) getIntent().getSerializableExtra(INTENT_KEY);
        if(clazz != null){
            GLSurfaceView.Renderer renderer=null;
            try {
                Constructor<? extends GLSurfaceView.Renderer> constructor=clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                renderer= (GLSurfaceView.Renderer) constructor.newInstance();
                mBinding.glSurface.setRenderer(renderer);
                mBinding.glSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
////        FGLRender render = new FGLRender(mBinding.glSurface);
////        render.setShape(TriangleColorFull.class);
////        TriangleColorFull colorFull = null;
////        try {
////            Constructor constructor=TriangleColorFull.class.getDeclaredConstructor(View.class);
////            constructor.setAccessible(true);
////            colorFull= (TriangleColorFull) constructor.newInstance(mBinding.glSurface);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        mBinding.glSurface.setRenderer(new CircleRender());
////        mBinding.glSurface.setRenderer(new SimpleShapeRender());
//        mBinding.glSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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