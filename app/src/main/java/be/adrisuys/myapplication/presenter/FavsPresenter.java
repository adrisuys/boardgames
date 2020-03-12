package be.adrisuys.myapplication.presenter;

import java.util.List;

import be.adrisuys.myapplication.model.DataHolder;
import be.adrisuys.myapplication.model.Game;
import be.adrisuys.myapplication.view.FavouritesActivity;
import be.adrisuys.myapplication.viewinterface.FavsViewInterface;

public class FavsPresenter {

    private FavsViewInterface view;
    private List<Game> items;

    public FavsPresenter(FavsViewInterface view){
        this.view = view;
        items = DataHolder.getLikedGames();
        if (items.size() == 0){
            view.displayNoGameLiked();
        }
    }

    public Game getGameAtIndex(int adapterPosition) {
        return items.get(adapterPosition);
    }

    public void updateLike(Game game) {
        if (DataHolder.isLiked(game)){
            DataHolder.unlikeGame(game);
        } else {
            DataHolder.likeGame(game);
        }
        view.backUp();
        items = DataHolder.getLikedGames();
        view.updateList();
    }

    public void displayGameInfos(int adapterPosition) {
        Game game = items.get(adapterPosition);
        DataHolder.setCurrentGame(game);
        view.switchActivityToDetails();
    }

    public void onBindViewHolder(FavouritesActivity.MyHolder holder, int position) {
        Game item = items.get(position);
        holder.displayItem(item);
    }

    public int getItemCount() {
        return items.size();
    }
}
