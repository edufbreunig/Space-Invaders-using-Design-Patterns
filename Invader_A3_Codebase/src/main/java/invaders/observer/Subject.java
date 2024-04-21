package invaders.observer;

public interface Subject {
    void removeObserver(Observer observer);
    void registerObserver(Observer observer);

    void notifyObservers();
}
