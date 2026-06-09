import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexão {

    // O banco agora é um arquivo local chamado estoque.db
    private static final String URL = "jdbc:sqlite:estoque.db";

    public static Connection getConexao() {
        try {
            // Força o Java a carregar o driver do SQLite
            Class.forName("org.sqlite.JDBC");
            Connection conexao = DriverManager.getConnection(URL);
            
            // Cria a tabela automaticamente caso o arquivo estoque.db acabe de ser criado
            criarTabelaSeNaoExistir(conexao);
            
            return conexao;
        } catch (ClassNotFoundException e) {
            System.out.println("Driver do SQLite não encontrado na pasta lib! " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao SQLite: " + e.getMessage());
            return null;
        }
    }

    private static void criarTabelaSeNaoExistir(Connection conexao) {
        String sql = "CREATE TABLE IF NOT EXISTS produtos ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "nome TEXT NOT NULL,"
                   + "descricao TEXT,"
                   + "preco_unitario REAL NOT NULL,"
                   + "quantidade_estoque INTEGER NOT NULL"
                   + ");";
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela automática: " + e.getMessage());
        }
    }
}