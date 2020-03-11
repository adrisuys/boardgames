package be.adrisuys.myapplication.view;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Html;
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

    private TextView description;
    private ImageView img;
    private Game currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        currentGame = DataHolder.getCurrentGame();
        getSupportActionBar().setTitle(currentGame.getNameAndYear());
        description = findViewById(R.id.description);
        img = findViewById(R.id.img);
        displayGameDetails();
    }

    private void displayGameDetails() {
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
