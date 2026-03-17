package org.restaurant.controller.menu;
import java.util.*;
public class MenuController {
	private Scanner scanner;

	public MenuController(Scanner scanner) {
		this.scanner = scanner;
	}
	public void showmenu() {
		while(true) {
			 System.out.println("\n--- MENU ---");
	            System.out.println("1. Morning Menu");
	            System.out.println("2. Afternoon Menu");
	            System.out.println("3. Night Menu");
	            System.out.println("0. Back");
	            System.out.print("Choice: ");
	            
	            int choice  = scanner.nextInt();
	            scanner.nextLine();
	            
	            switch(choice) {
	            case 1 -> showMorningMenu();
	            case 2 -> showAfternoonMenu();
                case 3 -> showNightMenu();
                case 4 -> showsnacks();
                case 0 -> {
                    System.out.println("Returning...");
                    return;
                }
                default -> System.out.println("Invalid choice");
	            }
		}
	}
	private void showMorningMenu(){
		System.out.println("\n--- Morning Menu ---");
		System.out.println("1. Idly");
		System.out.println("2. Dosa");
		System.out.println("3. Pongal");
		System.out.println("4. Mini tiffin");
		System.out.println("5. Sambar vada");
		System.out.println("6. Poori");
		System.out.println("7. Chapati");
	}
	private void showAfternoonMenu(){
		System.out.println("\n--- Afternoon Menu ---");
		System.out.println("1. Meals");
		System.out.println("2. Veg briyani");
		System.out.println("3. Tomato rice");
		System.out.println("4. Curd rice");
		System.out.println("5. Chicken Briyani");
		System.out.println("6. Mutton briyani");
		System.out.println("7. Kuska");
		System.out.println("8. Chicken gravy");
		System.out.println("9. Paneer briyani");
		System.out.println("10. mushroom briyani");
	}
	private void showNightMenu(){
		 System.out.println("\n--- Night Menu ---");
		System.out.println("1. Idly");
		System.out.println("2. Dosa");
		System.out.println("3. Parotta");
		System.out.println("4. Noodels");
		System.out.println("5. Fried Rice");
		System.out.println("6. Egg roast");
		System.out.println("7. Chicken kothu parotta");
		System.out.println("8. chola poori");
	}
	private void showsnacks() {
		System.out.println("\n--- Snacks Menu ---");
	    System.out.println("1. Samosa");
	    System.out.println("2. Bajji");
	    System.out.println("3. Vada");
	    System.out.println("4. Sandwich");
	}
	
	
}
