package com.rexhouy.www.password;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rexhouy on 8/16/16.
 */
public class PasswordAdapter extends ArrayAdapter<Password> {

    private Password[] passwords;
    private List<Password> active;
    private SharedPreferences preferences;

    public PasswordAdapter(Context context, int resource, SharedPreferences preferences) {
        super(context, resource, R.id.url);
        this.preferences = preferences;
        load();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.password_item, parent, false);
        ((TextView)rowView.findViewById(R.id.url)).setText(active.get(position).getUrl());
        ((TextView)rowView.findViewById(R.id.comment)).setText(active.get(position).getComment());
        return rowView;
    }

    public void filter(String s) {
        if (passwords == null) {
            return;
        }
        clear();
        active.clear();
        int i = 0;
        for (Password p : passwords) {
            if (p.getUrl().contains(s)) {
                active.add(p);
                insert(p, i);
                i++;
            }
        }
    }

    public Password getDataByPosition(int position) {
        return active.get(position);
    }

    private void load() {
        this.active = Password.fromPreferences(this.preferences);
        this.passwords = active.toArray(new Password[this.active.size()]);
        filter("");
    }

}
