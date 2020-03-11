package be.adrisuys.myapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        getSupportActionBar().setTitle("Boardgames");
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
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = openFileOutput(GAME_FILE, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(DataHolder.getLikedGames());
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (oos != null){
                try {
                    oos.flush();
                    oos.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.favs:
                displayFavs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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

    public void retrieveGamesFromDB(){
        List<Game> games = null;
        try {
            FileInputStream fis = openFileInput(GAME_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            games = (List<Game>) ois.readObject();
            for (Game g : games) System.out.println(g);
            DataHolder.setLiked(games);
            ois.close();
            fis.close();
        } catch (Exception e){
            games = new ArrayList<>();
            DataHolder.setLiked(games);
            e.printStackTrace();
        }
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

    private void displayFavs(){

    }

    @Override
    public void onBackPressed() { }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, players, time, rating;
        ImageButton likeBtn;
        RatingBar ratingBar;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            name.setOnClickListener(this);
            players = itemView.findViewById(R.id.players_number);
            players.setOnClickListener(this);
            time = itemView.findViewById(R.id.game_time);
            time.setOnClickListener(this);
            rating = itemView.findViewById(R.id.ratings);
            rating.setOnClickListener(this);
            likeBtn = itemView.findViewById(R.id.like_btn);
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick();
                }
            });
            ratingBar = itemView.findViewById(R.id.rating_bar);
            setRatingBarColor();
        }

        private void handleClick(){
            Game game = presenter.getGameAtIndex(getAdapterPosition());
            if (DataHolder.isLiked(game)){
                likeBtn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
            } else {
                likeBtn.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            }
            presenter.updateLike(game);
        }

        private void setRatingBarColor() {
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.magenta), PorterDuff.Mode.SRC_ATOP);
        }

        public void displayItem(Game item) {
            name.setText(item.getName());
            players.setText(item.getNumbersOfPlayers() + " ppl");
            time.setText(item.getPlayTime());
            ratingBar.setRating(item.getRatingAsFloat());
            rating.setText(item.getNumberOfRatings());
            if (DataHolder.isLiked(item)){
                likeBtn.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            } else {
                likeBtn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
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
