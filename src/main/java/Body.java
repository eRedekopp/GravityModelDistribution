public interface Body<T> {

    Body<T> plus(Body<T> other);

    double distanceTo(Body<T> other);

    double computeGravForce(Body<T> other);
}
