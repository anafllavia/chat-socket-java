package thread;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;


public class chatCliente {
    private static PrintWriter saida;
    private static BufferedReader entrada;
    private static JTextArea areaMensagens;
    private static JTextField campoMensagem;
    private static String nome;

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Chat");
        areaMensagens = new JTextArea(20, 50);
        areaMensagens.setEditable(false);
        campoMensagem = new JTextField(50);

        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());
        painel.add(new JScrollPane(areaMensagens), BorderLayout.CENTER);
        painel.add(campoMensagem, BorderLayout.SOUTH);

        frame.getContentPane().add(painel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Conectar ao servidor
        Socket socket = new Socket("localhost", 12345);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        saida = new PrintWriter(socket.getOutputStream(), true);

        // Ler mensagens do servidor em uma thread separada
        new Thread(new LeitorDeMensagens()).start();

        // Pedir o nome do usuário
        nome = JOptionPane.showInputDialog(frame, "Digite seu nome de usuário:");
        if (nome == null || nome.trim().isEmpty()) {
            nome = "Anônimo";
        }

        // Enviar nome para o servidor
        saida.println(nome);

        // Ação de envio de mensagem
        campoMensagem.addActionListener(e -> {
            String mensagem = campoMensagem.getText().trim();
            if (!mensagem.isEmpty()) {
                saida.println(mensagem);
                campoMensagem.setText("");
            }
        });
    }

    // Thread para ler mensagens do servidor
    static class LeitorDeMensagens implements Runnable {
        @Override
        public void run() {
            try {
                String mensagem;
                while ((mensagem = entrada.readLine()) != null) {
                    final String mensagemFinal = mensagem;
                    SwingUtilities.invokeLater(() -> areaMensagens.append(mensagemFinal + "\n"));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                    areaMensagens.append("Conexão encerrada pelo servidor.\n")
                );
            }
        }
    }
}
