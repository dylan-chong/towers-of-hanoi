package main;

/**
 * Created by Dylan on 7/03/17.
 *
 * @param T The type to be observed
 * @param N The notification type
 */
public interface Observable<T, N> {
    boolean registerObserver(Observer<T, N> observer);

    void notifyObservers(N notification);
}

