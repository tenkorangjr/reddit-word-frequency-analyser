public class BSTMapTests {
    public static void bstTests(){
        {
            /*
             * Test constructor and size() method
             */
            //Setup
            BSTMap<String, Integer> map = new BSTMap<>();

            //Verify
            System.out.println(map + " == ");
            System.out.println(map.size() + " == 0");

            //Test
            assert map != null;
            assert map.size() == 0;
        }

        {
            /*
             * Test put() and get() method
             */
            //Setup
            BSTMap<String, Integer> map1 = new BSTMap<>();
            BSTMap<String, Integer> map2 = new BSTMap<>();

            map1.put("Michael", 10);
            map1.put("Professor", 5);
            map1.put("Tenkorang", 20);

            map2.put("Michael", 10);
            map2.put("Professor", 5);
            int oldVal = map2.put("Michael", 20);


            // Verify
            System.out.println(map1.get("Michael") + " == 10");
            System.out.println(map2.get("Michael") + " == 20");
            System.out.println(oldVal + " == 10");

            //Test
            assert oldVal != 10 : "Put return not working";
            assert map1.put("null", 20) == null: "Put return statement not working";
        }

        {
            /*
             * Test contains method
             */

             // Setup
            BSTMap<String, Integer> map = new BSTMap<>();
            map.put("Desmond", 20);
            map.put("Christian", 40);
            map.put("Delanyo", 60);

            // Verify
            System.out.println(map.containsKey("Delanyo") + " == true");
            System.out.println(map.containsKey("delanyo") + " == false");
            System.out.println(map.containsKey("Desmond") + " == true");

            // Test
            assert map.containsKey("Delanyo") == true: "Faulty contains method";
            assert map.containsKey("delanyo") != true: "Faulty contains method";

        }

        {
            /*
             * Test remove method
             */

             // Setup
            BSTMap<String, Integer> map = new BSTMap<>();
            map.put("Desmond", 20);
            map.put("Christian", 40);
            map.put("Delanyo", 60);

            // Verify
            System.out.println(map.remove("Delanyo") + " == 60");
            System.out.println(map.containsKey("delanyo") + " == false");
            System.out.println(map.containsKey("desmond") + " == false");

            // Test
            assert map.remove("Christian") == 40: "Faulty remove method";
            assert map.containsKey("Christian") == true: "remove() does not remove item from map";
        }

        {
            /*
             * Test values(), keySet() & entrySet() methods
             */

             // Setup
            BSTMap<String, Integer> map = new BSTMap<>();
            map.put("Dog", 21);
            map.put("Bat", 12);
            map.put("Sheep", 9);
            map.put("Elephant", 67);

            // Verify
            System.out.println(map.values() + " == {12, 21, 9, 67}");
            System.out.println(map.keySet() + " == {'Dog', 'Bat', 'Sheep', 'Elephant'}");
            System.out.println(map.entrySet() + " == {'<Dog -> 21>', '<Bat -> 12>', '<Sheep -> 9>', '<Elephant -> 67>'}");

            // Test
            assert map.values() != null: "Check return for values()";
            assert map.keySet() != null: "Check return for keySet()";
            assert map.entrySet() != null: "Check return for entrySet()";
    }

        {
            /*
             * Test clear() and maxDepth() methods
             */

            // Setup
            BSTMap<String, Integer> map = new BSTMap<>();
            map.put("Dog", 21);
            map.put("Bat", 12);
            map.put("Sheep", 9);
            map.put("Elephant", 67);
            map.clear();

            // Verify
            System.out.println(map.size() + " == 0");
            System.out.println(map.maxDepth() + " == 0");

            // Test
            assert map.values().size() == 0: "clear() faulty";
        }
    }

    public static void main(String[] args) {
        bstTests();
    }
}
