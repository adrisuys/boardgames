package be.adrisuys.myapplication.view;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import be.adrisuys.myapplication.R;
import be.adrisuys.myapplication.model.DataHolder;
import be.adrisuys.myapplication.model.Game;

public class DetailsActivity extends AppCompatActivity {

    private TextView description, name, categories;
    private ImageView img, likeBtn;
    private Game currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        currentGame = DataHolder.getCurrentGame();
        name = findViewById(R.id.game_title);
        description = findViewById(R.id.description);
        categories = findViewById(R.id.categories);
        description.setMovementMethod(new ScrollingMovementMethod());
        img = findViewById(R.id.img);
        likeBtn = findViewById(R.id.like_btn);
        handleBtnClicked();
        displayGameDetails();
    }

    private void handleBtnClicked() {
        if (!DataHolder.isLiked(currentGame)){
            likeBtn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
        } else {
            likeBtn.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
        }
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataHolder.isLiked(currentGame)){
                    likeBtn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                } else {
                    likeBtn.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                }
                updateLike();
            }
        });
    }

    private void updateLike(){
        if (DataHolder.isLiked(currentGame)){
            DataHolder.unlikeGame(currentGame);
        } else {
            DataHolder.likeGame(currentGame);
        }
        backUp();
    }

    private void backUp() {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = openFileOutput("saved_games", Context.MODE_PRIVATE);
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

    private void displayGameDetails() {
        name.setText(currentGame.getNameAndYear());
        description.setText(Html.fromHtml(currentGame.getDescription()));
        categories.setText(currentGame.getCategories());
        setImage();
    }

    private void setImage() {
        if (currentGame.getImgUrl() != ""){
            Picasso.get()
                    .load(currentGame.getImgUrl())
                    .centerCrop()
                    .transform(new RoundedRectImage(50, 0))
                    .fit()
                    .into(img);
        } else {
            img.setBackgroundResource(R.drawable.no_image_available);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(this, MainActivity.class));
    }
}
