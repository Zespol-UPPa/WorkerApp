package parkflow.deskoptworker.Controllers.Worker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class WorkerMenuController {

    @FXML private Button dashboardBtn;
    @FXML private Button parkingBtn;
    @FXML private Button reportsBtn;
    @FXML private Button customersBtn;  // ✓ POPRAWIONE - było personnelBtn
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;

    private Button currentButton;
    private WorkerController parentController;  // ✓ DODANE - referencja do parent controllera

    @FXML
    public void initialize() {
        System.out.println("WorkerMenuController initialized");

        // Ustaw domyślny aktywny przycisk
        setActiveButton(dashboardBtn);

        // Przypisz akcje do przycisków
        dashboardBtn.setOnAction(e -> handleMenuClick(dashboardBtn, "Dashboard"));
        parkingBtn.setOnAction(e -> handleMenuClick(parkingBtn, "Parkings"));
        reportsBtn.setOnAction(e -> handleMenuClick(reportsBtn, "Reports"));
        customersBtn.setOnAction(e -> handleMenuClick(customersBtn, "Customers"));
        settingsBtn.setOnAction(e -> handleMenuClick(settingsBtn, "Settings"));
        logoutBtn.setOnAction(e -> handleLogout());
    }

    /**
     * Ustawia referencję do głównego WorkerController
     */
    public void setParentController(WorkerController controller) {
        this.parentController = controller;
        System.out.println("Parent controller set: " + controller);
    }

    /**
     * Obsługuje kliknięcie w przycisk menu
     */
    private void handleMenuClick(Button clickedButton, String viewName) {
        System.out.println("Menu clicked: " + viewName);

        // Zmień aktywny przycisk
        setActiveButton(clickedButton);

        // Powiadom parent controller o zmianie
        if (parentController != null) {
            parentController.onMenuItemSelected(viewName);
        } else {
            System.err.println("ERROR: Parent controller is null!");
        }
    }

    /**
     * Obsługuje wylogowanie
     */
    private void handleLogout() {
        System.out.println("Logging out...");
        // TODO: Implement logout logic
        // viewFactory.showLoginWindow();
        // closeCurrentWindow();
    }

    /**
     * Ustawia aktywny przycisk (dodaje klasę CSS "current")
     */
    private void setActiveButton(Button button) {
        // Usuń klasę z poprzedniego przycisku
        if (currentButton != null) {
            currentButton.getStyleClass().remove("current");
        }

        // Dodaj klasę do nowego przycisku
        button.getStyleClass().add("current");

        // Zapamiętaj aktualny przycisk
        currentButton = button;
    }

    /**
     * Programowo ustawia aktywny element menu (bez wywoływania handleMenuClick).
     * Używane przy nawigacji z innych modułów (np. Parkings → Customers).
     *
     * @param menuItem nazwa elementu menu: "Dashboard", "Parkings", "Reports", "Customers", "Settings"
     */
    public void setActiveMenuItem(String menuItem) {
        Button targetButton = switch (menuItem) {
            case "Dashboard" -> dashboardBtn;
            case "Parkings" -> parkingBtn;
            case "Reports" -> reportsBtn;
            case "Customers" -> customersBtn;
            case "Settings" -> settingsBtn;
            default -> null;
        };

        if (targetButton != null) {
            setActiveButton(targetButton);
            System.out.println("Menu item programmatically set to: " + menuItem);
        } else {
            System.err.println("Unknown menu item: " + menuItem);
        }
    }
}