package com.example.vehicle_and_cargo_matching_system.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class OrderListView extends ListView{
    public OrderListView(Context context) {
        super(context);
    }

    public OrderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OrderListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
