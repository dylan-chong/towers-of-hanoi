package main;

/**
 * Created by Dylan on 7/03/17.
 *
 * @param T The observable type
 * @param N The notification type
 */
public interface Observer<T, N> {
    void receiveNotification(N notification);
}
