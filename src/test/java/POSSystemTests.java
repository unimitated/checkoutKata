import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class POSSystemTests {

    private POSSystem posSystem;

    @BeforeEach
    void setUp() {
        posSystem = new POSSystem();

    }

    @Test
    void addScannableItem() {
        POSItem item = new POSItem();
        item.name = "Bread";
        posSystem.addOrUpdateScannableItem("Bread", item);
        Assertions.assertEquals(posSystem.getItemInfo("Bread").name, item.name);
    }

    @Test
    void removeScannableItem() {
        addScannableItem();
        posSystem.removeScannableItem("Bread");
        Assertions.assertEquals(posSystem.getItemInfo("Bread"), null);
    }

    @Test
    void whenAUnitItemHasAMarkdown() {
        POSItem item = new POSItem();

        item.price = 4.00;
        item.markdownAmount = 2.00;
        posSystem.addOrUpdateScannableItem("Beef", item);
        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);

        Assertions.assertEquals(4.00, posSystem.getCurrentTotal());
    }

    @Test
    void whenAWeighedItemHasAMarkdown() {
        POSItem item = new POSItem();

        item.price = 4.00;
        item.markdownAmount = 2.00;
        posSystem.addOrUpdateScannableItem("Beef", item);
        posSystem.scanItem("Beef", 3.0);
        posSystem.scanItem("Beef", 2.0);

        Assertions.assertEquals(10.00, posSystem.getCurrentTotal());
    }

    @Test
    void whenAItemIsScannedItAddsToTheTotal() {
        POSItem item = new POSItem();
        POSItem item2 = new POSItem();

        item.price = 3.99;
        posSystem.addOrUpdateScannableItem("Beef", item);
        posSystem.scanItem("Beef", 0);
        item2.price = 5.01;
        posSystem.addOrUpdateScannableItem("Fish", item2);
        posSystem.scanItem("Fish", 0);
        Assertions.assertEquals(9.00, posSystem.getCurrentTotal());
    }

    @Test
    void whenAItemAndWeightIsScannedItAddsToTheTotal() {
        POSItem item = new POSItem();
        POSItem item2 = new POSItem();

        item.price = 3.99;
        posSystem.addOrUpdateScannableItem("Beef", item);
        posSystem.scanItem("Beef", 0);
        item2.price = 5.01;
        posSystem.addOrUpdateScannableItem("Fish", item2);
        posSystem.scanItem("Fish", 3.0);

        Assertions.assertEquals(19.02, posSystem.getCurrentTotal());
    }

    @Test
    void whenAUnitItemIsRemovedTheTotalIsUpdated() {
        whenAItemIsScannedItAddsToTheTotal();
        posSystem.voidScannedItem("Beef", 0);

        Assertions.assertEquals(5.01, posSystem.getCurrentTotal());
    }

    @Test
    void whenAWeighedItemIsRemovedTheTotalIsUpdated() {
        whenAItemAndWeightIsScannedItAddsToTheTotal();
        posSystem.voidScannedItem("Fish", 2);

        Assertions.assertEquals(9.00, posSystem.getCurrentTotal());
    }

    @Test
    void whenABuyGetDiscountIsApplied() {
        POSItem item = new POSItem();

        item.price = 8.00;
        item.special = 1;
        item.specialBuyCount = 3;
        item.specialGetCount = 1;
        item.specialDiscount = .50;
        posSystem.addOrUpdateScannableItem("Beef", item);
        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);

        Assertions.assertEquals(28, posSystem.getCurrentTotal());
    }

    @Test
    void whenABuyGetDiscountIsAppliedIsRemoved() {
        whenABuyGetDiscountIsApplied();
        posSystem.voidScannedItem("Beef", 0);

        Assertions.assertEquals(24, posSystem.getCurrentTotal());
    }

    @Test
    void whenABuyXForNDiscountIsApplied() {
        POSItem item = new POSItem();

        item.price = 8.00;
        item.special = 2;
        item.specialBuyCount = 3;
        item.specialDiscount = 5.00;
        posSystem.addOrUpdateScannableItem("Beef", item);
        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);

        Assertions.assertEquals(13, posSystem.getCurrentTotal());

        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);

        Assertions.assertEquals(10, posSystem.getCurrentTotal());

    }

    @Test
    void whenABuyXForNDiscountIsRemoved() {
        whenABuyXForNDiscountIsApplied();
        posSystem.voidScannedItem("Beef", 0);

        Assertions.assertEquals(21, posSystem.getCurrentTotal());

    }

    @Test
    void whenABuyXGetNAtYWeighedDiscountIsApplied() {
        POSItem item = new POSItem();

        item.price = 8.00;
        item.special = 3;
        item.specialBuyCount = 2;
        item.specialGetCount = 1;
        item.specialDiscount = .75;
        posSystem.addOrUpdateScannableItem("Beef", item);
        posSystem.scanItem("Beef", 6);
        posSystem.scanItem("Beef", 1);

        Assertions.assertEquals(44, posSystem.getCurrentTotal());

    }

    @Test
    void whenABuyXGetNAtYWeighedDiscountIsRemoved() {
        whenABuyXGetNAtYWeighedDiscountIsApplied();
        posSystem.voidScannedItem("Beef", 3);

        Assertions.assertEquals(26, posSystem.getCurrentTotal());

    }

    @Test
    void whenASpecialLimitExistsPerUnit() {
        POSItem item = new POSItem();

        item.price = 8.00;
        item.special = 2;
        item.specialBuyCount = 2;
        item.specialDiscount = 5.00;
        item.specialLimit = 4;
        posSystem.addOrUpdateScannableItem("Beef", item);
        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);

        Assertions.assertEquals(10, posSystem.getCurrentTotal());

        posSystem.scanItem("Beef", 0);
        posSystem.scanItem("Beef", 0);

        Assertions.assertEquals(26, posSystem.getCurrentTotal());

    }

    @Test
    void whenASpecialLimitExistsPerWeight() {
        POSItem item = new POSItem();

        item.price = 8.00;
        item.special = 3;
        item.specialBuyCount = 2;
        item.specialGetCount = 1;
        item.specialDiscount = .50;
        item.specialLimit = 6;
        posSystem.addOrUpdateScannableItem("Beef", item);
        posSystem.scanItem("Beef", 6);

        Assertions.assertEquals(40, posSystem.getCurrentTotal());

        posSystem.scanItem("Beef", 1);

        Assertions.assertEquals(48, posSystem.getCurrentTotal());

    }
}