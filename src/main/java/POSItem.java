

public class POSItem {
     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public double getMarkdownAmount() {
          return markdownAmount;
     }

     public void setMarkdownAmount(double markdownAmount) {
          this.markdownAmount = markdownAmount;
     }

     public boolean isMarkdownApplied() {
          return markdownApplied;
     }

     public void setMarkdownApplied(boolean markdownApplied) {
          this.markdownApplied = markdownApplied;
     }

     public int getSpecial() {
          return special;
     }

     public void setSpecial(int special) {
          this.special = special;
     }

     public int getSpecialBuyCount() {
          return specialBuyCount;
     }

     public void setSpecialBuyCount(int specialBuyCount) {
          this.specialBuyCount = specialBuyCount;
     }

     public int getSpecialGetCount() {
          return specialGetCount;
     }

     public void setSpecialGetCount(int specialGetCount) {
          this.specialGetCount = specialGetCount;
     }

     public double getSpecialDiscount() {
          return specialDiscount;
     }

     public void setSpecialDiscount(double specialDiscount) {
          this.specialDiscount = specialDiscount;
     }

     public int getSpecialLimit() {
          return specialLimit;
     }

     public void setSpecialLimit(int specialLimit) {
          this.specialLimit = specialLimit;
     }

     public double getPrice() {
          return price;
     }

     public void setPrice(double price) {
          this.price = price;
     }

     public int getQuantity() {
          return quantity;
     }

     public void setQuantity(int quantity) {
          this.quantity = quantity;
     }

     public double getTotalWeight() {
          return totalWeight;
     }

     public void setTotalWeight(double totalWeight) {
          this.totalWeight = totalWeight;
     }

     private String name = "";
     private double markdownAmount = 0;
     private boolean markdownApplied = false;

     private int special = 0;
     private int specialBuyCount = 0;
     private int specialGetCount = 0;
     private double specialDiscount = 0;
     private int specialLimit = 0;

     private double price = 0;
     private int quantity = 0;
     private double totalWeight = 0;

}
