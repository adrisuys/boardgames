package be.adrisuys.myapplication.model;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import be.adrisuys.myapplication.presenter.MainPresenter;
import be.adrisuys.myapplication.view.MainActivity;

public class DBManager {

    private final Gson gson;
    private RequestQueue requestQueue;
    private MainActivity activity;
    private MainPresenter presenter;

    public DBManager(MainActivity activity, MainPresenter presenter){
        this.activity = activity;
        this.presenter = presenter;
        requestQueue = Volley.newRequestQueue(activity);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
    }

    private String getUrl(String search){
        return "https://www.boardgameatlas.com/api/search?name=" + search + "&client_id=MLymII3VXu";
    }

    public void makeRequest(String search){
        String url = getUrl(search);
        System.out.println(url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERROR " + error);
                    }
                });
        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    private void handleResponse(JSONObject response) {
        try {
            JSONArray jsonGames = (JSONArray) response.get("games");
            List<Game> games = new ArrayList<>();
            for (int i = 0; i < jsonGames.length(); i++){
                JSONObject jsonGame = (JSONObject) jsonGames.get(i);
                Game game = generateGame(jsonGame);
                games.add(game);
            }
            presenter.onGameListRecovered(games);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Game generateGame(JSONObject jsonGame) throws JSONException {
        String name = jsonGame.getString("name");
        int yearPublished = jsonGame.optInt("year_published", 0);
        int minPlayers = jsonGame.optInt("min_players", 0);
        int maxPlayers = jsonGame.optInt("max_players", 0);
        int minPlaytime = jsonGame.optInt("min_playtime", 0);
        int maxPlaytime = jsonGame.optInt("max_playtime", 0);
        String description = jsonGame.optString("description", "");
        int numUserRatings = jsonGame.getInt("num_user_ratings");
        double averageUserRatings = jsonGame.optDouble("average_user_rating", 0);
        String imgUrl = jsonGame.optString("image_url", "");
        return new Game(name, yearPublished, minPlayers, maxPlayers, minPlaytime, maxPlaytime, description, numUserRatings, averageUserRatings, imgUrl);
    }

}
