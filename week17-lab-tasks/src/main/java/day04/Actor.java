package day04;

public class Actor {

    // --- attributes ---------------------------------------------------------

    private Long id;
    private String name;

    // --- constructors -------------------------------------------------------

    public Actor(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // --- getters and setters ------------------------------------------------

    public Long getId() { return id; }
    public String getName() { return name; }

    // --- public methods -----------------------------------------------------

    @Override
    public String toString() {
        return "Actor{id=" + id + ", name='" + name + "'}";
    }
}