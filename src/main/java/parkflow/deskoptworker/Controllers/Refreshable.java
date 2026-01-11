package parkflow.deskoptworker.Controllers;

/**
 * Interface for controllers that need to refresh their data
 * when the view becomes visible again
 */
public interface Refreshable {
    /**
     * Called when the view is displayed/activated
     * Controllers should reload their data from the backend here
     */
    void refresh();
}
