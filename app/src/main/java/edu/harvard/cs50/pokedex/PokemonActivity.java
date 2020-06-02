package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;

public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private String url;
    private RequestQueue requestQueue;

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

        load();

        buttonCatch = findViewById(R.id.catchButton);

        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);



    }

    public void load() {
        type1TextView.setText("");
        type2TextView.setText("");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    nameTextView.setText(response.getString("name"));
                    savedPokemon = response.getString("name");
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));

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


    private static final String SHARED_PREF = "Sharedprefs";
    private String savedPokemon;
    boolean catchPoke;
    private Button buttonCatch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;



    private static final String POKECATCH = "pokeIsCaught";
    private ArrayList<String> savedList = new ArrayList<>();

    public void toggleCatch (View view) {
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






