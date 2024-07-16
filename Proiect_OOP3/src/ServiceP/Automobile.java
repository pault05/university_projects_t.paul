package ServiceP;

public interface Automobile {
    default void reparatieVehicul() {
        setStatus(true);
    }

    default boolean statusVehicul() {
        return getStatus();
    }

    // Methods to manipulate the status
    void setStatus(boolean status);

    boolean getStatus();
}
