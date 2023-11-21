
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientEX {
    private static String inv;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("ERROR(102): use java ClientEX <ip>");
            System.exit(-1);
        }

        Scanner sc = new Scanner(System.in);

        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try (Socket socket = new Socket(ipAddress, 1234)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread userInputThread = new Thread(() -> {
                try {
                    int tempoRefresh = 5;
                    try {
                        if (args.length == 2) {
                            System.out.println("Tempo de refresh entre STOCK_REQUEST: " + args[1] + " segundos");
                            tempoRefresh = Integer.parseInt(args[1]);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Valor invalido, utilizamos os 5segundos");
                    }
                    boolean x = true;
                    while (x = true) {
                        // Manda STOCK_REQUEST para o servidor
                        out.println("STOCK_REQUEST");
                        String response = in.readLine();
                        inv = response.replace("|", "\n");
                        // Stock_Request é enviado de 5 em 5 segundos
                        try {
                            Thread.sleep(tempoRefresh * 1000);
                        } catch (InterruptedException e) {
                            x = true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Dá inicio á Thread do User
            userInputThread.start();
            int opcao1 = 0;
            do {
                System.out.println("Login:");
                System.out.println("1- Admin");
                System.out.println("2- Cliente");
                System.out.println("3- Exit");
                opcao1 = sc.nextInt();
                sc.nextLine();
                switch (opcao1) {
                    case 1:
                        int opcao2 = 0;
                        do {
                            System.out.println("Menu:");
                            System.out.println("1- Adicionar Produto");
                            System.out.println("2- Remover Produto");
                            System.out.println("3- Voltar");
                            opcao2 = sc.nextInt();
                            sc.nextLine();

                            switch (opcao2) {
                                case 1:
                                    System.out.println("Inventario: " + inv);
                                    String id;
                                    int qtd;
                                    String nome;
                                    System.out.println("Insira o ID do Produto.");
                                    id = sc.nextLine();
                                    System.out.println("Insira a descriçao do Produto.");
                                    nome = sc.nextLine();
                                    do {
                                        System.out.println(
                                                "Insira a quantidade de Produto (Numero inteiro positivo).");
                                        qtd = sc.nextInt();
                                        if (qtd < 0) {
                                            System.out.println("STOCK_ERROR(101): Valor Invalido.");
                                        }
                                    } while (qtd < 0);
                                    out.println("Novo_Produto");
                                    out.println(id);
                                    out.println(nome);
                                    out.println(qtd);
                                    out.flush();
                                    break;
                                case 2:
                                    System.out.println("Inventario: " + inv);
                                    System.out.println("Insira o ID do Produto.");
                                    id = sc.nextLine();
                                    out.println("Remover_Produto");
                                    out.println(id);
                                    break;
                                case 3:
                                    break;
                                default:
                                    System.out.println("ERROR(100): Escolha inválida. Selecionar 1, 2, ou 3.");
                                    break;
                            }
                        } while (opcao2 != 3);
                        break;
                    case 2:
                        int opcao3 = 0;
                        do {
                            System.out.println("Menu:");
                            System.out.println("1- STOCK_UPDATE");
                            System.out.println("2- STOCK_REQUEST");
                            System.out.println("3- Voltar");
                            opcao3 = sc.nextInt();
                            sc.nextLine();

                            switch (opcao3) {
                                case 1:
                                    String id;
                                    int qtd;
                                    System.out.println("Inventario: " + inv);
                                    System.out.println("Insira o ID do Produto.");
                                    id = sc.nextLine();
                                    do {
                                        System.out.println("Insira a quantidade de Produto (Numeros inteiros).");
                                        qtd = sc.nextInt();
                                    } while (qtd != (int) qtd);
                                    out.println("STOCK_UPDATE");
                                    out.println(id);
                                    out.println(qtd);
                                    out.flush();
                                    String resposta = in.readLine();
                                    String resposta1 = resposta.replace("|", "\n");
                                    System.out.println("Update: " + resposta1);
                                    userInputThread.interrupt();
                                    opcao1 = 2;
                                    break;
                                case 2:
                                    System.out.println("Inventario: " + inv);
                                    break;
                                case 3:
                                    break;
                                default:
                                    System.out.println("ERROR(100): Escolha inválida. Selecionar 1, 2, ou 3.");
                                    break;
                            }
                        } while (opcao3 != 3);
                        break;
                    case 3:
                        //System.exit(-1);
                        break;
                }
            } while (opcao1 != 3);
            out.println("SAVE");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.exit(-1);
            sc.close();
        }
    }
}
