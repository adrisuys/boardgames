package be.adrisuys.myapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import be.adrisuys.myapplication.model.DBManager;
import be.adrisuys.myapplication.model.DataHolder;
import be.adrisuys.myapplication.model.Game;
import be.adrisuys.myapplication.presenter.MainPresenter;
import be.adrisuys.myapplication.R;
import be.adrisuys.myapplication.viewinterface.MainViewInterface;

public class MainActivity extends AppCompatActivity implements MainViewInterface {

    private static final String GAME_FILE = "saved_games";
    private RecyclerView recyclerView;
    private EditText searchBar;
    private MyAdapter adapter;
    private MainPresenter presenter;
    private DBManager dbManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this);
        retrieveGamesFromDB();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        searchBar = findViewById(R.id.search_bar);
        handleSearchBar();
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        dbManager = new DBManager(this, presenter);
    }

    @Override
    public void updateList() {
        if (adapter == null){
            adapter = new MyAdapter(presenter);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setPresenter(presenter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void backUp() {
        List<Game> favs = DataHolder.getLikedGames();
        String json = new Gson().toJson(favs);
        List<Game> owned = DataHolder.getOwnedGames();
        String jsonBis = new Gson().toJson(owned);
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("favs", json);
        editor.putString("owned", jsonBis);
        editor.commit();
    }

    @Override
    public void switchActivityToDetails() {
        Intent i = new Intent(this, DetailsActivity.class);
        startActivity(i);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayNoGameFound() {
        Toast.makeText(this, "No game found...", Toast.LENGTH_SHORT).show();
    }

    public void onSearchStarted(View v){
        String input = searchBar.getText().toString().toLowerCase().trim();
        if (input.isEmpty()){
            Toast.makeText(this, "Vide", Toast.LENGTH_SHORT).show();
        } else if (input == null){
            Toast.makeText(this, "Nul", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            dbManager.makeRequest(input);
        }
    }

    public void displayFavs(View v){
        Intent i = new Intent(this, SecondaryActivity.class);
        i.putExtra("mode", "LIKE");
        startActivity(i);
    }

    public void displayOwned(View v){
        Intent i = new Intent(this, SecondaryActivity.class);
        i.putExtra("mode", "OWN");
        startActivity(i);
    }

    public void retrieveGamesFromDB(){
        List<Game> games = new ArrayList<>();
        List<Game> gamesBis = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("favs", null);
        if (json != null){
            games = new Gson().fromJson(json, new TypeToken<List<Game>>(){}.getType());
        }
        DataHolder.setLiked(games);
        String jsonBis = sharedPreferences.getString("owned", null);
        if (jsonBis != null){
            gamesBis = new Gson().fromJson(jsonBis, new TypeToken<List<Game>>(){}.getType());
        }
        DataHolder.setOwned(gamesBis);
    }

    private void handleSearchBar() {
        searchBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    onSearchStarted(v);
                    return true;
                }
                return false; // pass on to other listeners.
            }
        });
    }

    @Override
    public void onBackPressed() { }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, players, time, rating, age;
        ImageButton likeBtn, ownBtn;
        RatingBar ratingBar;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            name.setOnClickListener(this);
            players = itemView.findViewById(R.id.players_number);
            players.setOnClickListener(this);
            time = itemView.findViewById(R.id.game_time);
            time.setOnClickListener(this);
            age = itemView.findViewById(R.id.min_age);
            age.setOnClickListener(this);
            rating = itemView.findViewById(R.id.ratings);
            rating.setOnClickListener(this);
            likeBtn = itemView.findViewById(R.id.like_btn);
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClickLike();
                }
            });
            ownBtn = itemView.findViewById(R.id.own_btn);
            ownBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClickOwn();
                }
            });
            ratingBar = itemView.findViewById(R.id.rating_bar);
            setRatingBarColor();
        }

        private void handleClickLike(){
            Game game = presenter.getGameAtIndex(getAdapterPosition());
            if (DataHolder.isLiked(game)){
                likeBtn.setBackgroundResource(R.drawable.like_bordered);
            } else {
                likeBtn.setBackgroundResource(R.drawable.like_filled);
            }
            presenter.updateLike(game);
        }

        private void handleClickOwn(){
            Game game = presenter.getGameAtIndex(getAdapterPosition());
            if (DataHolder.isOwned(game)){
                ownBtn.setBackgroundResource(R.drawable.owned_false);
            } else {
                ownBtn.setBackgroundResource(R.drawable.owned_true);
            }
            presenter.updateOwn(game);
        }

        private void setRatingBarColor() {
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.magenta), PorterDuff.Mode.SRC_ATOP);
        }

        public void displayItem(Game item) {
            name.setText(item.getNameAndPrice());
            players.setText(item.getNumbersOfPlayers());
            time.setText(item.getPlayTime());
            age.setText(item.getMinAge());
            ratingBar.setRating(item.getRatingAsFloat());
            rating.setText(item.getNumberOfRatings());
            if (DataHolder.isLiked(item)){
                likeBtn.setBackgroundResource(R.drawable.like_filled);
            } else {
                likeBtn.setBackgroundResource(R.drawable.like_bordered);
            }
            if (DataHolder.isOwned(item)){
                ownBtn.setBackgroundResource(R.drawable.owned_true);
            } else {
                ownBtn.setBackgroundResource(R.drawable.owned_false);
            }
        }

        @Override
        public void onClick(View v) {
            presenter.displayGameInfos(getAdapterPosition());
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        private MainPresenter presenter;

        MyAdapter(MainPresenter presenter){
            this.presenter = presenter;
        }

        public void setPresenter(MainPresenter presenter) {
            this.presenter = presenter;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new MyHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            presenter.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return presenter.getItemCount();
        }
    }
}
