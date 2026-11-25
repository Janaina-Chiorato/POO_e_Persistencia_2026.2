

import java.util.Scanner;
import view.MenuPrincipal;

public class Main {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            MenuPrincipal menu = new MenuPrincipal(scanner);

            menu.iniciar();
        }
    }
}