package productions;

public class Movie extends Production{

    public String duration;
    public int releaseYear;

    public Movie(){
        super();

        releaseYear = 0;
        duration = "";
    }

    // define displayInfo method
    public void displayInfo(boolean running_mode) {
        String display = "";

        display = title + " " + " " + plot;

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

    // tostring method for printing to GUI and also for CLI
    public String toString() {
        return super.toString();
    }
}