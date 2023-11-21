
import java.io.IOException;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;


public class ClientRMI implements Serializable{
     
    public ClientRMI() {
    }

    public static void main(String[] args) throws NotBoundException {
        Scanner sc = new Scanner(System.in);

        if (args.length != 1) {
			System.out.println("Erro: use java ClientRMI <ipNameServer>");
			System.exit(-1);
		}


        try {
            //Objeto remoto que definimos como um objeto local
            DirectNotification client = new Notify();

            // Localiza o RMI registry no endereço ip e na porta=1099

            Stock_Server rmiServer = (Stock_Server) LocateRegistry.getRegistry(args[0], 1099).lookup("ServerRMI");
            
            //Adicionar o client á lista de clientes
            rmiServer.subscribe(client);

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
                                    String resposta = rmiServer.stock_request();
                                    String resposta1 = resposta.replace("|", "\n");
                                    System.out.println("Update: " + resposta1);
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
                                    System.out.println(rmiServer.addProduto(new Produto(id, nome, qtd)));

                                    break;
                                case 2:
                                    resposta = rmiServer.stock_request();
                                    resposta1 = resposta.replace("|", "\n");
                                    System.out.println("Update: " + resposta1);
                                    System.out.println("Insira o ID do Produto.");
                                    id = sc.nextLine();
                                    System.out.println(rmiServer.remProduto(id));
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
                                    String resposta = rmiServer.stock_request();
                                    String resposta1 = resposta.replace("|", "\n");
                                    System.out.println("Update: " + resposta1);
                                    System.out.println("Insira o ID do Produto.");
                                    id = sc.nextLine();
                                    do {
                                        System.out.println("Insira a quantidade de Produto (Numeros inteiros).");
                                        qtd = sc.nextInt();
                                    } while (qtd != (int) qtd);
                                    System.out.println(rmiServer.stock_update(id, qtd));

                                    break;
                                case 2:
                                    resposta = rmiServer.stock_request();
                                    resposta1 = resposta.replace("|", "\n");
                                    System.out.println("Update: " + resposta1);
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
                        break;
                }
            } while (opcao1 != 3);
            rmiServer.guardarDados();
            rmiServer.unsubscribe(client);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.exit(-1);
            sc.close();
        }
    }


}
