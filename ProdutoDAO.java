import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ProdutoDAO {

    public void cadastrar(Produto produto) {
        String sql = "INSERT INTO produtos (nome, descricao, preco_unitario, quantidade_estoque) VALUES (?, ?, ?, ?)";
        try (Connection conexao = Conexão.getConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setInt(4, produto.getQuantidade());
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");
        } catch (SQLException ex) {
            System.out.println("Erro ao salvar: " + ex.getMessage());
        }
    }

    public void editar(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco_unitario = ?, quantidade_estoque = ? WHERE id = ?";
        try (Connection conexao = Conexão.getConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setInt(4, produto.getQuantidade());
            pstmt.setInt(5, produto.getId()); 
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Produto atualizado com sucesso!");
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar: " + ex.getMessage());
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conexao = Conexão.getConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Produto excluído com sucesso!");
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir: " + ex.getMessage());
        }
    }

    public List<Produto> buscarTodos() {
        List<Produto> listaProdutos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco_unitario, quantidade_estoque FROM produtos";
        
        try (Connection conexao = Conexão.getConexao();
             Statement mensageiro = conexao.createStatement();
             ResultSet resultado = mensageiro.executeQuery(sql)) {

            while (resultado.next()) {
                Produto produto = new Produto();
                produto.setId(resultado.getInt("id"));
                produto.setNome(resultado.getString("nome"));
                produto.setDescricao(resultado.getString("descricao"));
                produto.setPreco(resultado.getDouble("preco_unitario"));
                produto.setQuantidade(resultado.getInt("quantidade_estoque"));
                
                listaProdutos.add(produto);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar dados: " + e.getMessage());
        }
        return listaProdutos;
    }
}