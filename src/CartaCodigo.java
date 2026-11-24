/** Carta de programação: acrescenta um exemplo Java à explicação. */
public class CartaCodigo extends Carta {
    // Dado específico desta subclasse: o exemplo é texto para leitura no jogo.
    private final String exemploCodigo;

    public CartaCodigo(String idConteudo, String titulo,
                       String explicacao, String exemploCodigo) {
        // Inicializa primeiro a parte comum, que pertence à classe Carta.
        super(idConteudo, titulo, explicacao);
        // Depois guarda a informação extra desta categoria.
        this.exemploCodigo = exemploCodigo;
    }

    // Mesmo método de CartaConceito, com outro comportamento: inclui código.
    @Override
    public String getConteudoEducativo() {

        // As quebras de linha organizam título, explicação e exemplo Java.
        return getTitulo() + "\n\n" + getExplicacao()
                + "\n\nExemplo em Java:\n" + exemploCodigo;
    }
}