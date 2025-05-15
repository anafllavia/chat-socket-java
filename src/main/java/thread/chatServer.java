package thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class chatServer {
    private static final int PORTA = 12345;
    private static List<PrintWriter> saidas = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Servidor de chat iniciado...");
        ServerSocket servidor = new ServerSocket(PORTA);

        while (true) {
            Socket cliente = servidor.accept();
            System.out.println("Novo cliente conectado!");

            PrintWriter saidaCliente = new PrintWriter(cliente.getOutputStream(), true);
            saidas.add(saidaCliente);

            new Thread(() -> {
                try {
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                    String nome = entrada.readLine(); // Lê o nome do usuário
                    String mensagem;

                    while ((mensagem = entrada.readLine()) != null) {
                        System.out.println("Mensagem recebida: " + mensagem);

                        for (PrintWriter saida : saidas) {
                            saida.println(nome + ": " + mensagem); // Envia para todos, inclusive o remetente
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Erro com cliente: " + e.getMessage());
                }
            }).start();
        }
    }
}
