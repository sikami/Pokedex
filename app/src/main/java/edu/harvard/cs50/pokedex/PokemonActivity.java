package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private TextView pokeTexts;
    private String url;
    private String urlText;
    private int index;
    private RequestQueue requestQueue;
    private String urlImages;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        url = getIntent().getStringExtra("url");

        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);
        image = findViewById(R.id.imageView);
        buttonCatch = findViewById(R.id.catchButton);
        pokeTexts = findViewById(R.id.pokeText);

        load();
        pokeFlavorText();

        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

    }

    public void pokeFlavorText() {
        pokeTexts.setText("");
        Log.d("id", "url: " + url);

        String[] splitting = url.split("/");
        String index = splitting[6];
        urlText = "https://pokeapi.co/api/v2/pokemon-species/" + index;
        Log.d("id", "urlText: " + urlText);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, urlText, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray flavorsText = response.getJSONArray("flavor_text_entries");

                    JSONObject flavors = flavorsText.getJSONObject(0);
                    String flav = flavors.getString("flavor_text");
                    pokeTexts.setText(flav);
                }
                catch (JSONException e) {
                    Log.e("cs50", "cant download pokemontext");

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "error downloading text");
            }

    });
        requestQueue.add(req);

    }


    public void load() {
        type1TextView.setText("");
        type2TextView.setText("");
        image.setImageBitmap(null);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    nameTextView.setText(response.getString("name"));
                    savedPokemon = response.getString("name");
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));


                    urlImages = response.getJSONObject("sprites").getString("front_default");

                    DownloadSpriteTask downloadSpriteTask = new DownloadSpriteTask();
                    downloadSpriteTask.execute(urlImages);


                    JSONArray typeEntries = response.getJSONArray("types");
                    for (int i = 0; i < typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");

                        if (slot == 1) {
                            type1TextView.setText(type);
                        }
                        else if (slot == 2) {
                            type2TextView.setText(type);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error", error);
            }
        });

        requestQueue.add(request);

    }


    //for images


    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            } catch (IOException e) {
                Log.e("cs50", "Download sprite error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            image.setImageBitmap(bitmap);
        }
    }

    private static final String SHARED_PREF = "Sharedprefs";
    private String savedPokemon;
    boolean catchPoke;
    private Button buttonCatch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;



    private static final String POKECATCH = "pokeIsCaught";
    private Set<String> savedList = new HashSet<>();

    public void toggleCatch (View view) {
        sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);

            if (!catchPoke) {
                buttonCatch.setText("Release");
                Log.d("catch2", "catchpoke : " + catchPoke);
                savedList.add(savedPokemon);
                catchPoke = true;
            }
            else {
                buttonCatch.setText("Catch");
                savedList.remove(savedPokemon);
                Log.d("catch2", "catchpoke : " + catchPoke);
                catchPoke = false;
            }


        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(POKECATCH, savedPokemon);
        editor.apply();

    }


}






