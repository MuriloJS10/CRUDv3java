-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 25/11/2026 às 02:50
-- Versão do servidor: 10.11.19-MariaDB
-- Versão do PHP: 8.5.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `portfolio`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `postagem`
--

CREATE TABLE `postagem` (
  `id` int(11) NOT NULL,
  `titulo` varchar(180) NOT NULL,
  `conteudo` text NOT NULL,
  `imagem` varchar(255) DEFAULT NULL,
  `autor_id` int(11) NOT NULL,
  `data_publicacao` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` varchar(20) NOT NULL DEFAULT 'PUBLICADA'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `postagem`
--

INSERT INTO `postagem` (`id`, `titulo`, `conteudo`, `imagem`, `autor_id`, `data_publicacao`, `status`) VALUES
(2, 'Trabalho de Programação', 'Github, banco de dados MySQL, Java', 'nao tem', 3, '2026-11-23 01:31:54', 'Finalizado'),
(3, 'Festa de Final de Ano', 'Musica sem bike swifty', 'esgoto', 4, '2026-11-23 01:41:57', 'A fazer'),
(6, 'Aniversario de 17 anos', 'Trucka Boesing', 'sem imagem', 4, '2026-11-23 01:42:55', 'a fazer'),
(7, 'senhor dos aneis', 'livro de magia', 'naosei', 7, '2026-11-24 00:31:08', 'fazendo');

-- --------------------------------------------------------

--
-- Estrutura para tabela `project`
--

CREATE TABLE `project` (
  `id` int(11) NOT NULL,
  `titulo` varchar(100) NOT NULL,
  `descricao` text NOT NULL,
  `link` varchar(250) NOT NULL,
  `tecnologias` varchar(250) NOT NULL,
  `data_criacao` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `project`
--

INSERT INTO `project` (`id`, `titulo`, `descricao`, `link`, `tecnologias`, `data_criacao`) VALUES
(1, 'PHPMyAdmin', 'Administrar o banco de Dados', 'www.palmeiras.com.br', 'Java, VSCod, Github, MySQL', '22/11/2026'),
(2, 'Fazer Vestibular', 'IFC Medicina', 'www.ifc.edu.br', 'Sangue, Cortes', '22/11/2026'),
(3, 'harry potter', 'livro de magia com feitiços', 'lalalala', 'hidrofóbica', '2022-02-22');

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuario`
--

CREATE TABLE `usuario` (
  `id` int(11) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `data_cadastro` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `usuario`
--

INSERT INTO `usuario` (`id`, `nome`, `email`, `senha`, `data_cadastro`) VALUES
(3, 'Murilo J. Simionatto', 'murilojsimionatto@gmail.com', 'mimifeio', '2026-11-23 01:31:34'),
(4, 'Érico Simioonatto', 'ericosimionatto@gmail.com', 'mimifeio2', '2026-11-23 01:40:01'),
(5, 'Lilian Ramos', 'lilianramos@ifc.gov.br', 'mimifeio5', '2026-11-23 01:40:36'),
(7, 'pessoachata', 'pessoachata@gmail.com', 'lalalalala', '2026-11-24 00:30:14');

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `postagem`
--
ALTER TABLE `postagem`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_postagem_autor` (`autor_id`);

--
-- Índices de tabela `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_usuario_email` (`email`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `postagem`
--
ALTER TABLE `postagem`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de tabela `project`
--
ALTER TABLE `project`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de tabela `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `postagem`
--
ALTER TABLE `postagem`
  ADD CONSTRAINT `fk_postagem_usuario` FOREIGN KEY (`autor_id`) REFERENCES `usuario` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
