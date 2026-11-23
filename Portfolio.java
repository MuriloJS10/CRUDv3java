
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

// SISTEMA DE GERENCIAMENTO PORTFOLIO
public class Portfolio extends JFrame {

    // CONEXÃO COM MYSQL
    private static final String URL =
        "jdbc:mysql://localhost:3306/portfolio";

    private static final String USUARIO = "root";
    private static final String SENHA = "";

    // CONFIGURAÇÃO DAS TABELAS
    private static class Configuracao {

        String tabela;
        String[] campos;
        String[] nomes;
        String[] obrigatorios;

        Configuracao(String tabela, String[] campos,
                     String[] nomes, String[] obrigatorios) {

            this.tabela = tabela;
            this.campos = campos;
            this.nomes = nomes;
            this.obrigatorios = obrigatorios;
        }
    }

    // ORDEM OBRIGATÓRIA DAS TABELAS
    private final Configuracao[] configuracoes = {

        // 1. USUÁRIOS
        new Configuracao(
            "usuario",
            new String[]{
                "nome", "email", "senha"
            },
            new String[]{
                "Nome", "E-mail", "Senha"
            },
            new String[]{
                "nome", "email", "senha"
            }
        ),

        // 2. POSTAGENS
        new Configuracao(
            "postagem",
            new String[]{
                "titulo", "conteudo", "imagem",
                "autor_id", "status"
            },
            new String[]{
                "Título", "Conteúdo", "Imagem",
                "ID do autor", "Status"
            },
            new String[]{
                "titulo", "conteudo", "autor_id", "status"
            }
        ),

        // 3. PROJETOS
        new Configuracao(
            "project",
            new String[]{
                "titulo", "descricao", "link",
                "tecnologias", "data_criacao"
            },
            new String[]{
                "Título", "Descrição", "Link",
                "Tecnologias", "Data de criação"
            },
            new String[]{
                "titulo", "descricao", "link",
                "tecnologias", "data_criacao"
            }
        )
    };

    // ELEMENTOS DA INTERFACE
    private JTabbedPane abas;

    private ArrayList<TelaTabela> telas =
        new ArrayList<>();

    private TelaTabela telaAtual;

    // CLASSE QUE REPRESENTA CADA TELA
    private class TelaTabela {

        Configuracao config;

        JPanel painel;

        JPanel formulario;

        Map<String, JTextField> campos =
            new LinkedHashMap<>();

        JTable tabela;

        DefaultTableModel modelo;

        TelaTabela(Configuracao config) {

            this.config = config;

            criarTela();
        }

        // CRIAR INTERFACE
        private void criarTela() {

            painel = new JPanel(new BorderLayout(10, 10));

            painel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            );

            // FORMULÁRIO
            formulario = new JPanel(
                new GridLayout(0, 2, 10, 10)
            );

            formulario.setBorder(
                BorderFactory.createTitledBorder(
                    "Dados de " + config.tabela.toUpperCase()
                )
            );

            for (int i = 0; i < config.campos.length; i++) {

                JLabel label = new JLabel(config.nomes[i]);

                JTextField campo = new JTextField();

                campos.put(config.campos[i], campo);

                formulario.add(label);
                formulario.add(campo);
            }

            // BOTÕES CRUD
            JPanel botoes = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 10)
            );

            JButton create = new JButton("Create");
            JButton read = new JButton("Read");
            JButton update = new JButton("Update");
            JButton delete = new JButton("Delete");

            botoes.add(create);
            botoes.add(read);
            botoes.add(update);
            botoes.add(delete);

            // TABELA
            modelo = new DefaultTableModel();

            tabela = new JTable(modelo);

            tabela.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
            );

            tabela.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
            );

            JScrollPane scroll = new JScrollPane(tabela);

            // ORGANIZAÇÃO
            painel.add(formulario, BorderLayout.NORTH);
            painel.add(scroll, BorderLayout.CENTER);
            painel.add(botoes, BorderLayout.SOUTH);

            // AÇÕES DOS BOTÕES
            create.addActionListener(e -> cadastrar(this));

            read.addActionListener(e -> consultar(this));

            update.addActionListener(e -> atualizar(this));

            delete.addActionListener(e -> excluir(this));

            // SELECIONAR REGISTRO
            tabela.getSelectionModel().addListSelectionListener(e -> {

                if (!e.getValueIsAdjusting()) {
                    preencherCampos(this);
                }
            });
        }
    }

    // CONSTRUTOR
    public Portfolio() {

        setTitle("Portfolio - Gerenciamento");

        setSize(1100, 700);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        abas = new JTabbedPane();

        // CRIAR AS TRÊS TELAS
        for (Configuracao config : configuracoes) {

            TelaTabela tela = new TelaTabela(config);

            telas.add(tela);

            abas.addTab(
                config.tabela.toUpperCase(),
                tela.painel
            );
        }

        add(abas);

        // CONTROLE DE ACESSO ÀS ABAS
        abas.addChangeListener(e -> {

            int index = abas.getSelectedIndex();

            if (index < 0) return;

            if (index > 0 && !temUsuariosCadastrados()) {

                abas.setSelectedIndex(0);

                JOptionPane.showMessageDialog(
                    this,
                    "Primeiro, cadastre pelo menos um usuário!",
                    "Acesso bloqueado",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            telaAtual = telas.get(index);

            consultar(telaAtual);
        });

        // INICIAR NA ABA DE USUÁRIOS
        telaAtual = telas.get(0);

        consultar(telaAtual);

        atualizarControleAbas();

        setVisible(true);
    }

    // CONEXÃO COM O BANCO
    private Connection conectar() throws SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {

            throw new SQLException(
                "Driver MySQL não encontrado!", e
            );
        }

        return DriverManager.getConnection(
            URL, USUARIO, SENHA
        );
    }

    // VERIFICAR SE EXISTEM USUÁRIOS
    private boolean temUsuariosCadastrados() {

        String sql = "SELECT COUNT(*) FROM usuario";

        try (Connection conexao = conectar();
             Statement comando = conexao.createStatement();
             ResultSet resultado = comando.executeQuery(sql)) {

            if (resultado.next()) {
                return resultado.getInt(1) > 0;
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Erro ao verificar usuários: " + e.getMessage()
            );
        }

        return false;
    }

    // CONTROLAR ACESSO ÀS ABAS
    private void atualizarControleAbas() {

        boolean existemUsuarios = temUsuariosCadastrados();

        abas.setEnabledAt(0, true);

        abas.setEnabledAt(1, existemUsuarios);

        abas.setEnabledAt(2, existemUsuarios);
    }

    // OBTER DADOS DO FORMULÁRIO
    private Map<String, Object> obterDados(TelaTabela tela) {

        Map<String, Object> dados =
            new LinkedHashMap<>();

        for (String campo : tela.config.campos) {

            String valor = tela.campos.get(campo)
                .getText().trim();

            // IMAGEM PODE FICAR VAZIA
            if (campo.equals("imagem") && valor.isEmpty()) {

                dados.put(campo, null);

                continue;
            }

            // STATUS PADRÃO
            if (campo.equals("status") && valor.isEmpty()) {

                valor = "PUBLICADA";
            }

            // VALIDAR ID DO AUTOR
            if (campo.equals("autor_id")) {

                try {

                    dados.put(
                        campo,
                        Integer.parseInt(valor)
                    );

                } catch (NumberFormatException e) {

                    JOptionPane.showMessageDialog(
                        this,
                        "O ID do autor deve ser numérico!"
                    );

                    return null;
                }

            } else {

                dados.put(campo, valor);
            }
        }

        // VALIDAR CAMPOS OBRIGATÓRIOS
        for (String obrigatorio : tela.config.obrigatorios) {

            Object valor = dados.get(obrigatorio);

            if (valor == null ||
                valor.toString().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Preencha o campo: " + obrigatorio
                );

                return null;
            }
        }

        return dados;
    }

    // CREATE - CADASTRAR
    private void cadastrar(TelaTabela tela) {

        Map<String, Object> dados = obterDados(tela);

        if (dados == null) return;

        StringBuilder sql = new StringBuilder(
            "INSERT INTO " + tela.config.tabela + " ("
        );

        StringBuilder valores = new StringBuilder();

        int contador = 0;

        for (String campo : dados.keySet()) {

            sql.append(campo);

            valores.append("?");

            contador++;

            if (contador < dados.size()) {

                sql.append(", ");

                valores.append(", ");
            }
        }

        sql.append(") VALUES (").append(valores).append(")");

        try (Connection conexao = conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql.toString())) {

            int i = 1;

            for (Object valor : dados.values()) {

                comando.setObject(i++, valor);
            }

            comando.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Registro cadastrado com sucesso!"
            );

            limparCampos(tela);

            consultar(tela);

            atualizarControleAbas();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Erro ao cadastrar: " + e.getMessage()
            );
        }
    }

    // READ - CONSULTAR
    private void consultar(TelaTabela tela) {

        String sql =
            "SELECT * FROM " + tela.config.tabela;

        try (Connection conexao = conectar();
             Statement comando = conexao.createStatement();
             ResultSet resultado = comando.executeQuery(sql)) {

            ResultSetMetaData dados =
                resultado.getMetaData();

            int quantidade = dados.getColumnCount();

            DefaultTableModel novoModelo =
                new DefaultTableModel();

            // CRIAR COLUNAS
            for (int i = 1; i <= quantidade; i++) {

                novoModelo.addColumn(
                    dados.getColumnName(i)
                );
            }

            // ADICIONAR REGISTROS
            while (resultado.next()) {

                Object[] linha = new Object[quantidade];

                for (int i = 1; i <= quantidade; i++) {

                    linha[i - 1] = resultado.getObject(i);
                }

                novoModelo.addRow(linha);
            }

            tela.modelo = novoModelo;

            tela.tabela.setModel(novoModelo);

            tela.tabela.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Erro ao consultar: " + e.getMessage()
            );
        }
    }

    // PREENCHER CAMPOS AO SELECIONAR REGISTRO
    private void preencherCampos(TelaTabela tela) {

        int linha = tela.tabela.getSelectedRow();

        if (linha == -1) return;

        for (String campo : tela.config.campos) {

            int coluna = tela.modelo.findColumn(campo);

            Object valor = tela.tabela.getValueAt(
                linha, coluna
            );

            tela.campos.get(campo).setText(
                valor == null ? "" : valor.toString()
            );
        }
    }

    // OBTER ID DO REGISTRO SELECIONADO
    private int obterId(TelaTabela tela) {

        int linha = tela.tabela.getSelectedRow();

        if (linha == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Selecione um registro na tabela!"
            );

            return -1;
        }

        int coluna = tela.modelo.findColumn("id");

        return Integer.parseInt(
            tela.tabela.getValueAt(linha, coluna).toString()
        );
    }

    // UPDATE - ATUALIZAR
    private void atualizar(TelaTabela tela) {

        int id = obterId(tela);

        if (id == -1) return;

        Map<String, Object> dados = obterDados(tela);

        if (dados == null) return;

        StringBuilder sql = new StringBuilder(
            "UPDATE " + tela.config.tabela + " SET "
        );

        int contador = 0;

        for (String campo : dados.keySet()) {

            sql.append(campo).append(" = ?");

            contador++;

            if (contador < dados.size()) {

                sql.append(", ");
            }
        }

        sql.append(" WHERE id = ?");

        try (Connection conexao = conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql.toString())) {

            int i = 1;

            for (Object valor : dados.values()) {

                comando.setObject(i++, valor);
            }

            comando.setInt(i, id);

            int resultado = comando.executeUpdate();

            if (resultado > 0) {

                JOptionPane.showMessageDialog(
                    this,
                    "Registro atualizado com sucesso!"
                );

                limparCampos(tela);

                consultar(tela);

                atualizarControleAbas();
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Erro ao atualizar: " + e.getMessage()
            );
        }
    }

    // DELETE - EXCLUIR
    private void excluir(TelaTabela tela) {

        int id = obterId(tela);

        if (id == -1) return;

        int resposta = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir este registro?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION
        );

        if (resposta != JOptionPane.YES_OPTION) return;

        String sql =
            "DELETE FROM " + tela.config.tabela +
            " WHERE id = ?";

        try (Connection conexao = conectar();
             PreparedStatement comando =
                 conexao.prepareStatement(sql)) {

            comando.setInt(1, id);

            int resultado = comando.executeUpdate();

            if (resultado > 0) {

                JOptionPane.showMessageDialog(
                    this,
                    "Registro excluído com sucesso!"
                );

                limparCampos(tela);

                consultar(tela);

                atualizarControleAbas();
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Erro ao excluir: " + e.getMessage()
            );
        }
    }

    // LIMPAR CAMPOS
    private void limparCampos(TelaTabela tela) {

        for (JTextField campo : tela.campos.values()) {

            campo.setText("");
        }

        tela.tabela.clearSelection();
    }

    // EXECUTAR SISTEMA
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new Portfolio();
        });
    }
}
