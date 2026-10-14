# Femboy Maid Cafe

A Java console application that simulates ordering and order management for a themed cafe. Customers can browse the menu, build or edit a cart, place orders, view their receipt, and check or claim orders when they are ready. Staff can view and edit orders, mark orders ready for pickup, and add or remove menu items.

The menu and orders are kept in memory while the program is running. They reset when the application exits.

## Requirements

- A JDK (Java Development Kit) installed. JDK 8 or newer is suitable.
- Visual Studio Code.
- The **Extension Pack for Java** installed in VS Code.

## Download the project

1. Open this repository on GitHub.
2. Click the **<> Code** button near the top of the repository page.
3. Select **Download ZIP** from the menu.
4. Once the ZIP file has downloaded, extract it to a folder on your computer.
5. In VS Code, choose **File > Open Folder...** and open the extracted project folder.

## Run in VS Code

1. Install a JDK and the **Extension Pack for Java** from the VS Code Extensions view.
2. Open the project folder in VS Code if it is not already open.
3. Open `femboy_maid_cafe.java`.
4. If VS Code asks to configure the Java project, allow it. The Java extension will detect the file and prepare it for running.
5. Click the **Run** button at the top-right of the editor, or go to **Run > Run Without Debugging** in the menu bar.
6. If prompted, choose the Java entry point for the project and start the program.

This is the easiest way to launch the app in VS Code. Once it starts, use the terminal panel to interact with the program by entering the menu number for the action you want to choose.

If you prefer running it manually from the terminal instead of the editor buttons, you can still compile and launch it with:

```powershell
javac femboy_maid_cafe.java
java femboy_maid_cafe
```

## Features

- Customer and staff menus.
- A default menu with four food and drink items and PHP prices.
- Cart creation, quantity updates, item removal, and order receipts.
- Order status workflow: pending preparation, ready for pickup, and claimed.
- Staff controls for editing orders and adding or removing menu items.
- Validation for numeric input, prices, quantities, and menu selections.

## Project files

- `femboy_maid_cafe.java` - application source code.
- `receipt-sample.jfif` - sample receipt image.
