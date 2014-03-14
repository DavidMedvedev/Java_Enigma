public class ItemMan {

    //
    // Public
    //

    // Constructor
    public ItemMan(int id){

    }

    // Getters and Setters
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDesc() {return desc;}
    public void setDesc(String desc) {this.desc = desc;}

    public Boolean getFound() {return found;}

    public boolean itemFound() {return found;}

    public void setFound(boolean found){this.found = found;}

    public void setFound(Boolean found) {this.found = found;}

    public String toString(){return this.name +": "+ this.desc;}



    /*
    public Items getNext() {return next;}
    public void setNext(Items next) {
        this.next = next;
    }
    */

    // Other methods
   // @Override
    //public String toString() {
    //    return super.toString() + " name=" + this.name + " desc=" + this.desc;
    //}


    //
    // Private
    //
    private int id;
    private String name;
    private String desc;
    private Boolean found = false;
    private ItemMan next = null;

}