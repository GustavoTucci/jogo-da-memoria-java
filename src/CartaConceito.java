/** Carta dos pilares: apresenta somente a explicação do conceito. */
public class CartaConceito extends Carta {
    // Herança: super envia os dados para o construtor de Carta.
    public CartaConceito(String idConteudo, String titulo, String explicacao) {
        super(idConteudo, titulo, explicacao);
    }

    // Override indica a implementação do método abstrato herdado.
    @Override
    public String getConteudoEducativo() {
        // Os métodos de consulta vêm de Carta; \n\n deixa uma linha em branco.
        return getTitulo() + "\n\n" + getExplicacao();
    }
}
