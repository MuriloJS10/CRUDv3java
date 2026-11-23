
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class CadastroAlunos extends JFrame {

    ArrayList<Aluno> alunos = new ArrayList<>();

    JTextField nome = new JTextField();
    JTextField cpf = new JTextField();
    JTextField nomedopai = new JTextField();
    JTextField nomedamae = new JTextField();
    JTextField datadenascimento = new JTextField();
    JTextField telefonedoresponsavel = new JTextField();

    File fotodoestudante;

    DefaultTableModel modelo;
    JTable tabela;

    // Classe dos alunos
    class Aluno {
        String nome, cpf, nomedopai, nomedamae;
        String datadenascimento, telefonedoresponsavel;
        File fotodoestudante;

        Aluno(String nome, String cpf, String nomedopai,
              String nomedamae, String datadenascimento,
              String telefonedoresponsavel, File foto) {

            this.nome = nome;
            this.cpf = cpf;
            this.nomedopai = nomedopai;
            this.nomedamae = nomedamae;
            this.datadenascimento = datadenascimento;
            this.telefonedoresponsavel = telefonedoresponsavel;
            this.fotodoestudante = foto;
        }
    }

    // Construtor
    public CadastroAlunos() {

        setTitle("Cadastro de Alunos");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel painel = new JPanel(new GridLayout(8, 2, 5, 5));

        painel.add(new JLabel("Nome:"));
        painel.add(nome);

        painel.add(new JLabel("CPF:"));
        painel.add(cpf);

        painel.add(new JLabel("Nome do pai:"));
        painel.add(nomedopai);

        painel.add(new JLabel("Nome da mãe:"));
        painel.add(nomedamae);

        painel.add(new JLabel("Data de nascimento:"));
        painel.add(datadenascimento);

        painel.add(new JLabel("Telefone do responsável:"));
        painel.add(telefonedoresponsavel);

        JButton selecionarFoto = new JButton("Selecionar Foto");
        JButton adicionar = new JButton("Adicionar Aluno");

        painel.add(selecionarFoto);
        painel.add(adicionar);

        add(painel, BorderLayout.NORTH);

        selecionarFoto.addActionListener(e -> selecionarFoto());

        adicionar.addActionListener(e -> adicionarAlunos());

        // Tabela
        modelo = new DefaultTableModel(
            new String[]{
                "Nome", "CPF", "Pai", "Mãe",
                "Nascimento", "Telefone", "Foto"
            }, 0
        );

        tabela = new JTable(modelo);

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Botões de edição e exclusão
        JPanel botoes = new JPanel();

        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");
        JButton editarFoto = new JButton("Editar Foto");
        JButton excluirFoto = new JButton("Excluir Foto");

        botoes.add(editar);
        botoes.add(excluir);
        botoes.add(editarFoto);
        botoes.add(excluirFoto);

        add(botoes, BorderLayout.SOUTH);

        editar.addActionListener(e -> editarEstudante());
        excluir.addActionListener(e -> excluirEstudante());
        editarFoto.addActionListener(e -> editarFoto());
        excluirFoto.addActionListener(e -> excluirFoto());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Validar CPF
    boolean validarCPF(String cpf) {
        String regex = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$";
        return cpf.matches(regex);
    }

    // Validar foto
    boolean validarFoto(File foto) {

        if (foto == null ||
            !foto.getName().matches("(?i).+\\.(jpg|jpeg|png|gif|bmp|webp)$")) {

            JOptionPane.showMessageDialog(
                this, "O arquivo deve ser uma imagem!"
            );

            return false;
        }

        return true;
    }

    // Selecionar foto
    void selecionarFoto() {

        JFileChooser escolher = new JFileChooser();

        escolher.setFileFilter(
            new FileNameExtensionFilter(
                "Imagens", "jpg", "jpeg", "png", "gif", "bmp", "webp"
            )
        );

        if (escolher.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fotodoestudante = escolher.getSelectedFile();
        }
    }

    // Adicionar alunos
    void adicionarAlunos() {

        String n = nome.getText().trim();
        String c = cpf.getText().trim();
        String pai = nomedopai.getText().trim();
        String mae = nomedamae.getText().trim();
        String nascimento = datadenascimento.getText().trim();
        String telefone = telefonedoresponsavel.getText().trim();

        boolean camposInvalidos = false;

        String apenasLetras = "^[\\p{L}\\s]+$";

        if (n.isEmpty() || !n.matches(apenasLetras)) {
            JOptionPane.showMessageDialog(this, "Apenas letras no nome!");
            camposInvalidos = true;
        }

        if (c.isEmpty() || !validarCPF(c)) {
            JOptionPane.showMessageDialog(this, "CPF inválido!");
            camposInvalidos = true;
        }

        if (pai.isEmpty() || !pai.matches(apenasLetras)) {
            JOptionPane.showMessageDialog(this, "Apenas letras no nome do pai!");
            camposInvalidos = true;
        }

        if (mae.isEmpty() || !mae.matches(apenasLetras)) {
            JOptionPane.showMessageDialog(this, "Apenas letras no nome da mãe!");
            camposInvalidos = true;
        }

        if (nascimento.isEmpty()) {
            camposInvalidos = true;
        }

        if (telefone.isEmpty() || !telefone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Apenas números no telefone!");
            camposInvalidos = true;
        }

        if (fotodoestudante == null || !validarFoto(fotodoestudante)) {
            JOptionPane.showMessageDialog(this, "Favor adicionar uma foto.");
            camposInvalidos = true;
        }

        if (camposInvalidos) {
            JOptionPane.showMessageDialog(
                this, "Existem campos inválidos. Por favor, corrija-os."
            );
            return;
        }

        alunos.add(new Aluno(
            n, c, pai, mae, nascimento, telefone, fotodoestudante
        ));

        limparCampos();

        JOptionPane.showMessageDialog(
            this, "Aluno adicionado com sucesso!"
        );

        listarAlunos();
    }

    // Limpar campos
    void limparCampos() {

        nome.setText("");
        cpf.setText("");
        nomedopai.setText("");
        nomedamae.setText("");
        datadenascimento.setText("");
        telefonedoresponsavel.setText("");

        fotodoestudante = null;
    }

    // Listar alunos
    void listarAlunos() {

        modelo.setRowCount(0);

        for (Aluno aluno : alunos) {

            modelo.addRow(new Object[]{
                aluno.nome,
                aluno.cpf,
                aluno.nomedopai,
                aluno.nomedamae,
                aluno.datadenascimento,
                aluno.telefonedoresponsavel,
                aluno.fotodoestudante != null
                    ? aluno.fotodoestudante.getName()
                    : "Sem foto"
            });
        }
    }

    // Obter aluno selecionado
    int alunoSelecionado() {

        int index = tabela.getSelectedRow();

        if (index == -1) {
            JOptionPane.showMessageDialog(
                this, "Selecione um aluno na tabela!"
            );
            return -1;
        }

        return tabela.convertRowIndexToModel(index);
    }

    // Editar foto
    void editarFoto() {

        int index = alunoSelecionado();

        if (index == -1) return;

        JFileChooser escolher = new JFileChooser();

        escolher.setFileFilter(
            new FileNameExtensionFilter(
                "Imagens", "jpg", "jpeg", "png", "gif", "bmp", "webp"
            )
        );

        if (escolher.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            File novaFoto = escolher.getSelectedFile();

            if (validarFoto(novaFoto)) {
                alunos.get(index).fotodoestudante = novaFoto;
                listarAlunos();
            }
        }
    }

    // Editar alunos
    void editarEstudante() {

        int index = alunoSelecionado();

        if (index == -1) return;

        Aluno aluno = alunos.get(index);

        String novoNome = JOptionPane.showInputDialog(
            this, "Novo nome:", aluno.nome
        );

        if (novoNome == null) return;

        String novoCpf = JOptionPane.showInputDialog(
            this, "Novo CPF:", aluno.cpf
        );

        if (novoCpf == null) return;

        String novoPai = JOptionPane.showInputDialog(
            this, "Novo nome do pai:", aluno.nomedopai
        );

        if (novoPai == null) return;

        String novaMae = JOptionPane.showInputDialog(
            this, "Novo nome da mãe:", aluno.nomedamae
        );

        if (novaMae == null) return;

        String novoNascimento = JOptionPane.showInputDialog(
            this, "Nova data de nascimento:", aluno.datadenascimento
        );

        if (novoNascimento == null) return;

        String novoTelefone = JOptionPane.showInputDialog(
            this, "Novo telefone:", aluno.telefonedoresponsavel
        );

        if (novoTelefone == null) return;

        alunos.set(index, new Aluno(
            novoNome.trim(),
            novoCpf.trim(),
            novoPai.trim(),
            novaMae.trim(),
            novoNascimento.trim(),
            novoTelefone.trim(),
            aluno.fotodoestudante
        ));

        listarAlunos();
    }

    // Excluir alunos
    void excluirEstudante() {

        int index = alunoSelecionado();

        if (index == -1) return;

        int resposta = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir este aluno?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            alunos.remove(index);
            listarAlunos();
        }
    }

    // Excluir foto
    void excluirFoto() {

        int index = alunoSelecionado();

        if (index == -1) return;

        int resposta = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir a foto deste aluno?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            alunos.get(index).fotodoestudante = null;
            listarAlunos();
        }
    }

    // Executar programa
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CadastroAlunos());
    }
}

