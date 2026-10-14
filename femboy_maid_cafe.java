import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

public class femboy_maid_cafe {
    // ANSI escape codes for red error text
    private static final String RED = "\033[31m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String CYAN = "\033[36m";
    private static final String BOLD = "\033[1m";
    private static final String RESET = "\033[0m";
    private static final String ORDER_SEPARATOR = "====================================";
    private static final String RECEIPT_SEPARATOR =
        "------------------------------------------------------------";

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pause(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static Integer readInteger(Scanner scanner, String prompt, String fieldName) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println(RED + "\n" + fieldName + " cannot be blank. Please enter a value." + RESET);
            return null;
        }

        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            System.out.println(RED + "\n" + fieldName + " must be a whole number." + RESET);
            return null;
        }
    }

    private static BigDecimal readPrice(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println(RED + "\nPrice cannot be blank." + RESET);
            return null;
        }

        try {
            BigDecimal price = new BigDecimal(input).setScale(2, RoundingMode.HALF_UP);
            if (price.signum() < 0) {
                System.out.println(RED + "\nPrice cannot be negative." + RESET);
                return null;
            }
            return price;
        } catch (NumberFormatException e) {
            System.out.println(RED + "\nEnter a valid price, such as 120 or 120.50." + RESET);
            return null;
        }
    }

    private static String formatCurrency(BigDecimal amount) {
        return String.format("PHP %,.2f", amount);
    }

    private static void printReceipt(int orderNumber, Map<String, Integer> order,
            Map<String, BigDecimal> itemPrices) {
        BigDecimal total = BigDecimal.ZERO;
        clearScreen();
        System.out.println(CYAN + RECEIPT_SEPARATOR);
        System.out.println("                 FEMBOY MAID CAFE");
        System.out.println("                    ORDER RECEIPT");
        System.out.println("Order #" + orderNumber);
        System.out.println(RECEIPT_SEPARATOR + RESET);
        System.out.printf(YELLOW + "%-25s %5s %10s %12s%n" + RESET,
            "Description", "Qty", "Unit Price", "Amount");
        System.out.println(CYAN + RECEIPT_SEPARATOR + RESET);

        for (Map.Entry<String, Integer> entry : order.entrySet()) {
            BigDecimal unitPrice = itemPrices.get(entry.getKey());
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(entry.getValue()));
            total = total.add(lineTotal);
            System.out.printf("%-25s %5d %10s %12s%n",
                entry.getKey(), entry.getValue(), formatCurrency(unitPrice),
                formatCurrency(lineTotal));
        }

        System.out.println(CYAN + RECEIPT_SEPARATOR + RESET);
        System.out.printf(BOLD + GREEN + "%-42s %12s%n" + RESET,
            "TOTAL DUE", formatCurrency(total));
        System.out.println(CYAN + RECEIPT_SEPARATOR);
        System.out.println("            Thank you for your order!");
        System.out.println(RECEIPT_SEPARATOR + RESET);
    }

    private static void editCustomerCart(Scanner scanner, Map<String, Integer> cart, List<String> cafeMenu,
            Map<String, BigDecimal> itemPrices) {
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty. Add an item first.");
            pause(scanner);
            return;
        }

        boolean editing = true;
        while (editing) {
            clearScreen();
            System.out.println("\n" + CYAN + "====EDIT CART====" + RESET + "\n");
            int itemNumber = 1;
            for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                System.out.println("[" + itemNumber++ + "]- " + entry.getKey() + " x" + entry.getValue());
            }
            System.out.println("[" + itemNumber + "]- Add menu item");
            System.out.println("[" + (itemNumber + 1) + "]- Remove cart item");
            System.out.println("[0]- Finish editing");

            Integer selectionInput = readInteger(scanner, "\nSelect an option: ", "Cart option");
            if (selectionInput == null) {
                pause(scanner);
                continue;
            }
            int selection = selectionInput;

            if (selection == 0) {
                editing = false;
            } else if (selection >= 1 && selection <= cart.size()) {
                String selectedItem = new ArrayList<>(cart.keySet()).get(selection - 1);
                Integer newQuantityInput = readInteger(scanner,
                    "Enter new quantity for " + selectedItem + " (0 to remove): ", "Quantity");
                if (newQuantityInput == null) {
                    pause(scanner);
                    continue;
                }
                int newQuantity = newQuantityInput;

                if (newQuantity < 0) {
                    System.out.println(RED + "\nQuantity cannot be negative." + RESET);
                } else if (newQuantity == 0) {
                    cart.remove(selectedItem);
                    System.out.println("\nRemoved " + selectedItem + " from the cart.");
                } else {
                    cart.put(selectedItem, newQuantity);
                    System.out.println("\nUpdated " + selectedItem + " to quantity " + newQuantity + ".");
                }
                pause(scanner);
            } else if (selection == cart.size() + 1) {
                if (cafeMenu.isEmpty()) {
                    System.out.println(RED + "\nThe food menu is empty; no item can be added." + RESET);
                    pause(scanner);
                    continue;
                }

                System.out.println("\nFood Menu:");
                for (int i = 0; i < cafeMenu.size(); i++) {
                    String item = cafeMenu.get(i);
                    System.out.println("[" + (i + 1) + "]- " + item + " - " + formatCurrency(itemPrices.get(item)));
                }

                Integer menuSelectionInput = readInteger(scanner,
                    "Select an item to add (0 to cancel): ", "Menu item choice");
                if (menuSelectionInput == null) {
                    pause(scanner);
                    continue;
                }
                int menuSelection = menuSelectionInput;

                if (menuSelection == 0) {
                    continue;
                }
                if (menuSelection < 1 || menuSelection > cafeMenu.size()) {
                    System.out.println(RED + "\nInvalid menu item number." + RESET);
                    pause(scanner);
                    continue;
                }

                Integer quantityInput = readInteger(scanner, "Enter quantity to add: ", "Quantity");
                if (quantityInput == null) {
                    pause(scanner);
                    continue;
                }
                int quantity = quantityInput;

                if (quantity <= 0) {
                    System.out.println(RED + "\nQuantity must be greater than 0." + RESET);
                } else {
                    String item = cafeMenu.get(menuSelection - 1);
                    cart.put(item, cart.getOrDefault(item, 0) + quantity);
                    System.out.println("\nAdded " + quantity + "x " + item + " to the cart.");
                }
                pause(scanner);
            } else if (selection == cart.size() + 2) {
                if (cart.isEmpty()) {
                    System.out.println(RED + "\nYour cart is empty." + RESET);
                    pause(scanner);
                    continue;
                }

                System.out.println("\nCart Items:");
                List<String> cartItems = new ArrayList<>(cart.keySet());
                for (int i = 0; i < cartItems.size(); i++) {
                    String item = cartItems.get(i);
                    System.out.println("[" + (i + 1) + "]- " + item + " x" + cart.get(item));
                }

                Integer itemToRemoveInput = readInteger(scanner,
                    "Select an item to remove (0 to cancel): ", "Cart item choice");
                if (itemToRemoveInput == null) {
                    pause(scanner);
                    continue;
                }
                int itemToRemove = itemToRemoveInput;

                if (itemToRemove == 0) {
                    continue;
                }
                if (itemToRemove < 1 || itemToRemove > cartItems.size()) {
                    System.out.println(RED + "\nInvalid cart item number." + RESET);
                    pause(scanner);
                    continue;
                }

                String removedItem = cartItems.get(itemToRemove - 1);
                cart.remove(removedItem);
                System.out.println("\nRemoved " + removedItem + " from the cart.");
                pause(scanner);
            } else {
                System.out.println(RED + "\nInvalid cart option." + RESET);
                pause(scanner);
            }
        }
    }

    private static boolean handleOrderActionMenu(Scanner scanner, Map<String, Integer> cart,
            List<String> cafeMenu, Map<String, BigDecimal> itemPrices) {
        while (true) {
            clearScreen();
            System.out.println("\n====ORDER ACTIONS====\n");
            System.out.println("[1]- Finish Order");
            System.out.println("[2]- Edit Current Cart");
            System.out.println("[0]- Return to Menu");

            Integer actionInput = readInteger(scanner, "\nChoose an action: ", "Action choice");
            if (actionInput == null) {
                pause(scanner);
                continue;
            }
            int action = actionInput;

            switch (action) {
                case 1:
                    return true;
                case 2:
                    editCustomerCart(scanner, cart, cafeMenu, itemPrices);
                    break;
                case 0:
                    return false;
                default:
                    System.out.println(RED + "\nInvalid action choice." + RESET);
                    pause(scanner);
                    break;
            }
        }
    }

    private static void printOrderBlock(Integer orderNumber, Map<String, Integer> order) {
        // Opening separator before each order
        System.out.println(ORDER_SEPARATOR);
        System.out.println("Order #" + orderNumber + ":");
        if (order == null || order.isEmpty()) {
            System.out.println("- No items");
        } else {
            for (Map.Entry<String, Integer> entry : order.entrySet()) {
                System.out.println("- " + entry.getKey() + " x" + entry.getValue());
            }
        }
    }

    private static void printOrdersSummary(String title, Queue<Integer> orderNumbers,
            Map<Integer, Map<String, Integer>> orders) {
        System.out.println();
        if (title.toLowerCase().contains("ready")) {
            System.out.println(GREEN + title + RESET);
        } else if (title.toLowerCase().contains("prepared") || title.toLowerCase().contains("pending")) {
            System.out.println(YELLOW + title + RESET);
        } else {
            System.out.println(title);
        }
        List<Integer> displayedOrders = new ArrayList<>();

        for (Integer number : orderNumbers) {
            Map<String, Integer> order = orders.get(number);
            if (order != null && !order.isEmpty() && !displayedOrders.contains(number)) {
                displayedOrders.add(number);
            }
        }

        if (displayedOrders.isEmpty()) {
            System.out.println("No current orders.");
            return;
        }

        for (Integer number : displayedOrders) {
            printOrderBlock(number, orders.get(number));
        }
        // Hardcoded closing separator for this view
        System.out.println(ORDER_SEPARATOR);
    }

    private static void editOrders(Scanner scanner, Queue<Integer> processingNumbers,
            Queue<Integer> readyNumbers, Map<Integer, Map<String, Integer>> orders,
            List<String> cafeMenu, Map<String, BigDecimal> itemPrices) {
        if (processingNumbers.isEmpty() && readyNumbers.isEmpty()) {
            System.out.println("\nNo current orders to edit.");
            return;
        }

        // Show being-prepared orders first, then delivered (ready) orders separately
        if (!processingNumbers.isEmpty()) {
            printOrdersSummary("Current Orders - Being Prepared:", processingNumbers, orders);
        }
        if (!readyNumbers.isEmpty()) {
            printOrdersSummary("Delivered Orders - Ready for Pickup:", readyNumbers, orders);
        }

        Integer orderNumberInput = readInteger(scanner,
            "\nEnter order number to edit (0 to go back): ", "Order number");
        if (orderNumberInput == null) {
            return;
        }
        int orderNumber = orderNumberInput;

        if (orderNumber == 0) {
            return;
        }

        Map<String, Integer> order = orders.get(orderNumber);
        if (order == null || (!processingNumbers.contains(orderNumber) && !readyNumbers.contains(orderNumber))) {
            System.out.println(RED + "\nThat order does not exist or is no longer active." + RESET);
            return;
        }
        if (readyNumbers.contains(orderNumber) || !processingNumbers.contains(orderNumber)) {
            System.out.println(RED + "\nThat order has already been marked ready for pickup and cannot be edited." + RESET);
            return;
        }

        boolean editing = true;
        while (editing) {
            clearScreen();
            System.out.println("\n====EDIT ORDER #" + orderNumber + "====\n");
            int itemNumber = 1;
            for (Map.Entry<String, Integer> entry : order.entrySet()) {
                System.out.println("[" + itemNumber++ + "]- " + entry.getKey() + " x" + entry.getValue());
            }
            System.out.println("[" + itemNumber + "]- Add menu item");
            System.out.println("[" + (itemNumber + 1) + "]- Remove order item");
            System.out.println("[0]- Finish editing");
            System.out.print("\nSelect an item to edit: ");

            Integer selectionInput = readInteger(scanner, "", "Item choice");
            if (selectionInput == null) {
                pause(scanner);
                continue;
            }
            int selection = selectionInput;

            if (selection == 0) {
                editing = false;
            } else if (selection >= 1 && selection <= order.size()) {
                String selectedItem = new ArrayList<>(order.keySet()).get(selection - 1);
                Integer newQuantityInput = readInteger(scanner,
                    "Enter new quantity for " + selectedItem + " (0 to remove): ", "Quantity");
                if (newQuantityInput == null) {
                    pause(scanner);
                    continue;
                }
                int newQuantity = newQuantityInput;

                if (newQuantity < 0) {
                    System.out.println(RED + "\nQuantity cannot be negative." + RESET);
                } else if (newQuantity == 0 && order.size() == 1) {
                    System.out.println(RED + "\nAn order must contain at least one item." + RESET);
                } else if (newQuantity == 0) {
                    order.remove(selectedItem);
                    System.out.println("\nRemoved " + selectedItem + " from the order.");
                } else {
                    order.put(selectedItem, newQuantity);
                    System.out.println("\nUpdated " + selectedItem + " to quantity " + newQuantity + ".");
                }
                pause(scanner);
            } else if (selection == order.size() + 1) {
                if (cafeMenu.isEmpty()) {
                    System.out.println(RED + "\nThe food menu is empty; no item can be added." + RESET);
                    pause(scanner);
                    continue;
                }

                System.out.println("\nFood Menu:");
                for (int i = 0; i < cafeMenu.size(); i++) {
                    String item = cafeMenu.get(i);
                    System.out.println("[" + (i + 1) + "]- " + item + " - " + formatCurrency(itemPrices.get(item)));
                }

                Integer menuSelectionInput = readInteger(scanner,
                    "Select an item to add (0 to cancel): ", "Menu item choice");
                if (menuSelectionInput == null) {
                    pause(scanner);
                    continue;
                }
                int menuSelection = menuSelectionInput;

                if (menuSelection == 0) {
                    continue;
                }
                if (menuSelection < 1 || menuSelection > cafeMenu.size()) {
                    System.out.println(RED + "\nInvalid menu item number." + RESET);
                    pause(scanner);
                    continue;
                }

                Integer quantityInput = readInteger(scanner, "Enter quantity to add: ", "Quantity");
                if (quantityInput == null) {
                    pause(scanner);
                    continue;
                }
                int quantity = quantityInput;

                if (quantity <= 0) {
                    System.out.println(RED + "\nQuantity must be greater than 0." + RESET);
                } else {
                    String item = cafeMenu.get(menuSelection - 1);
                    order.put(item, order.getOrDefault(item, 0) + quantity);
                    System.out.println("\nAdded " + quantity + "x " + item + " to the order.");
                }
                pause(scanner);
            } else if (selection == order.size() + 2) {
                if (order.size() == 1) {
                    System.out.println(RED + "\nCannot remove the last item. An order must contain at least one item." + RESET);
                    pause(scanner);
                    continue;
                }

                System.out.println("\nOrder Items:");
                List<String> orderItems = new ArrayList<>(order.keySet());
                for (int i = 0; i < orderItems.size(); i++) {
                    String item = orderItems.get(i);
                    System.out.println("[" + (i + 1) + "]- " + item + " x" + order.get(item));
                }

                Integer itemToRemoveInput = readInteger(scanner,
                    "Select an order item to remove (0 to cancel): ", "Item choice");
                if (itemToRemoveInput == null) {
                    pause(scanner);
                    continue;
                }
                int itemToRemove = itemToRemoveInput;

                if (itemToRemove == 0) {
                    continue;
                }
                if (itemToRemove < 1 || itemToRemove > orderItems.size()) {
                    System.out.println(RED + "\nInvalid order item number." + RESET);
                    pause(scanner);
                    continue;
                }

                String removedItem = orderItems.get(itemToRemove - 1);
                order.remove(removedItem);
                System.out.println("\nRemoved " + removedItem + " from the order.");
                pause(scanner);
            } else {
                System.out.println(RED + "\nInvalid item number." + RESET);
                pause(scanner);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Queue<Integer> processingNumbers = new LinkedList<>();
        Queue<Integer> readyNumbers = new LinkedList<>();
        Map<Integer, Map<String, Integer>> orders = new HashMap<>();

        // Dynamic list allowing staff to add or remove items
        List<String> cafe_menu = new ArrayList<>(Arrays.asList(
            "Femboy Milk Shake",
            "Grand Femboy Pudding",
            "Femboy Chicken Thigh",
            "Astolfo Special"
        ));
        Map<String, BigDecimal> itemPrices = new HashMap<>();
        itemPrices.put("Femboy Milk Shake", new BigDecimal("120.00"));
        itemPrices.put("Grand Femboy Pudding", new BigDecimal("95.00"));
        itemPrices.put("Femboy Chicken Thigh", new BigDecimal("150.00"));
        itemPrices.put("Astolfo Special", new BigDecimal("180.00"));

        String[] staff_menu = {
            "See Orders",
            "Edit Order",
            "Deliver Order",
            "Add Food Menu Item",
            "Remove Food Menu Item",
            "Back to Main Menu"
        };

        String[] customer_menu = {
            "Place an Order",
            "Check or Claim Order",
            "Back to Main Menu"
        };

        int order_number = 1;
        boolean runCafe = true;

        do {
            clearScreen();
            System.out.println("\n" + CYAN + "====WELCOME TO FEMBOY MAID CAFE====" + RESET + "\n");
            System.out.println("[1]- Customer");
            System.out.println("[2]- Staff");
            System.out.println("[3]- Exit");
            Integer roleChoiceInput = readInteger(scanner, "\nSelect Option: ", "Menu choice");
            if (roleChoiceInput == null) {
                pause(scanner);
                continue;
            }
            int role_choice = roleChoiceInput;

            switch (role_choice) {
                case 1:
                    boolean runCustomerMenu = true;
                    boolean suppressCustomerPause = false;

                    do {
                        clearScreen();
                        System.out.println("\n" + CYAN + "====CUSTOMER MENU====" + RESET + "\n");

                        for (int i = 0; i < customer_menu.length; i++) {
                            System.out.println("[" + (i + 1) + "]- " + customer_menu[i]);
                        }

                        Integer userInput = readInteger(scanner, "\nSelect Option: ", "Menu choice");
                        if (userInput == null) {
                            pause(scanner);
                            continue;
                        }
                        int user_input = userInput;

                        switch (user_input) {
                            case 1:
                                boolean ordering = true;
                                Map<String, Integer> cart = new LinkedHashMap<>();

                                while (ordering) {
                                    clearScreen();
                                    System.out.println("\n" + CYAN + "====FEMBOY CAFE MENU====" + RESET + "\n");

                                    if (cafe_menu.isEmpty()) {
                                        System.out.println(RED + "The food menu is currently empty. Please check back later!" + RESET);
                                        break;
                                    }

                                    for (int i = 0; i < cafe_menu.size(); i++) {
                                        String item = cafe_menu.get(i);
                                        System.out.println("[" + (i + 1) + "]- " + item + " - "
                                            + formatCurrency(itemPrices.get(item)));
                                    }
                                    System.out.println("[0]- Complete / Edit Order");

                                    if (!cart.isEmpty()) {
                                        System.out.println("\n--- Current Cart ---");
                                        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                                            System.out.println("- " + entry.getKey() + " x" + entry.getValue());
                                        }
                                    }

                                    Integer menuChoiceInput = readInteger(scanner,
                                        "\nSelect Food Item to Order (or 0 for order actions): ", "Menu item choice");
                                    if (menuChoiceInput == null) {
                                        pause(scanner);
                                        continue;
                                    }
                                    int menu_choice = menuChoiceInput;

                                    if (menu_choice == 0) {
                                        boolean finishOrder = handleOrderActionMenu(scanner, cart, cafe_menu, itemPrices);
                                        if (finishOrder) {
                                            ordering = false;
                                        }
                                    } else if (menu_choice >= 1 && menu_choice <= cafe_menu.size()) {
                                        String selectedItem = cafe_menu.get(menu_choice - 1);
                                        Integer quantityInput = readInteger(scanner,
                                            "Enter Quantity for " + selectedItem + ": ", "Quantity");
                                        if (quantityInput == null) {
                                            pause(scanner);
                                            continue;
                                        }
                                        int qty = quantityInput;
                                        if (qty <= 0) {
                                            System.out.println(RED + "\nQuantity must be greater than 0." + RESET);
                                            pause(scanner);
                                            continue;
                                        }

                                        cart.put(selectedItem, cart.getOrDefault(selectedItem, 0) + qty);
                                        System.out.println("\nAdded " + qty + "x " + selectedItem + " to your order!");
                                        pause(scanner);
                                    } else {
                                        System.out.println(RED + "\nItem selected does not exist! Please try again." + RESET);
                                        pause(scanner);
                                    }
                                }

                                if (!cart.isEmpty()) {
                                    processingNumbers.offer(order_number);
                                    orders.put(order_number, new LinkedHashMap<>(cart));
                                    printReceipt(order_number, cart, itemPrices);
                                    order_number++;
                                } else {
                                    System.out.println("\nNo items selected. Order canceled.");
                                }
                                break;

                            case 2:
                                boolean inCheck = true;

                                while (inCheck) {
                                    clearScreen();
                                    System.out.println("\n====ORDER STATUS====\n");

                                    if (processingNumbers.isEmpty() && readyNumbers.isEmpty()) {
                                        System.out.println("No current orders.");
                                        inCheck = false;
                                    } else {
                                        if (!processingNumbers.isEmpty()) {
                                            printOrdersSummary("Orders Being Prepared:", processingNumbers, orders);
                                            if (readyNumbers.isEmpty()) {
                                                // No ready orders to claim; pause once so user can see the view, then return to Customer menu
                                                System.out.println();
                                                pause(scanner);
                                                inCheck = false;
                                                suppressCustomerPause = true;
                                                break;
                                            }
                                        }

                                        if (!readyNumbers.isEmpty()) {
                                            printOrdersSummary("Orders Ready for Pickup:", readyNumbers, orders);

                                            boolean askAgain = true;
                                            while (askAgain) {
                                                // Refresh the screen for each attempt so the view stays clean
                                                clearScreen();
                                                System.out.println("\n====ORDER STATUS====\n");
                                                if (!processingNumbers.isEmpty()) {
                                                    printOrdersSummary("Orders Being Prepared:", processingNumbers, orders);
                                                }
                                                if (!readyNumbers.isEmpty()) {
                                                    printOrdersSummary("Orders Ready for Pickup:", readyNumbers, orders);
                                                }

                                                System.out.print("\nClaim order (yes/no): ");
                                                String action = scanner.next().trim().toLowerCase();
                                                scanner.nextLine();

                                                if (action.equals("yes")) {
                                                    Integer inputNumber = readInteger(scanner, "Input Order No: ", "Order number");
                                                    if (inputNumber == null) {
                                                        System.out.println(RED + "\nOrder number cannot be blank or must be a whole number." + RESET);
                                                        pause(scanner);
                                                        // stay in the inner loop so user can re-enter
                                                        continue;
                                                    }
                                                    int inputNo = inputNumber;

                                                    if (readyNumbers.contains(inputNo)) {
                                                        readyNumbers.remove(inputNo);
                                                        orders.remove(inputNo);
                                                        // Refresh view so the claimed order no longer appears
                                                        clearScreen();
                                                        System.out.println("\n====ORDER STATUS====\n");
                                                        if (!processingNumbers.isEmpty()) {
                                                            printOrdersSummary("Orders Being Prepared:", processingNumbers, orders);
                                                        }
                                                        if (!readyNumbers.isEmpty()) {
                                                            printOrdersSummary("Orders Ready for Pickup:", readyNumbers, orders);
                                                        }
                                                        System.out.println();
                                                        System.out.println("Order #" + inputNo + " Claimed! Thank you!");
                                                        // Pause so user can see the confirmation, then exit back to customer menu
                                                        pause(scanner);
                                                        askAgain = false;
                                                        inCheck = false;
                                                        suppressCustomerPause = true;
                                                    } else {
                                                        System.out.println(RED + "\nIncorrect Order Number or order is not ready yet!" + RESET);
                                                        pause(scanner);
                                                        // allow retry
                                                    }

                                                } else if (action.equals("no")) {
                                                    // Return back to customer menu
                                                    askAgain = false;
                                                    inCheck = false;
                                                } else {
                                                    System.out.println(RED + "\nInvalid Input! Expected 'yes' or 'no'." + RESET);
                                                    pause(scanner);
                                                    // re-prompt
                                                }
                                            }
                                        }
                                    }

                                    if (inCheck) {
                                        pause(scanner);
                                    }
                                }

                                break;

                            case 3:
                                runCustomerMenu = false;
                                break;

                            default:
                                System.out.println(RED + "\nInvalid Choice! Please enter a valid menu option." + RESET);
                                break;
                        }

                        if (runCustomerMenu) {
                            if (suppressCustomerPause) {
                                // consume the suppression once and do not pause this iteration
                                suppressCustomerPause = false;
                            } else {
                                pause(scanner);
                            }
                        }

                    } while (runCustomerMenu);
                    break;

                case 2:
                    boolean runStaffMenu = true;

                    do {
                        clearScreen();
                        System.out.println("\n" + CYAN + "====STAFF MENU====" + RESET + "\n");

                        for (int i = 0; i < staff_menu.length; i++) {
                            System.out.println("[" + (i + 1) + "]- " + staff_menu[i]);
                        }

                        Integer staffInput = readInteger(scanner, "\nSelect Option: ", "Menu choice");
                        if (staffInput == null) {
                            pause(scanner);
                            continue;
                        }
                        int staff_input = staffInput;

                        switch (staff_input) {
                            case 1:
                                if (processingNumbers.isEmpty() && readyNumbers.isEmpty()) {
                                    System.out.println("\nNo current orders.");
                                } else {
                                    if (!processingNumbers.isEmpty()) {
                                        printOrdersSummary("Orders Being Prepared:", processingNumbers, orders);
                                    }
                                    if (!readyNumbers.isEmpty()) {
                                        printOrdersSummary("Orders Ready for Pickup:", readyNumbers, orders);
                                    }
                                }
                                break;

                            case 2:
                                editOrders(scanner, processingNumbers, readyNumbers, orders, cafe_menu, itemPrices);
                                break;

                            case 3:
                                if (processingNumbers.isEmpty()) {
                                    System.out.println("\nNo orders to deliver.");
                                } else {
                                    printOrdersSummary("Pending Orders:", processingNumbers, orders);

                                    Integer orderNumberInput = readInteger(scanner,
                                        "\nEnter order number to deliver (0 to cancel): ", "Order number");
                                    if (orderNumberInput == null) {
                                        break;
                                    }
                                    int orderNumber = orderNumberInput;

                                    if (orderNumber == 0) {
                                        System.out.println("\nDelivery canceled.");
                                    } else if (processingNumbers.remove(orderNumber)) {
                                        Map<String, Integer> order = orders.get(orderNumber);
                                        System.out.println("\nOrder ready for pickup:");
                                        printOrderBlock(orderNumber, order);
                                        // Hardcoded closing separator for this single-order view
                                        System.out.println(ORDER_SEPARATOR);
                                        readyNumbers.offer(orderNumber);
                                    } else {
                                        System.out.println(RED + "\nThat order does not exist or is not pending." + RESET);
                                    }
                                }
                                break;

                            case 4:
                                clearScreen();
                                System.out.println("\n====ADD FOOD MENU ITEM====\n");
                                System.out.print("Enter new item name (0 to cancel): ");
                                String newItem = scanner.nextLine().trim();
                                if (newItem.equals("0")) {
                                    System.out.println("\nAdd item canceled.");
                                } else if (newItem.isEmpty()) {
                                    System.out.println(RED + "\nItem name cannot be empty!" + RESET);
                                } else if (cafe_menu.stream().anyMatch(item -> item.equalsIgnoreCase(newItem))) {
                                    System.out.println(RED + "\nThat menu item already exists." + RESET);
                                } else {
                                    boolean addingItem = true;
                                    while (addingItem) {
                                        BigDecimal newItemPrice = readPrice(scanner,
                                            "Enter price in PHP (0 to cancel): ");
                                        if (newItemPrice == null) {
                                            pause(scanner);
                                            continue;
                                        }
                                        if (newItemPrice.signum() == 0) {
                                            System.out.println("\nAdd item canceled.");
                                        } else {
                                            cafe_menu.add(newItem);
                                            itemPrices.put(newItem, newItemPrice);
                                            System.out.println("\nSuccessfully added '" + newItem
                                                + "' to the food menu at " + formatCurrency(newItemPrice) + ".");
                                        }
                                        addingItem = false;
                                    }
                                }
                                break;

                            case 5:
                                clearScreen();
                                System.out.println("\n====REMOVE FOOD MENU ITEM====\n");
                                if (cafe_menu.isEmpty()) {
                                    System.out.println(RED + "Food menu is currently empty." + RESET);
                                } else {
                                    for (int i = 0; i < cafe_menu.size(); i++) {
                                        String item = cafe_menu.get(i);
                                        System.out.println("[" + (i + 1) + "]- " + item + " - "
                                            + formatCurrency(itemPrices.get(item)));
                                    }
                                    System.out.println("[0]- Cancel");
                                    Integer removeChoiceInput = readInteger(scanner,
                                        "\nEnter item number to remove: ", "Menu item choice");
                                    if (removeChoiceInput == null) {
                                        break;
                                    }
                                    int removeChoice = removeChoiceInput;

                                    if (removeChoice == 0) {
                                        System.out.println("\nRemove item canceled.");
                                    } else if (removeChoice >= 1 && removeChoice <= cafe_menu.size()) {
                                        String removed = cafe_menu.remove(removeChoice - 1);
                                        if (!cafe_menu.contains(removed)) {
                                            itemPrices.remove(removed);
                                        }
                                        System.out.println("\nSuccessfully removed '" + removed + "' from the food menu.");
                                    } else {
                                        System.out.println(RED + "\nInvalid item number selection!" + RESET);
                                    }
                                }
                                break;

                            case 6:
                                runStaffMenu = false;
                                break;

                            default:
                                System.out.println(RED + "\nInvalid Choice! Please enter a valid menu option." + RESET);
                                break;
                        }

                        if (runStaffMenu) {
                            pause(scanner);
                        }

                    } while (runStaffMenu);
                    break;

                case 3:
                    System.out.println("\nGoodbye!");
                    runCafe = false;
                    break;

                default:
                    System.out.println(RED + "\nInvalid choice! Please select a valid main menu option." + RESET);
                    pause(scanner);
                    break;
            }

        } while (runCafe);

        scanner.close();
    }
}