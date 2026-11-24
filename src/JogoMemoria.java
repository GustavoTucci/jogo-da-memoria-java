import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Regras da partida. Esta classe não conhece botões, janelas ou Timer. */
public class JogoMemoria {
    // A lista usa o tipo base Carta e aceita objetos das duas subclasses.
    // final mantém a referência; os elementos da lista ainda podem mudar.
    private final List<Carta> cartas = new ArrayList<>();
    // null significa que ainda não existe uma carta selecionada nessa posição.
    private Carta primeiraCarta;
    private Carta segundaCarta;
    // O placar conta duplas selecionadas e pares acertados, não cliques isolados.
    private int tentativas;
    private int paresEncontrados;
    // Enquanto true, existe um erro visível e novas seleções são ignoradas.
    private boolean aguardando;
    // Guarda o último texto educativo que a interface deve apresentar.
    private String ultimoConteudo;

    public JogoMemoria() {
        // Reaproveita a preparação do reinício para montar a primeira partida.
        reiniciar();
    }

    public void reiniciar() {
        // Descarta a partida anterior e limpa as seleções e os contadores.
        cartas.clear();
        primeiraCarta = null;
        segundaCarta = null;
        tentativas = 0;
        paresEncontrados = 0;
        aguardando = false;
        // Também restaura a mensagem exibida antes do primeiro acerto.
        ultimoConteudo = "Encontre um par para conhecer o conceito!\n\n"
                + "Selecione duas cartas. Cada dupla selecionada conta como uma tentativa.";
        // Novos objetos garantem que nenhuma carta mantenha o estado antigo.
        criarCartas();
        // Reorganiza as posições aleatoriamente antes de começar a partida.
        Collections.shuffle(cartas);
    }

    private void criarCartas() {
        // Quatro pares de programação: explicação acompanhada de exemplo Java.
        // Cada chamada recebe ID, título, explicação e código, nessa ordem.
        adicionarParCodigo("variaveis", "Variáveis",
                "Uma variável tem um nome e armazena um valor de determinado tipo.",
                "int pontos = 10;\npontos = pontos + 5;");
        adicionarParCodigo("condicionais", "Condicionais",
                "Uma condição decide qual trecho será executado. O if testa uma condição; o else trata o caso contrário.",
                "int nota = 8;\nif (nota >= 6) {\n    System.out.println(\"Aprovado\");\n} else {\n    System.out.println(\"Revisar\");\n}");
        adicionarParCodigo("lacos", "Laços de repetição",
                "Laços repetem instruções. O for reúne início, condição e atualização; o while repete enquanto a condição for verdadeira.",
                "for (int i = 1; i <= 3; i++) {\n    System.out.println(i);\n}");
        adicionarParCodigo("arrays", "Arrays",
                "Um array guarda elementos do mesmo tipo em posições numeradas. Em Java, o primeiro índice é zero e o tamanho é fixo após a criação.",
                "int[] notas = {8, 7, 10};\nSystem.out.println(notas[0]); // Mostra 8");
        // Quatro pares sobre os pilares: apresentam somente a explicação.
        adicionarParConceito("abstracao", "Abstração",
                "Destaca o que é essencial e esconde detalhes de implementação. Aqui, Carta define o que toda carta possui e exige um método de conteúdo educativo.");
        adicionarParConceito("encapsulamento", "Encapsulamento",
                "Protege o estado interno e controla seu acesso por métodos. Aqui, atributos privados e métodos como ocultar() impedem esconder uma carta já encontrada.");
        adicionarParConceito("heranca", "Herança",
                "Permite criar classes a partir de outra, reaproveitando e especializando comportamentos. CartaConceito e CartaCodigo herdam de Carta.");
        adicionarParConceito("polimorfismo", "Polimorfismo",
                "Permite chamar o mesmo método por uma referência do tipo base, com comportamentos diferentes. Aqui, getConteudoEducativo() varia conforme a subclasse da carta.");
    }

    private void adicionarParCodigo(String id, String titulo, String texto, String codigo) {
        // Duas instâncias com o mesmo ID formam um par com estados independentes.
        for (int i = 0; i < 2; i++) {
            cartas.add(new CartaCodigo(id, titulo, texto, codigo));
        }
    }

    private void adicionarParConceito(String id, String titulo, String texto) {
        // O laço repete duas vezes: i vale 0 na primeira passagem e 1 na segunda.
        for (int i = 0; i < 2; i++) {
            cartas.add(new CartaConceito(id, titulo, texto));
        }
    }

    /** Retorna true se o clique foi aceito; false se deve ser ignorado. */
    public boolean selecionarCarta(int indice) {
        // Primeira proteção: bloqueia a espera e evita posições fora da lista.
        if (aguardando || indice < 0 || indice >= cartas.size()) {
            return false;
        }
        // Segunda proteção: não aceita uma carta acertada ou já aberta.
        Carta selecionada = cartas.get(indice);
        if (selecionada.isEncontrada() || selecionada.isRevelada()) {
            return false;
        }
        // Só revela depois de confirmar que o clique é válido.
        selecionada.revelar();
        if (primeiraCarta == null) {
            // Primeiro clique da jogada: guarda a seleção sem contar tentativa.
            primeiraCarta = selecionada;
            return true;
        }

        // O segundo clique válido completa uma dupla e conta uma tentativa.
        segundaCarta = selecionada;
        tentativas++;
        if (primeiraCarta.formaParCom(segundaCarta)) {
            // Acerto: mantém as duas abertas e acrescenta um par ao placar.
            primeiraCarta.marcarComoEncontrada();
            segundaCarta.marcarComoEncontrada();
            paresEncontrados++;
            // Polimorfismo: a referência é Carta, sem consultar a subclasse.
            ultimoConteudo = primeiraCarta.getConteudoEducativo();
            // Libera as seleções para que a próxima carta inicie outra jogada.
            primeiraCarta = null;
            segundaCarta = null;
        } else {
            // Erro: mantém ambas abertas. A interface inicia a espera de 1 segundo.
            aguardando = true;
        }
        // true significa clique aceito; não significa necessariamente acerto.
        return true;
    }

    /** A interface chama este método quando termina o tempo de exibição. */
    public void ocultarParIncorreto() {
        if (aguardando) {
            // Depois da espera, fecha o erro e permite uma nova jogada.
            primeiraCarta.ocultar();
            segundaCarta.ocultar();
            primeiraCarta = null;
            segundaCarta = null;
            aguardando = false;
        }
    }

    public List<Carta> getCartas() {
        // Protege a lista contra inclusão/remoção externa; as cartas são consultáveis.
        return Collections.unmodifiableList(cartas);
    }

    // Consultas usadas pela interface para mostrar o placar e o estado da partida.
    public int getTentativas() {
        return tentativas;
    }

    public int getParesEncontrados() {
        return paresEncontrados;
    }

    public boolean isAguardando() {
        return aguardando;
    }

    public boolean isConcluido() {
        // A vitória depende dos oito pares, sem limitar a quantidade de tentativas.
        return paresEncontrados == 8;
    }

    public String getUltimoConteudo() {
        return ultimoConteudo;
    }
}
