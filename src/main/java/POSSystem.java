import java.util.Dictionary;
import java.util.HashMap;


 public class POSSystem {

    private HashMap<String, POSItem> availableItemMap = new HashMap();
    private HashMap<String, POSItem> currentScannedItems = new HashMap();


    public void POSSystem() {

    }

    /* POSItem variables
        double markdownAmount - defaults to 0
        int special - defaults to 0
        double price - defaults to 0

        adding item not in hashmap will result in item being added, adding item
        currently in hashmap will result in item being updated with passed data.
     */
    public void addOrUpdateScannableItem(String name, POSItem item) {
        if(!availableItemMap.containsKey(name)) {
            availableItemMap.put(name, item);
        } else {
            removeScannableItem(name);
            availableItemMap.put(name, item);
        }

    }

    /* removes an item from the possible scannable items list */
    public void removeScannableItem(String name) {
        availableItemMap.remove(name);
    }

    public POSItem getItemInfo(String name) {
        return availableItemMap.get(name);
    }

    public void voidScannedItem(String item, double weight) {
        POSItem currentItem = currentScannedItems.get(item);
        currentItem.quantity = currentItem.quantity - 1;
        currentItem.totalWeight = currentItem.totalWeight - weight;

        currentScannedItems.put(item, currentItem);
    }

    /* Tracks total quantity or weight per item, and applies any markdowns available.  Once complete
        it adds the item to the currentScannedItems hashmap.
     */
     public void scanItem(String item, double weight) {
        POSItem currentItem = availableItemMap.get(item);
        currentItem.quantity = currentItem.quantity + 1;
        currentItem.totalWeight = weight + currentItem.totalWeight;
        if (!currentItem.markdownApplied) {
            currentItem.price = (currentItem.price - currentItem.markdownAmount);
            currentItem.markdownApplied = true;
        }
        currentScannedItems.put(item, currentItem);
    }

    /* Tracks current total for all scanned items, including markdowns and specials */
    public double getCurrentTotal() {
        double total = 0;
        for (POSItem item : currentScannedItems.values()) {
            if(item.totalWeight > 0) {
                if(item.special != 0) {
                    total = applySpecial(item) + total;

                } else {
                    total = (item.price * item.totalWeight) + total;
                }
            } else {
                if(item.special != 0) {
                    total = applySpecial(item) + total;
                } else {
                    total = (item.price*item.quantity) + total;
                }

            }

        }
        return total;
    }

    /* Applies the special assigned at the item.special property */
    public double applySpecial(POSItem item) {

        /* intialize total to the regular price and total amount in event of special not being valid */
        double total = (item.totalWeight > 0) ? item.totalWeight*item.price : item.quantity*item.price;
        int result = 0;
        /* apply the special limit to the quantity or weight */
        int specialQuantity = (item.quantity >= item.specialLimit && item.specialLimit > 0) ? item.specialLimit : item.quantity;
        double specialWeight = (item.totalWeight >= item.specialLimit && item.specialLimit > 0) ? item.specialLimit : item.totalWeight;
        switch(item.special) {
            /* Special Case 1 - Buy (currentItem.specialBuyCount), Get (currentItem.specialGetCount) at
                (currentItem.specialDiscount) percentage off */
            case 1:

                if(specialQuantity >= (item.specialBuyCount + item.specialGetCount)) {

                    result = specialQuantity / (item.specialBuyCount + item.specialGetCount);
                    total = result*(item.price-(item.price*item.specialDiscount)) + (item.quantity-result)*item.price;
                }
                break;
                /* Special Case 2 - Buy (currentItem.specialBuyCount) at
                (currentItem.specialDiscount) discounted price. */
            case 2:
                result = specialQuantity / item.specialBuyCount;
                int regular = item.quantity - (result * item.specialBuyCount);
                if(result > 0) {
                    total = result * item.specialDiscount + (regular * item.price);
                }
                break;
            case 3:
                /* Special Case 3 - Buy (currentItem.specialBuyCount), Get (currentItem.specialGetCount) at
                (currentItem.specialDiscount) percentage off by weight */
                double weight = item.totalWeight;
                if(specialWeight >= (item.specialBuyCount + item.specialGetCount)) {
                    result = (int)specialWeight / (item.specialBuyCount + item.specialGetCount);
                    total = result*(item.price - (item.price*item.specialDiscount)) + (item.totalWeight-result)*item.price;
                }
                break;
            default:
                return 0;
        }
        return total;
    }
}

