import javax.swing.SwingUtilities;

/** Ponto de entrada: executado pelo comando java -cp out Main. */
public class Main {
    public static void main(String[] args) {
        // args receberia argumentos de terminal; esta versão não precisa deles.
        // Cria e mostra os componentes na thread de eventos do Swing.
        // A lambda () -> define a tarefa; invokeLater agenda sua execução na EDT.
        SwingUtilities.invokeLater(() -> new TelaJogo().setVisible(true));
    }
}
