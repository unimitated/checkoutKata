package com.unimitated.checkout;

import java.util.concurrent.ConcurrentHashMap;
import com.unimitated.checkout.POSItem;



public class POSSystem {

    private ConcurrentHashMap<String, POSItem> availableItemMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, POSItem> currentScannedItems = new ConcurrentHashMap<>();


    public void POSSystem() {

    }

    /* POSItem variables
        double markdownAmount - defaults to 0
        int special - defaults to 0
        double price - defaults to 0

        adding item not in hashmap will result in item being added, adding item
        currently in hashmap will result in item being updated with passed data.
     */

    /**
     *
     * @param name of item being added.
     * @param item POSItem to be added to the HashMap.
     */
    public void addOrUpdateScannableItem(String name, POSItem item) {
            availableItemMap.put(name, item);
    }

    /**
     *
     * @param name of the item to be removed.
     */
    /* removes an item from the possible scannable items list */
    public void removeScannableItem(String name) {
        availableItemMap.remove(name);
    }

    /**
     *
     * @param name name of item to get information about.
     * @return the POSItem object.
     */
    public POSItem getItemInfo(String name) {
        return availableItemMap.get(name);
    }

    /**
     *
     * @param item name of item to be voided.
     * @param weight of item to be voided, pass 0 for by unit items.
     */
    public void voidScannedItem(String item, double weight) {
        POSItem currentItem = currentScannedItems.get(item);
        currentItem.setQuantity(currentItem.getQuantity() - 1);
        currentItem.setTotalWeight(currentItem.getTotalWeight() - weight);

        currentScannedItems.put(item, currentItem);
    }

    /**
     *
     * @param item name of item to be scanned.
     * @param weight of item to be scanned, pass 0 for by unit items.
     */
    /* Tracks total quantity or weight per item, and applies any markdowns available.  Once complete
        it adds the item to the currentScannedItems hashmap.
     */
    public void scanItem(String item, double weight) {
        POSItem currentItem = availableItemMap.get(item);
        currentItem.setQuantity(currentItem.getQuantity() + 1);
        currentItem.setTotalWeight(weight + currentItem.getTotalWeight());
        if (!currentItem.isMarkdownApplied()) {
            currentItem.setPrice((currentItem.getPrice() - currentItem.getMarkdownAmount()));
            currentItem.setMarkdownApplied(true);
        }
        currentScannedItems.put(item, currentItem);
    }

    /**
     *
     * @return returns a total for all currently scanned items.
     */
    /* Tracks current total for all scanned items, including markdowns and specials */
    public double getCurrentTotal() {
        double total = 0;
        for (POSItem item : currentScannedItems.values()) {
            if(item.getTotalWeight() > 0) {
                if(item.getSpecial() != 0) {
                    total = applySpecial(item) + total;

                } else {
                    total = (item.getPrice() * item.getTotalWeight()) + total;
                }
            } else {
                if(item.getSpecial() != 0) {
                    total = applySpecial(item) + total;
                } else {
                    total = (item.getPrice() * item.getQuantity()) + total;
                }

            }

        }
        return total;
    }

    /**
     *
     * @param item the item to test the special discount against.
     * @return the cost of the special item.
     */
    /* Applies the special assigned at the item.special property */
    private double applySpecial(POSItem item) {

        /* intialize total to the regular price and total amount in event of special not being valid */
        double total = (item.getTotalWeight() > 0) ? item.getTotalWeight() * item.getPrice() : item.getQuantity() * item.getPrice();
        int result = 0;
        /* apply the special limit to the quantity or weight */
        int specialQuantity = (item.getQuantity() >= item.getSpecialLimit() && item.getSpecialLimit() > 0) ? item.getSpecialLimit() : item.getQuantity();
        double specialWeight = (item.getTotalWeight() >= item.getSpecialLimit() && item.getSpecialLimit() > 0) ? item.getSpecialLimit() : item.getTotalWeight();
        switch(item.getSpecial()) {
            /* Special Case 1 - Buy (currentItem.specialBuyCount), Get (currentItem.specialGetCount) at
                (currentItem.specialDiscount) percentage off */
            case 1:

                if(specialQuantity >= (item.getSpecialBuyCount() + item.getSpecialGetCount())) {

                    result = specialQuantity / (item.getSpecialBuyCount() + item.getSpecialGetCount());
                    total = result*(item.getPrice() -(item.getPrice() * item.getSpecialDiscount())) + (item.getQuantity() -result)* item.getPrice();
                }
                break;
                /* Special Case 2 - Buy (currentItem.specialBuyCount) at
                (currentItem.specialDiscount) discounted price. */
            case 2:
                result = specialQuantity / item.getSpecialBuyCount();
                int regular = item.getQuantity() - (result * item.getSpecialBuyCount());
                if(result > 0) {
                    total = result * item.getSpecialDiscount() + (regular * item.getPrice());
                }
                break;
            case 3:
                /* Special Case 3 - Buy (currentItem.specialBuyCount), Get (currentItem.specialGetCount) at
                (currentItem.specialDiscount) percentage off by weight */
                double weight = item.getTotalWeight();
                if(specialWeight >= (item.getSpecialBuyCount() + item.getSpecialGetCount())) {
                    result = (int)specialWeight / (item.getSpecialBuyCount() + item.getSpecialGetCount());
                    total = result*(item.getPrice() - (item.getPrice() * item.getSpecialDiscount())) + (item.getTotalWeight() -result)* item.getPrice();
                }
                break;
            default:
                return 0;
        }
        return total;
    }
}

