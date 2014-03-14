import java.io.*;
import java.util.Scanner;

public class ListTester {

    public static void main(String[] args) {
        System.out.println("List tester.");

        // Make the list manager.
        ListMan lm1 = new ListMan();
        lm1.setName("Magic Items");
        lm1.setDesc("These are some of my favorite things.");

        // Make some list items.
        ListItem item1 = new ListItem();
        item1.setName("+2 ring");
        item1.setDesc("precious");
        item1.setCost(42.2112);
        item1.setNext(null);  // Redundant, but safe.

        ListItem item2 = new ListItem();
        item2.setName("Cloak of Doom");
        item2.setDesc("Scary");
        item2.setCost(666);
        item2.setNext(null); // Still redundant. Still safe.

        ListItem item3 = new ListItem();
        item3.setName("broad sword");
        item3.setDesc("sharp");
        item3.setCost(12);
        item3.setNext(null); // Still redundant. Still safe.

        // Put items in the list.
        lm1.add(item1);
        lm1.add(item2);
        lm1.add(item3);

        final String fileName = "magic.txt";

        readMagicItemsFromFileToList(fileName, lm1);
        // Display the list of items.
        System.out.println(lm1.toString());

        // Declare an array for the items.
        ListItem[] items = new ListItem[10];
        readMagicItemsFromFileToArray(fileName, items);
        // Display the array of items.
        System.out.println("Items in the array:");
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                System.out.println(items[i].toString());
            }
        }

        // Ask player for an item.
        Scanner inputReader = new Scanner(System.in);
        System.out.print("What item would you like? ");
        String targetItem = new String();
        targetItem = inputReader.nextLine();
        System.out.println();

        ListItem li = new ListItem();
        li = sequentialSearch(lm1, targetItem);
        if (li != null) {
            System.out.println(li.toString());
        }
    }

    //
    // Private
    //
    private static ListItem sequentialSearch(ListMan lm,
                                             String target) {
        ListItem retVal = null;
        System.out.println("Searching for " + target + ".");
        int counter = 0;
        ListItem currentItem = new ListItem();
        currentItem = lm.getHead();
        boolean isFound = false;
        while ( (!isFound) && (currentItem != null) ) {
            counter = counter +1;
            if (currentItem.getName().equalsIgnoreCase(target)) {
                // We found it!
                isFound = true;
                retVal = currentItem;
            } else {
                // Keep looking.
                currentItem = currentItem.getNext();
            }
        }
        if (isFound) {
            System.out.println("Found " + target + " after " + counter + " comparisons.");
            return  currentItem;
        } else {
            System.out.println("Could not find " + target + " in " + counter + " comparisons.");
        }

        return retVal;
    }


    private static void readMagicItemsFromFileToList(String fileName,
                                                     ListMan lm) {
        File myFile = new File(fileName);
        try {
            Scanner input = new Scanner(myFile);
            while (input.hasNext()) {
                // Read a line from the file.
                String itemName = input.nextLine();

                // Construct a new list item and set its attributes.
                ListItem fileItem = new ListItem();
                fileItem.setName(itemName);
                fileItem.setCost(Math.random() * 100);
                fileItem.setNext(null); // Still redundant. Still safe.

                // Add the newly constructed item to the list.
                lm.add(fileItem);
            }
            // Close the file.
            input.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. " + ex.toString());
        }

    }

    private static void readMagicItemsFromFileToArray(String fileName,
                                                      ListItem[] items) {
        File myFile = new File(fileName);
        try {
            int itemCount = 0;
            Scanner input = new Scanner(myFile);

            while (input.hasNext() && itemCount < items.length) {
                // Read a line from the file.
                String itemName = input.nextLine();

                // Construct a new list item and set its attributes.
                ListItem fileItem = new ListItem();
                fileItem.setName(itemName);
                fileItem.setCost(Math.random() * 100);
                fileItem.setNext(null); // Still redundant. Still safe.

                // Add the newly constructed item to the array.
                items[itemCount] = fileItem;
                itemCount = itemCount + 1;
            }
            // Close the file.
            input.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. " + ex.toString());
        }
    }

}