package com.ruenzuo.babel.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.models.Language;

import java.util.List;

/**
 * Created by ruenzuo on 23/08/14.
 */
public class LanguageAdapter extends ArrayAdapter<Language> {

    private static class LanguageViewHolder {
        public TextView vwTextName;
    }

    public LanguageAdapter(Context context, int resource, List<Language> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.language_row_layout, null);
            LanguageViewHolder viewHolder = new LanguageViewHolder();
            viewHolder.vwTextName = (TextView) convertView.findViewById(R.id.vwTextName);
            convertView.setTag(viewHolder);
        }
        LanguageViewHolder viewHolder = (LanguageViewHolder) convertView.getTag();
        Language language = getItem(position);
        viewHolder.vwTextName.setText(language.getName());
        return convertView;
    }

}
