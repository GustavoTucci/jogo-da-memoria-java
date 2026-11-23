import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/** Carrega os sprites usados pelas cartas. */
public final class Ilustracoes {
    private static final int LARGURA = 140;
    private static final int ALTURA = 77;

    private Ilustracoes() {
    }

    /** Recebe o ID do conteúdo e devolve o sprite pronto para um JButton. */
    public static ImageIcon criar(String id) {
        if (id == null || !id.matches(
                "variaveis|condicionais|lacos|arrays|abstracao|encapsulamento|heranca|polimorfismo|verso")) {
            throw new IllegalArgumentException("Ilustração desconhecida: " + id);
        }

        String nomeArquivo = id + ".png";
        URL recurso = Ilustracoes.class.getResource("/resources/sprites/" + nomeArquivo);
        Path caminho = Paths.get("src", "resources", "sprites", nomeArquivo);

        try {
            BufferedImage imagem;
            if (recurso != null) {
                imagem = ImageIO.read(recurso);
            } else if (Files.isRegularFile(caminho)) {
                imagem = ImageIO.read(caminho.toFile());
            } else {
                throw new IllegalStateException("Sprite não encontrado: " + caminho);
            }

            if (imagem == null) {
                throw new IllegalStateException("Não foi possível ler o sprite: " + nomeArquivo);
            }
            if (imagem.getWidth() != LARGURA || imagem.getHeight() != ALTURA) {
                throw new IllegalStateException("Dimensões incorretas para o sprite: "
                        + nomeArquivo + " (" + imagem.getWidth() + "x" + imagem.getHeight()
                        + ", esperado " + LARGURA + "x" + ALTURA + ")");
            }
            return new ImageIcon(imagem);
        } catch (IOException erro) {
            throw new IllegalStateException("Erro ao carregar o sprite: " + nomeArquivo, erro);
        }
    }
}
