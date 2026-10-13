import java.util.Scanner;
import java.util.InputMismatchException;

public class ExemploTratado{
    public static void main(String[] args){
        Scanner entrada = new Scanner(System.in);
        boolean entradaValida = false;
        while(!entradaValida){
            try {
            System.out.print("Informe o código do Produto (Inteiro)"); 
            int codigo = entrada.nextInt();
            System.out.println("Código registrado com sucesso: " + codigo);
            entradaValida = true;

            } catch (InputMismatchException e) {
                System.err.println("Erro: deve digitar o codigo certo!");
                entrada.nextLine();
            }
        }    
        entrada.close();
    }
}