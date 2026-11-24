
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Portfolio extends JFrame {
    
    
    // ==========================================
    // CORES DO SISTEMA - PALETA VERDE
    // ==========================================

    private final Color VERDE_ESCURO = new Color(22, 78, 54);
    private final Color VERDE = new Color(35, 122, 80);
    private final Color VERDE_MEDIO = new Color(85, 168, 121);
    private final Color VERDE_CLARO = new Color(229, 242, 233);
    private final Color FUNDO = new Color(247, 250, 247);
    private final Color BRANCO = Color.WHITE;
    private final Color TEXTO = new Color(45, 65, 53);

    // ==========================================
    // CONEXÃO COM O BANCO DE DADOS
    // ==========================================

    private final String URL =
        "jdbc:mysql://localhost:3306/portfolio?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private final String USUARIO = "root";
    private final String SENHA = "";

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    // ==========================================
    // CONFIGURAÇÕES DAS TABELAS
    // ==========================================

    private static class Configuracao {
        String nomeTabela;
        String titulo;
        String[] colunas;
        String[] campos;
        String[] rotulos;

        Configuracao(
            String nomeTabela,
            String titulo,
            String[] colunas,
            String[] campos,
            String[] rotulos
        ) {
            this.nomeTabela = nomeTabela;
            this.titulo = titulo;
            this.colunas = colunas;
            this.campos = campos;
            this.rotulos = rotulos;
        }
    }

    private final Configuracao configUsuario = new Configuracao(
        "usuario",
        "Gerenciamento de Usuários",
        new String[]{"id", "nome", "email", "senha", "data_cadastro"},
        new String[]{"nome", "email", "senha"},
        new String[]{"Nome", "E-mail", "Senha"}
    );

    private final Configuracao configPostagem = new Configuracao(
        "postagem",
        "Gerenciamento de Postagens",
        new String[]{
            "id", "titulo", "conteudo", "imagem",
            "autor_id", "data_publicacao", "status"
        },
        new String[]{"titulo", "conteudo", "imagem", "autor_id", "status"},
        new String[]{"Título", "Conteúdo", "Imagem", "ID do Autor", "Status"}
    );

    private final Configuracao configProject = new Configuracao(
        "project",
        "Gerenciamento de Projetos",
        new String[]{
            "id", "titulo", "descricao", "link",
            "tecnologias", "data_criacao"
        },
        new String[]{"titulo", "descricao", "link", "tecnologias", "data_criacao"},
        new String[]{"Título", "Descrição", "Link", "Tecnologias", "Data de criação"}
    );

    // ==========================================
    // COMPONENTES PRINCIPAIS
    // ==========================================

    private JTabbedPane abas;

    private TelaTabela telaUsuario;
    private TelaTabela telaPostagem;
    private TelaTabela telaProject;

    // ==========================================
    // CONSTRUTOR
    // ==========================================

    public Portfolio() {

        setTitle("Sistema de Gerenciamento - Portfolio");
        setSize(1100, 700);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(FUNDO);
        setLayout(new BorderLayout());

        criarInterface();

        atualizarControleAbas();

        setVisible(true);
    }

    // ==========================================
    // CRIAÇÃO DA INTERFACE
    // ==========================================

    private void criarInterface() {

        // Cabeçalho

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(VERDE_ESCURO);
        cabecalho.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel titulo = new JLabel("SISTEMA PORTFOLIO");
        titulo.setForeground(BRANCO);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitulo = new JLabel("Gerenciamento de dados");
        subtitulo.setForeground(VERDE_CLARO);
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(VERDE_ESCURO);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        cabecalho.add(textos, BorderLayout.WEST);

        add(cabecalho, BorderLayout.NORTH);

        // Abas

        abas = new JTabbedPane();
        abas.setFont(new Font("Arial", Font.BOLD, 14));
        abas.setBackground(FUNDO);
        abas.setForeground(TEXTO);
        abas.setBorder(
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );

        // Criação das telas

        telaUsuario = new TelaTabela(configUsuario);
        telaPostagem = new TelaTabela(configPostagem);
        telaProject = new TelaTabela(configProject);

        // Ordem das abas

        abas.addTab("Usuários", telaUsuario);
        abas.addTab("Postagens", telaPostagem);
        abas.addTab("Projetos", telaProject);

        add(abas, BorderLayout.CENTER);

        // Rodapé

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.setBackground(VERDE_ESCURO);
        rodape.setBorder(
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );

        JLabel textoRodape = new JLabel("Sistema de gerenciamento de dados");
        textoRodape.setForeground(BRANCO);
        textoRodape.setFont(new Font("Arial", Font.PLAIN, 12));

        rodape.add(textoRodape);

        add(rodape, BorderLayout.SOUTH);
    }

    // ==========================================
    // ESTILIZAÇÃO DOS BOTÕES
    // ==========================================

    private void estilizarBotao(
        JButton botao,
        Color cor,
        Color corTexto
    ) {

        botao.setBackground(cor);
        botao.setForeground(corTexto);
        botao.setFont(new Font("Arial", Font.BOLD, 13));

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);

        botao.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        botao.setPreferredSize(new Dimension(115, 38));
    }

    // ==========================================
    // ESTILIZAÇÃO DOS CAMPOS
    // ==========================================

    private void estilizarCampo(JTextField campo) {

        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setForeground(TEXTO);
        campo.setBackground(BRANCO);
        campo.setCaretColor(VERDE);

        campo.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(190, 215, 197), 1
                ),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
            )
        );
    }

    // ==========================================
    // ESTILIZAÇÃO DA TABELA
    // ==========================================

    private void estilizarTabela(JTable tabela) {

        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.setRowHeight(30);

        tabela.setForeground(TEXTO);
        tabela.setBackground(BRANCO);

        tabela.setSelectionBackground(VERDE_CLARO);
        tabela.setSelectionForeground(VERDE_ESCURO);

        tabela.setGridColor(new Color(220, 232, 222));
        tabela.setShowVerticalLines(false);

        tabela.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        JTableHeader cabecalho = tabela.getTableHeader();

        cabecalho.setBackground(VERDE_ESCURO);
        cabecalho.setForeground(BRANCO);
        cabecalho.setFont(new Font("Arial", Font.BOLD, 13));
        cabecalho.setPreferredSize(new Dimension(0, 35));

        cabecalho.setReorderingAllowed(false);
    }

    // ==========================================
    // VERIFICA SE EXISTEM USUÁRIOS
    // ==========================================

    private boolean temUsuariosCadastrados() {

        String sql = "SELECT COUNT(*) FROM usuario";

        try (
            Connection conexao = conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery()
        ) {

            if (resultado.next()) {
                return resultado.getInt(1) > 0;
            }

        } catch (SQLException e) {
            mostrarErro(e);
        }

        return false;
    }

    // ==========================================
    // CONTROLE DAS ABAS
    // ==========================================

    private void atualizarControleAbas() {

        boolean existemUsuarios = temUsuariosCadastrados();

        abas.setEnabledAt(0, true);
        abas.setEnabledAt(1, existemUsuarios);
        abas.setEnabledAt(2, existemUsuarios);

        if (!existemUsuarios) {
            abas.setSelectedIndex(0);
        }
    }

    // ==========================================
    // CLASSE RESPONSÁVEL POR CADA TABELA
    // ==========================================

    private class TelaTabela extends JPanel {

        private Configuracao config;

        private JTable tabela;
        private DefaultTableModel modelo;

        private JTextField[] campos;

        private JPanel formulario;

        private TelaTabela(Configuracao config) {

            this.config = config;

            criarTela();
            consultar();
        }

        // ======================================
        // CRIAÇÃO DA TELA
        // ======================================

        private void criarTela() {

            setLayout(new BorderLayout(12, 12));
            setBackground(FUNDO);

            setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            );

            // Painel do formulário

            formulario = new JPanel(
                new GridLayout(0, 2, 12, 10)
            );

            formulario.setBackground(FUNDO);

            formulario.setBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(VERDE, 1),
                    config.titulo,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 14),
                    VERDE_ESCURO
                )
            );

            campos = new JTextField[config.campos.length];

            for (int i = 0; i < config.campos.length; i++) {

                JLabel rotulo = new JLabel(config.rotulos[i]);

                rotulo.setFont(
                    new Font("Arial", Font.BOLD, 12)
                );

                rotulo.setForeground(TEXTO);

                JTextField campo = new JTextField();

                estilizarCampo(campo);

                campos[i] = campo;

                formulario.add(rotulo);
                formulario.add(campo);
            }

            add(formulario, BorderLayout.NORTH);

            // Tabela

            modelo = new DefaultTableModel();

            tabela = new JTable(modelo);

            estilizarTabela(tabela);

            JScrollPane rolagem = new JScrollPane(tabela);
            rolagem.getViewport().setBackground(BRANCO);
            rolagem.setBorder(
                BorderFactory.createLineBorder(
                    new Color(190, 215, 197)
                )
            );

            add(rolagem, BorderLayout.CENTER);

            // Botões

            JPanel botoes = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 8)
            );

            botoes.setBackground(FUNDO);

            JButton create = new JButton("Create");
            JButton read = new JButton("Read");
            JButton update = new JButton("Update");
            JButton delete = new JButton("Delete");

            estilizarBotao(create, VERDE_ESCURO, BRANCO);
            estilizarBotao(read, VERDE, BRANCO);
            estilizarBotao(update, VERDE_MEDIO, BRANCO);
            estilizarBotao(delete, new Color(116, 142, 121), BRANCO);

            botoes.add(create);
            botoes.add(read);
            botoes.add(update);
            botoes.add(delete);

            add(botoes, BorderLayout.SOUTH);

            // Ações dos botões

            create.addActionListener(e -> cadastrar());
            read.addActionListener(e -> consultar());
            update.addActionListener(e -> atualizar());
            delete.addActionListener(e -> excluir());

            // Preenche formulário ao selecionar uma linha

            tabela.getSelectionModel().addListSelectionListener(e -> {

                if (!e.getValueIsAdjusting()) {
                    preencherCampos();
                }
            });
        }

        // ======================================
        // CREATE - CADASTRAR
        // ======================================

        private void cadastrar() {

            StringBuilder sql = new StringBuilder(
                "INSERT INTO " + config.nomeTabela + " ("
            );

            for (int i = 0; i < config.campos.length; i++) {

                sql.append(config.campos[i]);

                if (i < config.campos.length - 1) {
                    sql.append(", ");
                }
            }

            sql.append(") VALUES (");

            for (int i = 0; i < config.campos.length; i++) {

                sql.append("?");

                if (i < config.campos.length - 1) {
                    sql.append(", ");
                }
            }

            sql.append(")");

            try (Connection conexao = conectar();
                 PreparedStatement stmt =
                     conexao.prepareStatement(sql.toString())) {

                for (int i = 0; i < campos.length; i++) {

                    String valor = campos[i].getText().trim();

                    if (valor.isEmpty()) {

                        // Imagem é opcional.

                        if (config.nomeTabela.equals("postagem")
                            && config.campos[i].equals("imagem")) {

                            stmt.setNull(i + 1, Types.VARCHAR);
                            continue;
                        }

                        // Campos vazios opcionais.

                        if (config.nomeTabela.equals("postagem")
                            && config.campos[i].equals("autor_id")) {

                            mostrarMensagem(
                                "Informe o ID do usuário que publicou a postagem."
                            );
                            return;
                        }

                        mostrarMensagem(
                            "Preencha o campo: " + config.rotulos[i]
                        );

                        return;
                    }

                    if (config.nomeTabela.equals("project")
                        && config.campos[i].equals("data_criacao")) {
                        LocalDate data = LocalDate.parse(
                            valor, DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        );
                        stmt.setDate(i + 1, java.sql.Date.valueOf(data));
                    } else {
                        stmt.setString(i + 1, valor);
                    }
                }

                stmt.executeUpdate();

                mostrarMensagem("Cadastro realizado com sucesso!");

                limparCampos();
                consultar();

                if (config.nomeTabela.equals("usuario")) {
                    atualizarControleAbas();
                }

            } catch (DateTimeParseException e) {
                mostrarMensagem("Data inválida. Digite no formato dd/MM/aaaa (exemplo: 23/11/2026).");
            } catch (SQLException e) {
                mostrarErro(e);
            }
        }

        // ======================================
        // READ - CONSULTAR
        // ======================================

        private void consultar() {

            String sql = "SELECT * FROM " + config.nomeTabela;

            try (Connection conexao = conectar();
                 PreparedStatement stmt =
                     conexao.prepareStatement(sql);
                 ResultSet resultado = stmt.executeQuery()) {

                ResultSetMetaData dados = resultado.getMetaData();

                int quantidadeColunas = dados.getColumnCount();

                modelo = new DefaultTableModel();

                for (int i = 1; i <= quantidadeColunas; i++) {
                    modelo.addColumn(dados.getColumnName(i));
                }

                while (resultado.next()) {

                    Object[] linha = new Object[quantidadeColunas];

                    for (int i = 0; i < quantidadeColunas; i++) {
                        Object valor = resultado.getObject(i + 1);

                        if ("data_criacao".equals(dados.getColumnName(i + 1))
                            && valor instanceof java.util.Date) {
                            valor = new java.text.SimpleDateFormat("dd/MM/yyyy")
                                .format((java.util.Date) valor);
                        }

                        linha[i] = valor;
                    }

                    modelo.addRow(linha);
                }

                tabela.setModel(modelo);

                estilizarTabela(tabela);

            } catch (SQLException e) {
                mostrarErro(e);
            }
        }

        // ======================================
        // UPDATE - ATUALIZAR
        // ======================================

        private void atualizar() {

            int linha = tabela.getSelectedRow();

            if (linha == -1) {
                mostrarMensagem(
                    "Selecione um registro da tabela para atualizar."
                );
                return;
            }

            Object id = tabela.getValueAt(linha, 0);

            StringBuilder sql = new StringBuilder(
                "UPDATE " + config.nomeTabela + " SET "
            );

            for (int i = 0; i < config.campos.length; i++) {

                sql.append(config.campos[i]).append(" = ?");

                if (i < config.campos.length - 1) {
                    sql.append(", ");
                }
            }

            sql.append(" WHERE id = ?");

            try (Connection conexao = conectar();
                 PreparedStatement stmt =
                     conexao.prepareStatement(sql.toString())) {

                for (int i = 0; i < campos.length; i++) {

                    String valor = campos[i].getText().trim();

                    if (valor.isEmpty()) {

                        if (config.nomeTabela.equals("postagem")
                            && config.campos[i].equals("imagem")) {

                            stmt.setNull(i + 1, Types.VARCHAR);
                            continue;
                        }

                        mostrarMensagem(
                            "Preencha o campo: " + config.rotulos[i]
                        );

                        return;
                    }

                    if (config.nomeTabela.equals("project")
                        && config.campos[i].equals("data_criacao")) {
                        LocalDate data = LocalDate.parse(
                            valor, DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        );
                        stmt.setDate(i + 1, java.sql.Date.valueOf(data));
                    } else {
                        stmt.setString(i + 1, valor);
                    }
                }

                stmt.setObject(campos.length + 1, id);

                stmt.executeUpdate();

                mostrarMensagem("Registro atualizado com sucesso!");

                limparCampos();
                consultar();

            } catch (DateTimeParseException e) {
                mostrarMensagem("Data inválida. Digite no formato dd/MM/aaaa (exemplo: 23/11/2026).");
            } catch (SQLException e) {
                mostrarErro(e);
            }
        }

        // ======================================
        // DELETE - EXCLUIR
        // ======================================

        private void excluir() {

            int linha = tabela.getSelectedRow();

            if (linha == -1) {
                mostrarMensagem(
                    "Selecione um registro da tabela para excluir."
                );
                return;
            }

            Object id = tabela.getValueAt(linha, 0);

            int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir este registro?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirmacao != JOptionPane.YES_OPTION) {
                return;
            }

            String sql =
                "DELETE FROM " + config.nomeTabela + " WHERE id = ?";

            try (Connection conexao = conectar();
                 PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

                stmt.setObject(1, id);

                stmt.executeUpdate();

                mostrarMensagem("Registro excluído com sucesso!");

                limparCampos();
                consultar();

                if (config.nomeTabela.equals("usuario")) {
                    atualizarControleAbas();
                }

            } catch (SQLException e) {
                mostrarErro(e);
            }
        }

        // ======================================
        // PREENCHER CAMPOS AO SELECIONAR LINHA
        // ======================================

        private void preencherCampos() {

            int linha = tabela.getSelectedRow();

            if (linha == -1) {
                return;
            }

            for (int i = 0; i < config.campos.length; i++) {

                String coluna = config.campos[i];

                int indice = -1;

                for (int j = 0; j < tabela.getColumnCount(); j++) {

                    if (tabela.getColumnName(j).equals(coluna)) {
                        indice = j;
                        break;
                    }
                }

                if (indice >= 0) {

                    Object valor = tabela.getValueAt(linha, indice);

                    campos[i].setText(
                        valor == null ? "" : valor.toString()
                    );
                }
            }
        }

        // ======================================
        // LIMPAR CAMPOS
        // ======================================

        private void limparCampos() {

            for (JTextField campo : campos) {
                campo.setText("");
            }

            tabela.clearSelection();
        }
    }

    // ==========================================
    // MENSAGENS DO SISTEMA
    // ==========================================

    private void mostrarMensagem(String mensagem) {

        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Sistema Portfolio",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void mostrarErro(SQLException erro) {

        JOptionPane.showMessageDialog(
            this,
            "Ocorreu um erro ao acessar o banco de dados:\n"
                + erro.getMessage(),
            "Erro",
            JOptionPane.ERROR_MESSAGE
        );

        erro.printStackTrace();
    }

    // ==========================================
    // EXECUÇÃO DO PROGRAMA
    // ==========================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            new Portfolio();
        });
    }
}
