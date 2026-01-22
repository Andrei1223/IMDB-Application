package productions;


public class Episode {

    private String episodeName;
    private String duration;
    public Episode(){
        episodeName = null;
        duration = null;
    }

    public Episode(String episodeName, String duration){
        this.episodeName = episodeName;
        this.duration = duration;
    }

    public String toString() {
        return "Episode '" + episodeName +
                "' with a duration of " + duration + "\n";
    }

    // getters and setters
    public String getEpisodeName() {
        return episodeName;
    }

    public String getDuration() {
        return duration;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

}