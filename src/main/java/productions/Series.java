package productions;

import java.util.*;

public class Series extends Production{
    public int releaseYear, numberSeasons, numberOfEpisodes;
    private Map<String, List<Episode>> seasons;

    // constructor
    public Series(){
        super();
        releaseYear = 0;
        numberSeasons = 0;
        numberOfEpisodes = 0;
        seasons = new HashMap<>();
    }

    // method to add episodes to the map
    public void addEpisodes(String season, List<Episode> episodes){
        seasons.put(season, episodes);
        updateNumberOfEpisodes(); // update the total number of episodes
    }

    // method to get episodes for a specific season
    public Map<String, List<Episode>> getSeasons() {
        return seasons;
    }

    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void updateNumberOfEpisodes() {
        numberOfEpisodes = 0;
        for (Map.Entry<String, List<Episode>> entry : seasons.entrySet()) {
            List<Episode> episodeList = entry.getValue();
            numberOfEpisodes += episodeList.size();
        }
    }

    public void setSeasons(Map<String, List<Episode>> seasons) {
        this.seasons = seasons;
        updateNumberOfEpisodes();
    }

    // define displayInfo method
    public void displayInfo(boolean running_mode) {
        String display = "";

        display = title + " " + " " + plot;

        // TODO change based on type of UI
        System.out.println(display);
    }

    public int compareTo(Object obj){
        if (obj instanceof Production)
            return super.title.compareTo(((Production)obj).title);
        else if (obj instanceof Actor)
            return super.title.compareTo(((Actor)obj).name);
        else
            System.out.println("Error no valid object type");
        return 0;
    }

    // method that returns the list of ratings as a formatted string
    public String convert_ratings() {
        String str = "";
        int i = 0;
        // traverse all the ratings
        for (i = 0; i < super.getNumberOfRatings(); i++) {
            Rating rating = super.getRating(i);
            str += rating.toString();
        }

        return str;
    }

    public String convert_episodes(List<Episode> episode_list) {
        String str = "";
        int index = 1;
        // traverse each episode from the list
        for (Episode episode : episode_list) {
            str += (index++) + ") " + episode.toString();
        }
        return str;
    }

    public String convert_list_of_episodes() {
        String str = "";
        // traverse each element from the hash map
        for (Map.Entry<String, List<Episode>> list_of_episodes : seasons.entrySet()) {
            String season_name = list_of_episodes.getKey();
            List<Episode> episodes = list_of_episodes.getValue();

            str += "The season '" + season_name + "' has the following episodes: \n\n";
            str += convert_episodes(episodes);
        }
        return str;
    }

    public String toString() {
        return super.toString() +
                "\nThe number of seasons is: " + numberSeasons +
                "\n" + convert_list_of_episodes() ;
    }

    // method that returns the list of episodes
    public Map<String, List<Episode>> returnList() {
        return seasons;
    }
}