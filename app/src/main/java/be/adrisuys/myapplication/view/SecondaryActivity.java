package be.adrisuys.myapplication.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import be.adrisuys.myapplication.R;
import be.adrisuys.myapplication.model.DataHolder;
import be.adrisuys.myapplication.model.Game;
import be.adrisuys.myapplication.presenter.SecondaryPresenter;
import be.adrisuys.myapplication.viewinterface.SecondaryViewInterface;

public class SecondaryActivity extends AppCompatActivity implements SecondaryViewInterface {

    private RecyclerView recyclerView;
    private SecondaryPresenter presenter;
    private MyAdapter adapter;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        title = findViewById(R.id.title);
        String mode = getIntent().getStringExtra("mode");
        title.setText((mode.equals("LIKE")) ? "Wish List" : "My games");
        presenter = new SecondaryPresenter(this, mode);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        displayGames();
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
    public void displayNoGameLiked() {
        Toast.makeText(this, "You don't like any game yet..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayNoGameOwned() {
        Toast.makeText(this, "You don't own any game yet..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateList() {
        displayGames();
    }

    private void displayGames(){
        if (adapter == null){
            adapter = new SecondaryActivity.MyAdapter(presenter);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setPresenter(presenter);
            adapter.notifyDataSetChanged();
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, players, time, rating;
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
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(SecondaryActivity.this, R.color.magenta), PorterDuff.Mode.SRC_ATOP);
        }

        public void displayItem(Game item) {
            name.setText(item.getNameAndPrice());
            players.setText(item.getNumbersOfPlayers());
            time.setText(item.getPlayTime());
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

    private class MyAdapter extends RecyclerView.Adapter<SecondaryActivity.MyHolder> {

        private SecondaryPresenter presenter;

        MyAdapter(SecondaryPresenter presenter){
            this.presenter = presenter;
        }

        public void setPresenter(SecondaryPresenter presenter) {
            this.presenter = presenter;
        }

        @NonNull
        @Override
        public SecondaryActivity.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new SecondaryActivity.MyHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(@NonNull SecondaryActivity.MyHolder holder, int position) {
            presenter.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return presenter.getItemCount();
        }
    }
}
