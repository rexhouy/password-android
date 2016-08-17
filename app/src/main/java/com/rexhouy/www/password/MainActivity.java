package com.rexhouy.www.password;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    PasswordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initPasswords();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update) {
            uploadPreference();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        ListView list = (ListView)findViewById(R.id.password_list);
        adapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ListView list = (ListView)findViewById(R.id.password_list);
        adapter.filter(newText);
        return true;
    }

    public void view(View v) {
        Password password = adapter.getDataByPosition(getPosition(v));
        new AlertDialog.Builder(this)
                .setTitle(password.getUrl())
                .setMessage(password.getPlain())
                .show();
    }

    public void copy(View v) {
        // copy password to clipboard
        Password password = adapter.getDataByPosition(getPosition(v));
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(password.getUrl(), password.getPlain());
        clipboard.setPrimaryClip(clip);

        showPrompt("Copied to clipboard");
    }

    public void uploadPreference() {
        File file = new File(Environment.getExternalStorageDirectory(), "pwd");

        StringBuffer buffer = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        List<Password> passwords = Password.fromText(buffer.toString());
        Password.toPreferences(getPreferences(Context.MODE_PRIVATE), passwords);
        file.delete();
        showPrompt("Update succeed");
    }

    private void initPasswords() {
        ListView list = (ListView)findViewById(R.id.password_list);
        adapter = new PasswordAdapter(this, R.layout.password_item, getPreferences(Context.MODE_PRIVATE));
        list.setAdapter(adapter);

    }

    private int getPosition(View v) {
        View parentRow = (View) v.getParent();
        ListView listView = (ListView) parentRow.getParent();
        return listView.getPositionForView(parentRow);
    }

    private void showPrompt(String text) {
        TextView prompt = (TextView)findViewById(R.id.copy_prompt);
        prompt.setText(text);
        prompt.setAlpha(1.0f);
        prompt.animate().alpha(0.0f).setDuration(3000);

    }
}
