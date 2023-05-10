package com.example.vehicle_and_cargo_matching_system.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class EvaluationListView extends ListView{
    public EvaluationListView(Context context) {
        super(context);
    }

    public EvaluationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EvaluationListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EvaluationListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
