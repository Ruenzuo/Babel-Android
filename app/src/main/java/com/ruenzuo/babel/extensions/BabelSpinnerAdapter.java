package com.ruenzuo.babel.extensions;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by ruenzuo on 23/08/14.
 */
public class BabelSpinnerAdapter<T> extends ArrayAdapter<T> {

    public BabelSpinnerAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return setFontSize(super.getView(position, convertView, parent));
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return setFontSize(super.getView(position, convertView, parent));
    }

    private View setFontSize(View view) {
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }
        return view;
    }

}
