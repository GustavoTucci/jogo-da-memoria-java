import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/** Interface: mostra os dados do jogo e encaminha os cliques para as regras. */
public class TelaJogo extends JFrame {
    // Identificador de serialização herdada de JFrame; não interfere na partida.
    private static final long serialVersionUID = 1L;
    // Constantes definem a paleta comum da janela e dos três estados das cartas.
    private static final Color FUNDO = new Color(12, 24, 44);
    private static final Color CIANO = new Color(30, 190, 215);
    private static final Color VERSO = new Color(22, 49, 79);
    private static final Color FRENTE = new Color(239, 246, 255);
    private static final Color ENCONTRADA = new Color(197, 245, 221);

    // O modelo cuida das regras; estes componentes apenas apresentam seus dados.
    private final JogoMemoria jogo = new JogoMemoria();
    private final JButton[] botoes = new JButton[16];
    private final JLabel placar = new JLabel();
    private final JTextArea explicacao = new JTextArea();
    // O mapa relaciona cada ID a uma imagem compartilhada pelas duas cartas do par.
    private final Map<String, ImageIcon> imagens = new HashMap<>();
    private final ImageIcon imagemVerso = Ilustracoes.criar("verso");
    // Guardar o Timer permite cancelar a ação pendente ao reiniciar a partida.
    private Timer temporizador;

    public TelaJogo() {
        // Chama o construtor de JFrame e define o título da janela.
        super("Code Memory | Aprenda encontrando pares");
        // Fechar a janela também encerra a aplicação.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // BorderLayout organiza cabeçalho ao norte, grade no centro e texto ao sul.
        JPanel principal = new JPanel(new BorderLayout(12, 12));
        principal.setBackground(FUNDO);
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        principal.add(criarCabecalho(), BorderLayout.NORTH);
        principal.add(criarTabuleiro(), BorderLayout.CENTER);
        principal.add(criarAreaEducativa(), BorderLayout.SOUTH);
        // Em telas menores, permite rolar o conteúdo sem esmagar as cartas.
        JScrollPane conteudo = new JScrollPane(principal);
        conteudo.setBorder(null);
        conteudo.getVerticalScrollBar().setUnitIncrement(16);
        setContentPane(conteudo);

        // Mostra o estado inicial, ajusta as dimensões e centraliza a janela.
        atualizarTela();
        setSize(980, 880);
        setMinimumSize(new Dimension(860, 740));
        setLocationRelativeTo(null);
    }

    /** Monta o título, o placar e o botão Reiniciar. */
    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout(12, 6));
        // Transparente para deixar aparecer o fundo azul do painel principal.
        cabecalho.setOpaque(false);
        JLabel titulo = new JLabel("</> Code Memory");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 27));
        titulo.setForeground(CIANO);
        cabecalho.add(titulo, BorderLayout.NORTH);

        placar.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        placar.setForeground(Color.WHITE);
        cabecalho.add(placar, BorderLayout.CENTER);

        JButton reiniciar = new JButton("Reiniciar");
        reiniciar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        reiniciar.setBackground(CIANO);
        reiniciar.setForeground(FUNDO);
        // Registra a ação para o clique futuro; não reinicia durante a montagem.
        reiniciar.addActionListener(evento -> reiniciarPartida());
        cabecalho.add(reiniciar, BorderLayout.EAST);
        return cabecalho;
    }

    /** Cria os 16 botões e prepara uma imagem para cada conteúdo. */
    private JPanel criarTabuleiro() {
        // Quatro linhas, quatro colunas e intervalos de dez pixels.
        JPanel tabuleiro = new JPanel(new GridLayout(4, 4, 10, 10));
        tabuleiro.setOpaque(false);
        tabuleiro.setPreferredSize(new Dimension(800, 460));
        for (int i = 0; i < botoes.length; i++) {
            // A lambda guarda um índice fixo para identificar seu próprio botão.
            final int indice = i;
            JButton botao = new JButton();
            botao.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            // A ilustração fica acima e o título fica centralizado abaixo dela.
            botao.setHorizontalTextPosition(SwingConstants.CENTER);
            botao.setVerticalTextPosition(SwingConstants.BOTTOM);
            botao.setIconTextGap(4);
            botao.setMargin(new Insets(3, 3, 3, 3));
            botao.setOpaque(true);
            botao.addActionListener(evento -> selecionarCarta(indice));
            // Guarda para atualizações futuras e coloca na célula da grade.
            botoes[i] = botao;
            tabuleiro.add(botao);
        }
        // Evita desenhar duas vezes o mesmo conteúdo e preserva imagens iguais no par.
        for (Carta carta : jogo.getCartas()) {
            String id = carta.getIdConteudo();
            if (!imagens.containsKey(id)) {
                imagens.put(id, Ilustracoes.criar(id));
            }
        }
        return tabuleiro;
    }

    /** Monta a área de explicação que permanece abaixo do tabuleiro. */
    private JPanel criarAreaEducativa() {
        JPanel area = new JPanel(new BorderLayout(0, 6));
        area.setOpaque(false);
        JLabel titulo = new JLabel("Aprenda com os pares encontrados");
        titulo.setForeground(CIANO);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        area.add(titulo, BorderLayout.NORTH);

        // Texto somente para leitura, com quebra de linha e fonte própria para código.
        explicacao.setEditable(false);
        explicacao.setLineWrap(true);
        explicacao.setWrapStyleWord(true);
        explicacao.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        explicacao.setBackground(VERSO);
        explicacao.setForeground(Color.WHITE);
        explicacao.setMargin(new Insets(10, 12, 10, 12));
        // Exemplos maiores podem ser lidos com a barra de rolagem desta área.
        JScrollPane rolagem = new JScrollPane(explicacao);
        rolagem.setPreferredSize(new Dimension(0, 185));
        rolagem.setBorder(BorderFactory.createLineBorder(CIANO));
        area.add(rolagem, BorderLayout.CENTER);
        return area;
    }

    /** Encaminha o clique ao modelo e trata a espera ou a mensagem de vitória. */
    private void selecionarCarta(int indice) {
        // Se a regra rejeitar o clique, encerra sem alterar a apresentação.
        if (!jogo.selecionarCarta(indice)) {
            return;
        }
        atualizarTela();
        if (jogo.isAguardando()) {
            // O Timer agenda a ação para aproximadamente 1000 ms sem travar a EDT.
            temporizador = new Timer(1000, evento -> {
                jogo.ocultarParIncorreto();
                atualizarTela();
            });
            // Executa uma vez por erro; start inicia a contagem do intervalo.
            temporizador.setRepeats(false);
            temporizador.start();
        } else if (jogo.isConcluido()) {
            // Só a vitória abre uma mensagem. Os acertos comuns usam a área de texto.
            JOptionPane.showMessageDialog(this,
                    "Parabéns! Você encontrou os 8 pares em "
                            + jogo.getTentativas() + " tentativas!",
                    "Vitória no Code Memory", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Interrompe a espera anterior e solicita uma nova partida ao modelo. */
    private void reiniciarPartida() {
        // Cancela a espera antiga antes de criar o novo estado do jogo.
        if (temporizador != null) {
            temporizador.stop();
            temporizador = null;
        }
        jogo.reiniciar();
        atualizarTela();
    }

    /** Copia o estado atual do modelo para o placar, o texto e os botões. */
    private void atualizarTela() {
        placar.setText("Tentativas: " + jogo.getTentativas()
                + "    |    Pares encontrados: " + jogo.getParesEncontrados() + "/8");
        explicacao.setText(jogo.getUltimoConteudo());
        // Volta ao começo da explicação quando o conteúdo é atualizado.
        explicacao.setCaretPosition(0);

        for (int i = 0; i < botoes.length; i++) {
            Carta carta = jogo.getCartas().get(i);
            JButton botao = botoes[i];
            if (carta.isRevelada()) {
                // O ternário condicao ? A : B escolhe o aspecto de acerto ou de seleção.
                botao.setIcon(imagens.get(carta.getIdConteudo()));
                botao.setText((carta.isEncontrada() ? "OK - " : "") + carta.getTitulo());
                botao.setBackground(carta.isEncontrada() ? ENCONTRADA : FRENTE);
                botao.setForeground(FUNDO);
                botao.setToolTipText(carta.getTitulo()
                        + (carta.isEncontrada() ? " (par encontrado)" : ""));
            } else {
                // A dica de uma carta fechada informa só sua posição, sem revelar o tema.
                botao.setIcon(imagemVerso);
                botao.setText("Code Memory");
                botao.setBackground(VERSO);
                botao.setForeground(Color.WHITE);
                botao.setToolTipText("Revelar carta " + (i + 1));
            }
            Color borda = carta.isEncontrada() ? new Color(34, 150, 100) : CIANO;
            botao.setBorder(BorderFactory.createLineBorder(borda, 2));
            // O modelo ignora cliques inválidos. Os botões continuam habilitados
            // para que o Swing preserve as cores e imagens dos pares encontrados.
        }
    }
}
