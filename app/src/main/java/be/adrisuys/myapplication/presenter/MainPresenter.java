package be.adrisuys.myapplication.presenter;

import java.util.List;

import be.adrisuys.myapplication.model.Categorie;
import be.adrisuys.myapplication.model.DataHolder;
import be.adrisuys.myapplication.model.Game;
import be.adrisuys.myapplication.view.MainActivity;
import be.adrisuys.myapplication.viewinterface.MainViewInterface;

public class MainPresenter {

    private MainViewInterface view;
    private List<Game> items;
    private List<Categorie> allCategories;

    public MainPresenter(MainViewInterface view){
        this.view = view;
    }

    public int getItemCount() {
        return items.size();
    }

    public void onBindViewHolder(MainActivity.MyHolder holder, int position) {
        Game item = items.get(position);
        holder.displayItem(item);
    }

    public void onGameListRecovered(List<Game> games) {
        items = games;
        if (items.size() == 0){
            view.displayNoGameFound();
        }
        view.hideProgressBar();
        view.updateList();
    }

    public Game getGameAtIndex(int i){
        return items.get(i);
    }

    public void updateLike(Game game) {
        if (DataHolder.isLiked(game)){
            DataHolder.unlikeGame(game);
        } else {
            DataHolder.likeGame(game);
        }
        view.backUp();
    }

    public void displayGameInfos(int adapterPosition) {
        Game game = items.get(adapterPosition);
        DataHolder.setCurrentGame(game);
        view.switchActivityToDetails();
    }

    public void setCategories(List<Categorie> categories) {
        allCategories = categories;
    }

    public Categorie getCategoriesById(String id){
        for (Categorie categorie : allCategories){
            if (categorie.getId().equals(id)){
                return categorie;
            }
        }
        return null;
    }
}
