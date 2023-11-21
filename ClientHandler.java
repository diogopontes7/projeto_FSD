
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Inventario inv;

    // Constructor
    public ClientHandler(Socket socket, Inventario sharedInventario) {
        this.clientSocket = socket;
        this.inv = sharedInventario;
    }

    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            // manda para o cliente
            out = new PrintWriter(
                    clientSocket.getOutputStream(), true);

            // recebe do cliente
            in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {

                // escreve o output do client
                System.out.printf(
                        " Enviado pelo cliente: " + clientSocket.getInetAddress() + ": %s\n",
                        line);
                if (line.equalsIgnoreCase("STOCK_REQUEST")) {
                    out.println(inv.stock_request());
                    out.flush();
                } else if (line.equalsIgnoreCase("STOCK_UPDATE")) {
                    try {
                        String id = in.readLine();
                        String qtd = in.readLine();
                        int n = Integer.parseInt(qtd);
                        System.out.println("Valores do STOCK_UPDATE: " + id + ", " + n);
                        String resposta = inv.stock_update(id, n);
                        if (resposta.equals("Quantidade_Invalida")) {
                            out.println("STOCK_ERROR(404): Quantidade invalida.");
                        } else if (resposta.equals("Id_invalido")) {
                            out.println("STOCK_ERROR(404): Id invalido.");
                        } else if (resposta.equals("Alteracao_aprovada")) {
                            out.println("STOCK_UPDATED: " + inv.stock_request());
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (line.equalsIgnoreCase("Novo_produto")) {

                    try {
                        String id = in.readLine();
                        String nome = in.readLine();
                        String qtd = in.readLine();
                        int n = Integer.parseInt(qtd);
                        System.out.println("Novo Produto: " + id + ", " + nome + ", " + qtd);
                        if (inv.addProduto(new Produto(id, nome, n)).equals("Produto Criado")) {
                            out.println("Produto Criado: " + inv.toString());
                        } else {
                            out.println("STOCK_ERROR(404): Produto ja existente");
                        }
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (line.equalsIgnoreCase("Remover_Produto")) {
                    try {
                        String id = in.readLine();
                        if (inv.remProduto(id).equals("Produto removido")) {
                            out.println("Produto removido: " + inv.toString());
                        } else {
                            out.println("STOCK_ERROR(404): Produto nao existe");
                        }
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (line.equalsIgnoreCase("SAVE")) {
                    inv.guardarDados();
                }
            }
        } catch (

        IOException e) {
            e.printStackTrace();
        } finally {
            try {

                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
