/** Dados e estados que toda carta possui, independentemente do conteúdo. */
public abstract class Carta {
    // Encapsulamento: só a própria classe acessa diretamente estes atributos.
    // final impede trocar os dados do conteúdo depois de construir a carta.
    private final String idConteudo;
    private final String titulo;
    private final String explicacao;
    // Os boolean começam como false: carta fechada e par ainda não encontrado.
    private boolean revelada;
    private boolean encontrada;

    // As subclasses chamam este construtor usando super(...).
    protected Carta(String idConteudo, String titulo, String explicacao) {
        // this identifica o atributo; o nome sem this identifica o parâmetro.
        this.idConteudo = idConteudo;
        this.titulo = titulo;
        this.explicacao = explicacao;
    }

    // Os getters permitem consultar os dados sem oferecer setters para alterá-los.
    public String getIdConteudo() {
        return idConteudo;
    }

    public String getTitulo() {
        return titulo;
    }

    // protected permite que as subclasses usem a explicação ao montar seu texto.
    protected String getExplicacao() {
        return explicacao;
    }

    // O prefixo is é usado em consultas que devolvem true ou false.
    public boolean isRevelada() {
        return revelada;
    }

    public boolean isEncontrada() {
        return encontrada;
    }

    // Uma carta encontrada já está aberta e não precisa mudar de estado.
    public void revelar() {
        if (!encontrada) {
            revelada = true;
        }
    }

    // Regra de proteção: um par encontrado nunca pode voltar a ficar fechado.
    public void ocultar() {
        if (!encontrada) {
            revelada = false;
        }
    }

    // Encontrada e revelada ficam true juntas, mantendo o estado coerente.
    public void marcarComoEncontrada() {
        encontrada = true;
        revelada = true;
    }

    public boolean formaParCom(Carta outra) {
        // Exige outro objeto válido. equals compara o texto do ID, não a classe.
        // O && evita acessar outra.idConteudo caso outra seja null.
        return outra != null && this != outra
                && idConteudo.equals(outra.idConteudo);
    }

    // Abstração: define o contrato, mas deixa a implementação para as subclasses.
    // Não há corpo aqui porque cada categoria monta seu texto de uma forma.
    public abstract String getConteudoEducativo();
}
