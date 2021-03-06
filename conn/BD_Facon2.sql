drop database if exists facon_database;

create database if not exists facon_database 
default character set utf8
default collate utf8_general_ci;

use facon_database;

#--------------- TABELAS ---------------#

create table if not exists tbl_usuario(

id INTEGER PRIMARY KEY AUTO_INCREMENT,
nome VARCHAR(100) NOT NULL,
dtNascimento DATE NOT NULL,
cpf VARCHAR(11) UNIQUE NOT NULL,
rg VARCHAR(9) NOT NULL,
email VARCHAR(60) UNIQUE NOT NULL,
endCep VARCHAR(8) NULL,
endBairro VARCHAR(60) NULL,
endRua VARCHAR(60) NULL,
endNum VARCHAR(50) NULL,
endCidade VARCHAR(60) NULL,
endEstado VARCHAR(2) NULL,
telFixo VARCHAR(10) NULL,
telCell VARCHAR(11) UNIQUE NOT NULL,
foto TEXT NULL,
ativo INT NOT NULL DEFAULT 1,
senha TEXT NOT NULL,
idFb TEXT NOT NULL
);

create table if not exists tbl_tipoServico(

id INTEGER PRIMARY KEY AUTO_INCREMENT,
profissao VARCHAR(100) UNIQUE NOT NULL,
categoria VARCHAR(100) NOT NULL
);

create table if not exists tbl_profissional(

idProfissional INTEGER PRIMARY KEY AUTO_INCREMENT,
idUsuario INTEGER NOT NULL UNIQUE,
idProfissao INTEGER NOT NULL,
descricao VARCHAR(400) NOT NULL DEFAULT "Sem descrição.",
formacao VARCHAR(400) NULL DEFAULT "Sem formação declarada.",
dtExperiencia DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_avaliacao(
id integer primary key auto_increment, 
idProfissional integer not null, 
idUsuario integer not null, 
estrelas int not null
); 

CREATE TABLE IF NOT EXISTS tbl_proposta(
token VARCHAR(40) PRIMARY KEY NOT NULL,
prestador VARCHAR(100) NOT NULL,
idFbPrestador VARCHAR(100) NOT NULL,
cliente VARCHAR(100) NOT NULL,
idFbCliente VARCHAR(100) NOT NULL,
statusProp ENUM("PENDENTE", "CONCLUÍDO", "RECUSADO", "ACEITO", "INACABADO") NOT NULL,
dtInicio DATE NOT NULL,
dtFim DATE NOT NULL,
valor FLOAT NOT NULL,
formaPag VARCHAR(30) NOT NULL,
localServ VARCHAR(500) NOT NULL,
descricao TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_fotosServices(
idFoto INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
idUser INTEGER NOT NULL,
url VARCHAR(500) NOT NULL
);

#drop table tbl_proposta;
select * from tbl_proposta;


#--------------- CHAVES ESTRANGEIRAS ---------------#

### tbl_profissional ###

ALTER TABLE tbl_profissional ADD CONSTRAINT FK_idProfissional 
FOREIGN KEY (idUsuario) REFERENCES tbl_usuario (id) on delete cascade; 

ALTER TABLE tbl_profissional ADD CONSTRAINT FK_idProfissao 
FOREIGN KEY (idProfissao) REFERENCES tbl_tipoServico (id); 

### tbl_avalicao ###

ALTER TABLE tbl_avaliacao ADD CONSTRAINT FK_idPro 
FOREIGN KEY (idProfissional) REFERENCES tbl_profissional (idProfissional) on delete cascade;

ALTER TABLE tbl_avaliacao ADD CONSTRAINT idUsuario 
FOREIGN KEY (idUsuario) REFERENCES tbl_usuario (id);

############# INDEX ###########
 
CREATE INDEX idx_users ON tbl_usuario(id);

CREATE INDEX idx_profissoes ON tbl_tipoServico(id);
 
############# INSERTS ##########
INSERT INTO tbl_usuario (id, nome, dtNascimento, rg, cpf, endCidade, endEstado, endRua, endBairro, endNum, senha, email, telCell, endCep, idFb, foto) VALUES 
(1, 'Gabrielle Luques', '2000-03-29', '381432402', '49677364677', 'Carapicuíba', 'SP', 'Rua Marconi', 'Vl Zette', '38', MD5('123456'), 'gabi@gmail.com',
'11951184349', '06332190', 'oER3PPYekAXoIfklFamUntpLcYi1', "https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/images%2Fmulher-orientemedio-0303.jpg?alt=media&token=d6275f2b-0f02-4180-ac44-840b2f613baf");

INSERT INTO tbl_usuario (id, nome, dtNascimento, rg, cpf, endCidade, endEstado, endRua, endBairro, endNum, senha, email, telCell, endCep, idFb, foto) VALUES 
(2, 'Flavio Alves', '2000-03-29', '381422402', '59677364677', 'Carapicuíba', 'SP', 'Rua Marconi', 'Vl Zette', '38', MD5('123456'), 'flavio@gmail.com',
'11951184549', '06332190', '8wyqSnA3RETHUMZZ8COQZPFAXc23', "https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/images%2F3253b880-9556-4823-8820-e21a017c620c?alt=media&token=531f0209-a69d-4c90-b5a4-7c2ef85b3ef1");

INSERT INTO tbl_usuario (id, nome, dtNascimento, rg, cpf, endCidade, endEstado, endRua, endBairro, endNum, senha, email, telCell, endCep, idFb, foto) VALUES 
(3, 'Talisson Luques', '2000-03-29', '383432402', '47677764677', 'Carapicuíba', 'SP','Rua Marconi', 'Vl Zette', '38', MD5('123456'), 'talis@gmail.com',
'11951184049', '06332190', '7UDK2owFkuanW9yeId7N6vuxzah2', "https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/images%2F5ab35863-fe64-4635-9c23-bcf00fe1975a?alt=media&token=62282adf-6c8e-4a39-865d-1225d5a21011");

INSERT INTO tbl_usuario (id, nome, dtNascimento, rg, cpf, endCidade, endEstado, endRua, endBairro, endNum, senha, email, telCell, endCep, idFb, foto) VALUES 
(4, 'Luiz Luques', '2000-03-29', '381422402', '59677374677', 'Carapicuíba', 'SP', 'Rua Marconi', 'Vl Zette', '38', MD5('123456'), 'luiz@gmail.com', '11951104549',
'06332190', '4G8kp12MIWdxJEU0pXv9hRHs7yV2', "https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/images%2FwillSmith.jpeg?alt=media&token=919bd79f-2bed-45a1-a20c-3d39463dea5f");

#delete from tbl_usuario where email = 'talisson.luques007@gmail.com';

INSERT INTO tbl_tipoServico (categoria, profissao) VALUES  
('Construção e Reparos', 'engenheiro civil'), 
('Construção e Reparos', 'arquiteto'), 
('Construção e Reparos', 'pedreiro'), 
('Construção e Reparos', 'ajudante de pedreiro'), 
('Construção e Reparos', 'pintor'), 
('Construção e Reparos', 'gesseiro'), 
('Construção e Reparos', 'carpinteiro'),  
('Construção e Reparos', 'eletricista'), 
('Construção e Reparos', 'vidraceiro'), 
('Construção e Reparos', 'encanador'), 
('Construção e Reparos', 'chaveiro'),
#SERVIÇOS DOMÉSTICOS
('Serviços Domésticos', 'faxineiro'), 
('Serviços Domésticos', 'babá'), 
('Serviços Domésticos', 'cuidador de idosos'), 
('Serviços Domésticos', 'jardineiro'), 
('Serviços Domésticos', 'cozinheiro'), 
('Serviços Domésticos', 'limpador de piscinas'), 
#ASSISTENCIA TÉCNICA
('Assistência Técnica', 'técnico em aparelhos eletrônicos'), 
('Assistência Técnica', 'técnico em redes'),
('Assistência Técnica', 'técnico em eletrodomésticos'),
('Assistência Técnica', 'instalador de ar condicionado'), 
#EVENTOS
('Eventos', 'fotógrafo'), 
('Eventos', 'montador'), 
('Eventos', 'segurança'), 
('Eventos', 'promotor'), 
('Eventos', 'vendedor de alimentos'), 
('Eventos', 'DJs'),
('Eventos', 'decoração'),
('Eventos', 'recepcionista'),
('Eventos', 'manobrista'),
('Eventos', 'confeiteiro'),
('Eventos', 'boleiro'),
('Eventos', 'alocador de brinquedos'),
#AULAS
('Aulas', 'professor de violão'), 
('Aulas', 'professor de guitarra'), 
('Aulas', 'professor de bateria'), 
('Aulas', 'professor de saxofone'), 
('Aulas', 'professor de teclado'), 
('Aulas', 'professor de piano'), 
('Aulas', 'professor de reforço escolar'), 
('Aulas', 'professor de educação especial'),
('Aulas', 'professor de canto'), 
('Aulas', 'professor de tecnologia da informação'), 
#CONSULTORIA
('Consultoria', 'consultor financeiro'), 
('Consultoria', 'consultor de marketing'), 
('Consultoria', 'consultor de recursos Humanos'), 
('Consultoria', 'consultor de vendas'), 
('Consultoria', 'consultor logistico'), 
#SAÚDE
('Saúde', 'ortopedista'), 
('Saúde', 'nutricionista'), 
('Saúde', 'fisioterapeuta'), 
('Saúde', 'psicologo'), 
('Saúde', 'esteticista'), 
('Saúde', 'professor de eduação física'),
('Saúde', 'bodybuilding coach'),
('Saúde', 'enfermeiro'),
('Saúde', 'fonoaudiólogo'),
('Saúde', 'dentista'),
('Saúde', 'quiropraxista'),
('Saúde', 'terapeuta'),
('Saúde', 'terapeuta ocupacional'),
('Saúde', 'musicoterapeuta'),
#MODA E BELEZA
('Moda e Beleza', 'estilista'),
('Moda e Beleza', 'cabeleireiro'),
('Moda e Beleza', 'figurinista'),
('Moda e Beleza', 'maquiador'),
('Moda e Beleza', 'manicure / pedicure'),
#TECNOLOGIA E DESIGN
('Tecnologia e Design', 'desenvolvedor front-end'), 
('Tecnologia e Design', 'desenvolvedor back-end'), 
('Tecnologia e Design', 'desenvolvedor de processamento de dados'), 
('Tecnologia e Design', 'desenvolvedor mobile'), 
('Tecnologia e Design', 'desenvolvedor web'), 
('Tecnologia e Design', 'desenvolvedor fullstack'), 
('Tecnologia e Design', 'administrador de rede'), 
('Tecnologia e Design', 'analista de BI'), 
('Tecnologia e Design', 'analista de infraestrutura'), 
('Tecnologia e Design', 'engenheiro de hardware'), 
('Tecnologia e Design', 'engenheiro de software'), 
('Tecnologia e Design', 'engenheiro de redes de comunicação'), 
('Tecnologia e Design', 'engenheiro de dados'), 
('Tecnologia e Design', 'engenheiro de redes'), 
('Tecnologia e Design', 'cientista de dados'), 
('Tecnologia e Design', 'editor de vídeos'), 
('Tecnologia e Design', 'editor de fotos'), 
('Tecnologia e Design', 'webdesigner'), 
('Tecnologia e Design', 'designer de games'), 
('Tecnologia e Design', 'designer de produtos'), 
#VEÍCULOS
('Veículos', 'mecânico'), 
('Veículos', 'borracheiro'), 
('Veículos', 'eletromecânico'), 
('Veículos', 'funileiro'), 
('Veículos', 'motorista particular'), 
('Veículos', 'carreteiro'), 
('Veículos', 'guincho'),
('Veículos', 'professor de direção');

###

## Talisson como profissional
INSERT INTO tbl_profissional (idProfissional, idUsuario, idProfissao, dtExperiencia) VALUES
(1, 3, 2, '2018-08-01');
## Gabi como profissional
INSERT INTO tbl_profissional (idProfissional, idUsuario, idProfissao, dtExperiencia) VALUES
(2, 1, 2, '2019-04-01');
## Luiz como profissional
INSERT INTO tbl_profissional (idProfissional, idUsuario, idProfissao, dtExperiencia) VALUES
(3, 4, 2, '2020-05-01');

###

## Avaliações de Luiz
INSERT INTO tbl_avaliacao (idProfissional, idUsuario, estrelas) VALUES
(3, 1, 5);
## Avaliações de Talisson
INSERT INTO tbl_avaliacao (idProfissional, idUsuario, estrelas) VALUES
(1, 1, 5);
INSERT INTO tbl_avaliacao (idProfissional, idUsuario, estrelas) VALUES
(1, 1, 4);
INSERT INTO tbl_avaliacao (idProfissional, idUsuario, estrelas) VALUES
(1, 1, 3);
INSERT INTO tbl_avaliacao (idProfissional, idUsuario, estrelas) VALUES
(1, 1, 4);
INSERT INTO tbl_avaliacao (idProfissional, idUsuario, estrelas) VALUES
(1, 1, 1);
INSERT INTO tbl_avaliacao (idProfissional, idUsuario, estrelas) VALUES
(1, 1, 5);

## INSERINDO FOTOS DE SERVIÇOS
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(1, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2FRAWI-ARQUITETURA-DESIGN-02-2-1920x1100.jpg?alt=media&token=3029f620-7b76-49e1-950d-0d7d704a0148');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(1, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F91091-conteudo-avancado-realidade-virtual-na-arquitetura-como-ela-vem-sendo-usada-650x590.jpg?alt=media&token=e1b5531e-7455-430d-a76e-088a4db8aebb');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(1, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F6354b228f593c6b11773be5bdb5f3aa4.jpg?alt=media&token=db9920e9-6322-4c3d-ac60-10c4f93ef023');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(1, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F1904_Ap_EP_sala_R0png_Page12.jpg?alt=media&token=67eaa417-cf51-4c6b-a2b6-5b36f96d7c15');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(1, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F170612_Studio_AG_023_Ricardo_Bassetti_4036%20(1).jpg?alt=media&token=34cdcf83-6913-4c74-8f85-e51434f2777e');

INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(2, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2FRAWI-ARQUITETURA-DESIGN-02-2-1920x1100.jpg?alt=media&token=3029f620-7b76-49e1-950d-0d7d704a0148');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(2, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F91091-conteudo-avancado-realidade-virtual-na-arquitetura-como-ela-vem-sendo-usada-650x590.jpg?alt=media&token=e1b5531e-7455-430d-a76e-088a4db8aebb');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(2, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F6354b228f593c6b11773be5bdb5f3aa4.jpg?alt=media&token=db9920e9-6322-4c3d-ac60-10c4f93ef023');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(2, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F1904_Ap_EP_sala_R0png_Page12.jpg?alt=media&token=67eaa417-cf51-4c6b-a2b6-5b36f96d7c15');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(2, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F170612_Studio_AG_023_Ricardo_Bassetti_4036%20(1).jpg?alt=media&token=34cdcf83-6913-4c74-8f85-e51434f2777e');

INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(3, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2FRAWI-ARQUITETURA-DESIGN-02-2-1920x1100.jpg?alt=media&token=3029f620-7b76-49e1-950d-0d7d704a0148');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(3, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F91091-conteudo-avancado-realidade-virtual-na-arquitetura-como-ela-vem-sendo-usada-650x590.jpg?alt=media&token=e1b5531e-7455-430d-a76e-088a4db8aebb');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(3, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F6354b228f593c6b11773be5bdb5f3aa4.jpg?alt=media&token=db9920e9-6322-4c3d-ac60-10c4f93ef023');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(3, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F1904_Ap_EP_sala_R0png_Page12.jpg?alt=media&token=67eaa417-cf51-4c6b-a2b6-5b36f96d7c15');
INSERT INTO tbl_fotosServices (idUser, url) VALUES 
(3, 'https://firebasestorage.googleapis.com/v0/b/faconapp-etec.appspot.com/o/fotosServices%2F170612_Studio_AG_023_Ricardo_Bassetti_4036%20(1).jpg?alt=media&token=34cdcf83-6913-4c74-8f85-e51434f2777e');

## SELECTS

#select * from tbl_usuario;
#select * from tbl_profissional;
#SELECT categoria, profissao from tbl_tipoServico where categoria = 'Construção e Reparos';
#select * from tbl_tipoServico where categoria = 'Construção e Reparos' and profissao like '%pedreiro%' order by id desc;
#select * from tbl_avaliacao where idProfissional = 1;

## SELECT LOGIN
#select u.id, u.nome, u.email, u.ativo, u.endBairro, u.endCep, u.endCidade, u.foto,
#u.endEstado, u.endNum, u.endRua, u.dtNascimento, u.telCell, u.telFixo, u.email,
#p.idProfissional, p.descricao, p.dtExperiencia, p.formacao, p.idProfissao, u.ativo,
#s.categoria, s.profissao,
#count(a.id) as qntAv, round(avg(a.estrelas)) as estrelas
#from tbl_usuario u
#left outer join tbl_profissional p on u.id = p.idUsuario 
#left outer join tbl_avaliacao a on p.idProfissional = a.idProfissional
#left outer join tbl_tipoServico s on s.id = p.idProfissao
#where email = 'talis@talis.com' and senha ='123456'
#group by u.id;


## SELECT BUSCA DE PROFISSIONAIS POR categoria/pesquisa 
#SELECT p.idProfissional, p.idUsuario, p.dtExperiencia, p.descricao, 
#u.nome, u.endCidade, u.endEstado, 
#s.profissao, s.categoria,
#ROUND(avg(a.estrelas)) AS estrelas, COUNT(a.id) AS qntAv
#FROM tbl_profissional p
#INNER JOIN tbl_usuario u ON u.id = p.idUsuario
#INNER JOIN tbl_tipoServico s ON s.id = p.idProfissao 
#LEFT OUTER JOIN tbl_avaliacao a on p.idProfissional = a.idProfissional
#WHERE s.categoria = 'Construção e Reparos' #AND p.idUsuario <> 3 	## <> do id do user logado (caso ele seja um profissional)
#group by p.idProfissional
#order by dtExperiencia asc
#;

#####
