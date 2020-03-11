package be.adrisuys.myapplication.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import be.adrisuys.myapplication.R;
import be.adrisuys.myapplication.model.DataHolder;
import be.adrisuys.myapplication.model.Game;
import be.adrisuys.myapplication.presenter.FavsPresenter;
import be.adrisuys.myapplication.viewinterface.FavsViewInterface;

public class FavouritesActivity extends AppCompatActivity implements FavsViewInterface {

    private static final String GAME_FILE = "saved_games";
    private RecyclerView recyclerView;
    private FavsPresenter presenter;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Favourites");
        presenter = new FavsPresenter(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        displayGames();
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

    private void displayGames(){
        if (adapter == null){
            adapter = new FavouritesActivity.MyAdapter(presenter);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setPresenter(presenter);
            adapter.notifyDataSetChanged();
        }
    }

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
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(FavouritesActivity.this, R.color.magenta), PorterDuff.Mode.SRC_ATOP);
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

    private class MyAdapter extends RecyclerView.Adapter<FavouritesActivity.MyHolder> {

        private FavsPresenter presenter;

        MyAdapter(FavsPresenter presenter){
            this.presenter = presenter;
        }

        public void setPresenter(FavsPresenter presenter) {
            this.presenter = presenter;
        }

        @NonNull
        @Override
        public FavouritesActivity.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new FavouritesActivity.MyHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(@NonNull FavouritesActivity.MyHolder holder, int position) {
            presenter.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return presenter.getItemCount();
        }
    }
}
