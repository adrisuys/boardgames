package be.adrisuys.myapplication.model;

import java.util.List;

public class DataHolder {
    private static List<Game> liked;
    private static List<Game> owned;
    private static Game currentGame;
    private static List<Categorie> categories;

    public static void setLiked(List<Game> other) {
        liked = other;
    }

    public static List<Game> getLikedGames(){
        return liked;
    }

    public static void likeGame(Game game){
        for (int i = 0; i < liked.size(); i++){
            if (liked.get(i).getName().equals(game.getName()) && liked.get(i).getPlayTime().equals(game.getPlayTime())){
                return;
            }
        }
        liked.add(game);
    }

    public static void unlikeGame(Game game){
        for (int i = 0; i < liked.size(); i++){
            if (liked.get(i).getName().equals(game.getName()) && liked.get(i).getPlayTime().equals(game.getPlayTime())){
                liked.remove(i);
            }
        }
    }

    public static boolean isLiked(Game game){
        for (Game g : liked){
            if (g.getName().equals(game.getName()) && g.getPlayTime().equals(game.getPlayTime())){
                return true;
            }
        }
        return false;
    }

    public static Game getCurrentGame() {
        return currentGame;
    }

    public static void setCurrentGame(Game currentGame) {
        DataHolder.currentGame = currentGame;
    }

    public static void setCategories(List<Categorie> categories) {
        DataHolder.categories = categories;
    }

    public static List<Categorie> getCategories() {
        return categories;
    }

    public static void setOwned(List<Game> owned) {
        DataHolder.owned = owned;
    }

    public static List<Game> getOwnedGames() {
        return owned;
    }

    public static void ownGame(Game game){
        for (int i = 0; i < owned.size(); i++){
            if (owned.get(i).getName().equals(game.getName()) && owned.get(i).getPlayTime().equals(game.getPlayTime())){
                return;
            }
        }
        owned.add(game);
    }

    public static void loseGame(Game game){
        for (int i = 0; i < owned.size(); i++){
            if (owned.get(i).getName().equals(game.getName()) && owned.get(i).getPlayTime().equals(game.getPlayTime())){
                owned.remove(i);
            }
        }
    }

    public static boolean isOwned(Game game){
        for (Game g : owned){
            if (g.getName().equals(game.getName()) && g.getPlayTime().equals(game.getPlayTime())){
                return true;
            }
        }
        return false;
    }
}
