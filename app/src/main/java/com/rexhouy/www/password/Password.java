package com.rexhouy.www.password;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by rexhouy on 8/16/16.
 */
public class Password implements Serializable {

    private String url;
    private String plain;
    private String comment;

    public static List<Password> fromPreferences(SharedPreferences p) {
        Map ps = p.getAll();
        List<Password> passwords = new ArrayList<>(ps.keySet().size());
        for (Iterator<String> itor = ps.keySet().iterator(); itor.hasNext();) {
            String key = itor.next();
            JSONObject jObj = null;
            try {
                jObj = new JSONObject(p.getString(key, "{}"));
                passwords.add(new Password(jObj.getString("url"),
                        jObj.getString("plain"),
                        jObj.isNull("comment") ? "" : jObj.getString("comment")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return passwords;
    }

    public static List<Password> fromText(String text) {
        JSONArray array = null;
        try {
            array = new JSONArray(text);
            List<Password> passwords = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject jObj = (JSONObject)array.get(i);
                passwords.add(new Password(jObj.getString("url"),
                        jObj.getString("plain"),
                        jObj.isNull("comment") ? "" : jObj.getString("comment")));
            }
            return passwords;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void toPreferences(SharedPreferences p, List<Password> passwords) {
        SharedPreferences.Editor editor = p.edit();
        for (Password pwd : passwords) {
            try {
                editor.putString(pwd.getUrl(), pwd.toJSON());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.commit();
    }

    public Password(String url, String plain, String comment) {
        this.url = url;
        this.plain = plain;
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlain() { return plain; }

    public void setPlain(String plain) {
        this.plain = plain;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String toJSON() throws JSONException {
        JSONObject jObj = new JSONObject();
        jObj.put("url", this.getUrl());
        jObj.put("plain", this.getPlain());
        jObj.put("comment", this.getComment());
        return jObj.toString();
    }
}
