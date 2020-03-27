package be.adrisuys.myapplication.presenter;

import java.util.List;

import be.adrisuys.myapplication.model.DataHolder;
import be.adrisuys.myapplication.model.Game;
import be.adrisuys.myapplication.view.SecondaryActivity;
import be.adrisuys.myapplication.viewinterface.SecondaryViewInterface;

public class SecondaryPresenter {

    private SecondaryViewInterface view;
    private List<Game> items;
    private String mode;

    public SecondaryPresenter(SecondaryViewInterface view, String mode){
        this.view = view;
        this.mode = mode;
        if (mode.equals("LIKE")){
            items = DataHolder.getLikedGames();
            if (items.size() == 0){
                view.displayNoGameLiked();
            }
        } else {
            items = DataHolder.getOwnedGames();
            if (items.size() == 0){
                view.displayNoGameOwned();
            }
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
        items = (mode.equals("LIKE")) ? DataHolder.getLikedGames() : DataHolder.getOwnedGames();
        view.updateList();
    }

    public void updateOwn(Game game) {
        if (DataHolder.isOwned(game)){
            DataHolder.loseGame(game);
        } else {
            DataHolder.ownGame(game);
        }
        view.backUp();
        items = (mode.equals("LIKE")) ? DataHolder.getLikedGames() : DataHolder.getOwnedGames();
        view.updateList();
    }

    public void displayGameInfos(int adapterPosition) {
        Game game = items.get(adapterPosition);
        DataHolder.setCurrentGame(game);
        view.switchActivityToDetails();
    }

    public void onBindViewHolder(SecondaryActivity.MyHolder holder, int position) {
        Game item = items.get(position);
        holder.displayItem(item);
    }

    public int getItemCount() {
        return items.size();
    }
}
