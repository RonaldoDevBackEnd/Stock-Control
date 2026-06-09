import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class TelaEstoque {
    
    private static DefaultTableModel modelo = new DefaultTableModel();
    // Instanciamos o nosso DAO para a tela poder conversar com o banco
    private static ProdutoDAO produtoDAO = new ProdutoDAO(); 

    public static void main(String[] args) {
        JFrame janela = new JFrame("Sistema de Estoque");
        janela.setLayout(new BorderLayout());

        modelo.addColumn("ID");
        modelo.addColumn("Nome");
        modelo.addColumn("Descrição");
        modelo.addColumn("Preço");
        modelo.addColumn("Quantidade");
        
        JTable tabela = new JTable(modelo);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);  
        tabela.getColumnModel().getColumn(1).setPreferredWidth(150); 
        tabela.getColumnModel().getColumn(2).setPreferredWidth(250); 
        tabela.getColumnModel().getColumn(3).setPreferredWidth(100); 
        tabela.getColumnModel().getColumn(4).setPreferredWidth(100); 

        janela.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel barraFerramentas = new JPanel();
        TelaEstoque tela = new TelaEstoque();
        
        // --- BOTÃO CADASTRAR ---
        JButton btnCadastrar = new JButton("Cadastrar Produto");
        btnCadastrar.addActionListener(e -> tela.abrirJanelaCadastro());
        barraFerramentas.add(btnCadastrar);

        // --- BOTÃO EDITAR ---
        JButton btnEditar = new JButton("Editar Produto");
        btnEditar.addActionListener(e -> {
            int linhaSelecionada = tabela.getSelectedRow();
            if (linhaSelecionada != -1) {
                tela.abrirJanelaEdicao(linhaSelecionada);
            } else {
                JOptionPane.showMessageDialog(null, "Selecione um produto para editar.");
            }
        });
        barraFerramentas.add(btnEditar);
        
        // --- BOTÃO EXCLUIR ---
        JButton btnExcluir = new JButton("Excluir Produto");
        btnExcluir.addActionListener(e -> {
            int linhaSelecionada = tabela.getSelectedRow();
            if (linhaSelecionada != -1) {
                int idProduto = (int) modelo.getValueAt(linhaSelecionada, 0);
                
                // A tela apenas pede ao DAO para excluir, passando o ID
                produtoDAO.excluir(idProduto);
                atualizarTabela(); 
                
            } else {
                JOptionPane.showMessageDialog(null, "Selecione um produto para excluir.");
            }
        });
        barraFerramentas.add(btnExcluir);

        janela.add(barraFerramentas, BorderLayout.NORTH);
        janela.setSize(750, 400); 
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLocationRelativeTo(null); 
        janela.setVisible(true);

        atualizarTabela();
    }

    // O método agora pede a lista para o DAO e apenas desenha na tela
    public static void atualizarTabela() {
        modelo.setRowCount(0);
        List<Produto> lista = produtoDAO.buscarTodos();
        
        for (Produto p : lista) {
            modelo.addRow(new Object[]{
                p.getId(), 
                p.getNome(), 
                p.getDescricao(), 
                p.getPreco(), 
                p.getQuantidade()
            });
        }
    }

    public void abrirJanelaCadastro() {
        JDialog dialog = new JDialog((Frame) null, "Cadastrar Produto", true);
        dialog.setLayout(new BorderLayout());
        dialog.setPreferredSize(new Dimension(450, 300)); 

        JPanel painelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        
        JTextField campoNome = new JTextField();
        JTextField campoDesc = new JTextField();
        JTextField campoPreco = new JTextField();
        JTextField campoQtd = new JTextField();

        campoNome.setDocument(new LimitadorCaracteres(50));
        campoDesc.setDocument(new LimitadorCaracteres(100));

        configurarPlaceholder(campoNome, "Ex: Teclado (Máx 50 carac.)");
        configurarPlaceholder(campoDesc, "Ex: RGB, Switch Blue (Máx 100 carac.)");
        configurarPlaceholder(campoPreco, "Ex: 99.90 (Use ponto)");
        configurarPlaceholder(campoQtd, "Ex: 10 (Apenas inteiros)");

        painelCampos.add(new JLabel("Nome:")); painelCampos.add(campoNome);
        painelCampos.add(new JLabel("Descrição:")); painelCampos.add(campoDesc);
        painelCampos.add(new JLabel("Preço:")); painelCampos.add(campoPreco);
        painelCampos.add(new JLabel("Quantidade:")); painelCampos.add(campoQtd);

        JButton btnSalvar = new JButton("CADASTRAR PRODUTO");
        btnSalvar.addActionListener(e -> {
            try {
                String nome = campoNome.getText().equals("Ex: Teclado (Máx 50 carac.)") ? "" : campoNome.getText();
                String descricao = campoDesc.getText().equals("Ex: RGB, Switch Blue (Máx 100 carac.)") ? "" : campoDesc.getText();
                String precoTexto = campoPreco.getText().equals("Ex: 99.90 (Use ponto)") ? "" : campoPreco.getText();
                String qtdTexto = campoQtd.getText().equals("Ex: 10 (Apenas inteiros)") ? "" : campoQtd.getText();

                // 1. Criamos a "caixa" vazia
                Produto novoProduto = new Produto();
                
                // 2. Preenchemos a caixa com os dados da tela
                novoProduto.setNome(nome);
                novoProduto.setDescricao(descricao);
                novoProduto.setPreco(Double.parseDouble(precoTexto));
                novoProduto.setQuantidade(Integer.parseInt(qtdTexto));
                
                // 3. Entregamos a caixa pronta para o DAO salvar
                produtoDAO.cadastrar(novoProduto);
                
                atualizarTabela();
                dialog.dispose(); 
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Aviso: Preencha Preço e Quantidade corretamente.");
            }
        });

        dialog.add(painelCampos, BorderLayout.CENTER);
        dialog.add(btnSalvar, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(null); 
        dialog.setVisible(true);
    }

    public void abrirJanelaEdicao(int linhaSelecionada) {
        JDialog dialog = new JDialog((Frame) null, "Editar Produto", true);
        dialog.setLayout(new BorderLayout());
        dialog.setPreferredSize(new Dimension(400, 300)); 

        int idProduto = (int) modelo.getValueAt(linhaSelecionada, 0);
        String nomeProduto = (String) modelo.getValueAt(linhaSelecionada, 1);
        String descProduto = (String) modelo.getValueAt(linhaSelecionada, 2);
        double precoProduto = (Double) modelo.getValueAt(linhaSelecionada, 3);
        int qtdProduto = (int) modelo.getValueAt(linhaSelecionada, 4);

        JPanel painelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        
        JTextField campoNome = new JTextField();
        JTextField campoDesc = new JTextField();
        JTextField campoPreco = new JTextField(String.valueOf(precoProduto));
        JTextField campoQtd = new JTextField(String.valueOf(qtdProduto));

        campoNome.setDocument(new LimitadorCaracteres(50));
        campoDesc.setDocument(new LimitadorCaracteres(100));
        
        campoNome.setText(nomeProduto);
        campoDesc.setText(descProduto);

        painelCampos.add(new JLabel("Nome:")); painelCampos.add(campoNome);
        painelCampos.add(new JLabel("Descrição:")); painelCampos.add(campoDesc);
        painelCampos.add(new JLabel("Preço:")); painelCampos.add(campoPreco);
        painelCampos.add(new JLabel("Quantidade:")); painelCampos.add(campoQtd);

        JButton btnAtualizar = new JButton("SALVAR ALTERAÇÕES");
        btnAtualizar.addActionListener(e -> {
            try {
                // 1. Criamos a "caixa" vazia
                Produto produtoEditado = new Produto();
                
                // 2. Preenchemos a caixa, incluindo o ID!
                produtoEditado.setId(idProduto);
                produtoEditado.setNome(campoNome.getText());
                produtoEditado.setDescricao(campoDesc.getText());
                produtoEditado.setPreco(Double.parseDouble(campoPreco.getText()));
                produtoEditado.setQuantidade(Integer.parseInt(campoQtd.getText()));
                
                // 3. Entregamos a caixa para o DAO atualizar
                produtoDAO.editar(produtoEditado);
                
                atualizarTabela();
                dialog.dispose(); 
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Aviso: Preencha Preço e Quantidade corretamente.");
            }
        });

        dialog.add(painelCampos, BorderLayout.CENTER);
        dialog.add(btnAtualizar, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(null); 
        dialog.setVisible(true);
    }

    // --- MÉTODOS AUXILIARES VISUAIS (Placeholders e Travas continuam aqui) ---
    private void configurarPlaceholder(JTextField campo, String textoPlaceholder) {
        campo.setText(textoPlaceholder);
        campo.setForeground(Color.GRAY); 

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(textoPlaceholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().trim().isEmpty()) {
                    campo.setText(textoPlaceholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
    }

    class LimitadorCaracteres extends PlainDocument {
        private int limiteMaximo;
        public LimitadorCaracteres(int limite) { this.limiteMaximo = limite; }
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limiteMaximo) {
                super.insertString(offs, str, a);
            }
        }
    }
}