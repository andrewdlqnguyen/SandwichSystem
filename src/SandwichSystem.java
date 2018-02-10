import java.util.*;

/* Assignment 2
 * @author Andrew Nguyen
 * @studentID 100893165
 * 
 * READ ME describes the objective and how the program works.
 */
public class SandwichSystem {
	
	// Initialize 4 unique threads: agents, chef 1-3
	static Thread agent_1, chef_BreadMaker, chef_PeanutButterSpreader, chef_JamSpreader;

	// The program will create 4 threads, chefs with bread, peanut butter, jam, and an agent that has all 3
	// main will create a SandwichMaking class called kitchen that'll do all the synchronization
	public static void main(String[] args) throws InterruptedException {
		
		String BREAD = "bread";
		String PEANUT_BUTTER = "peanut butter";
		String JAM = "jam";
		final SandwichMaking kitchen = new SandwichMaking(BREAD, PEANUT_BUTTER, JAM); 
		
		agent_1 = new Thread(new Runnable() 						// Creating an Agent Producer
        {
            @Override
            public void run()
            {
                try
                {
                    kitchen.put();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
		chef_BreadMaker = new Thread(new Runnable() 				// Creating a Chef Producer/Consumer for Bread
        {
			String name = "chef_BreadMaker";
			String ingredient = "bread";
            @Override
            public void run()
            {
                try
                {
                    kitchen.get(name, ingredient);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
		chef_PeanutButterSpreader = new Thread(new Runnable() 		// Creating a Chef Producer/Consumer for PeanutButter Spreading
		{
			String name = "chef_PeanutButterSpreader";
			String ingredient = "peanut butter";
            @Override
            public void run()
            {
                try
                {
                    kitchen.get(name, ingredient);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
		chef_JamSpreader = new Thread(new Runnable() 				// Creating a Chef Producer/Consumer for Jam Spreading
		{
			String name = "chef_JamSpreader";
			String ingredient = "jam";
            @Override
            public void run()
            {
                try
                {
                    kitchen.get(name, ingredient);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
		
		// Start the threads
		agent_1.start();
		chef_BreadMaker.start();
		chef_PeanutButterSpreader.start();
		chef_JamSpreader.start();
		

	}
	
	/* SandwichMaking class is where the threads make use of the synchronization and locking procedure.
	*  When an agent has selected two random ingredients, the chefs thread will be selected at random:
	*  Two conditions: if satisfied, notify all and a new agent is chosen - repeat, if not, current chef will be sent to wait set and a new chef will get chosen at random - repeat
	*/
	public static class SandwichMaking {
		
		private boolean empty = true; 								// Check if the table has any ingredients
		private int count = 1; 										// Program runs until 20 sandwiches have been made and consumed
		
		ArrayList<String> foodList = new ArrayList<String>(); 		// Initialize default food list
		ArrayList<String> list = new ArrayList<String>(); 			// temporary list to use to check for what ingredient is needed for the sandwich
		Random rand = new Random(); 								// Random generator
		
		public SandwichMaking(String b, String p, String j) {
			foodList.add(b);
			foodList.add(p);
			foodList.add(j);
		}
		
		// Select the two random ingredients and put it in the list of food
		public void random() {
			list.clear();
			for (int i = 0; i < 2; i++) {
				int randomIndex = rand.nextInt(foodList.size());
				String randomElement = foodList.get(randomIndex);
				foodList.remove(randomElement);
				list.add(randomElement);
			}
			foodList.addAll(list); 									// Refill foodList to default list

		}
		
		// This Method is used specifically for the agent since the agent is only putting in ingredients on the table
		public void put() throws InterruptedException {
			
			while(count < 20) {
				synchronized(this) {
					while (!empty) {
						wait();
					}
					random();
					System.out.println("========================================");
					System.out.println(count + ". The agent left two ingredients on the table: " + list);
					empty = false;
					notifyAll();
				}
			}	
		}
		
		// This Method is used for the chefs since the chef in the end will eat the sandwich and reset the loop.
		public void get(String name, String ingredient) throws InterruptedException {
			
			String chefName = name;
			String chefIngredient = ingredient;
			
			while(count < 20){
				synchronized(this) {
					while (empty) {
						wait();
					}
					if ((list.contains("bread") && (list.contains("peanut butter") && (chefName == "chef_JamSpreader")))) {
						System.out.println("The " + chefName + " spreads on " + chefIngredient + " to finish the sandwich \n" + "...And then he eats it \n");
						empty = true;
						notifyAll();
						count++;
					} else if ((list.contains("bread") && (list.contains("jam") && (chefName == "chef_PeanutButterSpreader")))) {
						System.out.println("The " + chefName + " spreads on " + chefIngredient + " to finish the sandwich \n" + "...And then he eats it \n");
						empty = true;
						notifyAll();
						count++;
					} else if ((list.contains("peanut butter") && (list.contains("jam") && (chefName == "chef_BreadMaker")))) {
						System.out.println("The " + chefName + " uses his " + chefIngredient + " to finish the sandwich \n" + "...And then he eats it \n");
						empty = true;
						notifyAll();
						count++;
					}				
				}		
			}
		}
	}
}
