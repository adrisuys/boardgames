package be.adrisuys.myapplication.view;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import be.adrisuys.myapplication.R;
import be.adrisuys.myapplication.model.DataHolder;
import be.adrisuys.myapplication.model.Game;

public class DetailsActivity extends AppCompatActivity {

    private TextView description, name;
    private ImageView img;
    private Game currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        currentGame = DataHolder.getCurrentGame();
        name = findViewById(R.id.game_title);
        description = findViewById(R.id.description);
        description.setMovementMethod(new ScrollingMovementMethod());
        img = findViewById(R.id.img);
        displayGameDetails();
    }

    private void displayGameDetails() {
        name.setText(currentGame.getNameAndYear());
        description.setText(Html.fromHtml(currentGame.getDescription()));
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
