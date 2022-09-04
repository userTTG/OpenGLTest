package com.zhh.opengltest;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.zhh.opengltest.databinding.ActivityShapeListBinding;
import com.zhh.opengltest.gl2d.Texture2dRender;
import com.zhh.opengltest.shape.CircleRender;
import com.zhh.opengltest.shape.CubeRender;
import com.zhh.opengltest.shape.RectRender;
import com.zhh.opengltest.shape.RichTriangleRender;
import com.zhh.opengltest.shape.TriangleRender;

import java.util.ArrayList;
import java.util.List;

public class ShapeListActivity extends AppCompatActivity {

    ActivityShapeListBinding mBinding;
    ListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_shape_list);

        mListAdapter = new ListAdapter(createListData());
        mBinding.rvShapes.setAdapter(mListAdapter);
        mBinding.rvShapes.setLayoutManager(new LinearLayoutManager(this));
        mListAdapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
            ShapeItem shapeItem = (ShapeItem) baseQuickAdapter.getData().get(i);
            GLDemoActivity.startAction(ShapeListActivity.this,shapeItem.aClass);
        });
    }

    private List<ShapeItem> createListData(){
        List<ShapeItem> list = new ArrayList<>();
        list.add(new ShapeItem("三角形", TriangleRender.class));
        list.add(new ShapeItem("等腰三角形", RichTriangleRender.class));
        list.add(new ShapeItem("正方形", RectRender.class));
        list.add(new ShapeItem("圆形", CircleRender.class));
        list.add(new ShapeItem("立方体", CubeRender.class));
        list.add(new ShapeItem("图片", Texture2dRender.class));
        return list;
    }


    private static class ShapeItem{
        public ShapeItem(String name, Class<? extends GLSurfaceView.Renderer> aClass) {
            this.name = name;
            this.aClass = aClass;
        }

        private String name;
        private Class<? extends GLSurfaceView.Renderer> aClass;
    }

    private static class ListAdapter extends BaseQuickAdapter<ShapeItem, BaseViewHolder> {

        public ListAdapter(List<ShapeItem> data) {
            super(R.layout.item_shape_list,data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ShapeItem shapeItem) {
            baseViewHolder.setText(R.id.btn_name,shapeItem.name);
        }
    }
}