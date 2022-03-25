package day04;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ActorsMoviesService {

    // --- attributes ---------------------------------------------------------

    private ActorsRepository actorsRepository;
    private MoviesRepository moviesRepository;
    private ActorsMoviesRepository actorsMoviesRepository;

    // --- constructors -------------------------------------------------------

    public ActorsMoviesService(
        ActorsRepository actorsRepository,
        MoviesRepository moviesRepository, ActorsMoviesRepository actorsMoviesRepository) {

        this.actorsRepository = actorsRepository;
        this.moviesRepository = moviesRepository;
        this.actorsMoviesRepository = actorsMoviesRepository;
    }

    // --- getters and setters ------------------------------------------------

    public ActorsRepository getActorsRepository() { return actorsRepository; }
    public MoviesRepository getMoviesRepository() { return moviesRepository; }
    public ActorsMoviesRepository getActorsMoviesRepository() { return actorsMoviesRepository; }

    // --- public methods -----------------------------------------------------

    public void insertMovieWithActors(String title, LocalDate releaseDate, List<String> actorNames) {
        Long movieId = moviesRepository.saveMovie(title, releaseDate);
        for (String actorName : actorNames) {
            updateActorsAndActorsMoviesTables(movieId, actorName);
        }
    }

    // --- private methods ----------------------------------------------------

    private void updateActorsAndActorsMoviesTables(Long movieId, String actorName) {
        Long actorId;
        Optional<Actor> foundActor = actorsRepository.findActorByName(actorName);
        if (foundActor.isPresent()) {
            actorId = foundActor.get().getId();
        } else {
            actorId = actorsRepository.saveActor(actorName);
        }
        actorsMoviesRepository.insertActorAndMovieId(actorId, movieId);
    }
}