public class Space extends Locale {

   //
   // -- PRIVATE --
   //

   // Constructor
   public Space(int id) {
      super(id);
   }

   // Getters and Setters
   public void setNearestPlanet(String value) {
      this.nearestPlanet = value;
   }
   public String getNearestPlanet() {
      return this.nearestPlanet;
   }

   // Other methods
   @Override
   public String toString() {
      return "Space..." + super.toString() + " nearestPlanet=" + this.nearestPlanet;
   }


   //
   // -- PRIVATE --
   //
   private String nearestPlanet;

}